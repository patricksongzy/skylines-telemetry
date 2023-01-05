package patricksongzy.skylines.processing.streaming;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import patricksongzy.skylines.processing.data.StalingData;
import patricksongzy.skylines.processing.data.enriched.AmbulanceData;
import patricksongzy.skylines.processing.data.enriched.AmbulanceTripSegment;
import patricksongzy.skylines.processing.data.enriched.HospitalData;
import patricksongzy.skylines.processing.data.enriched.IncidentData;
import patricksongzy.skylines.processing.data.sensor.AmbulanceTelemetry;
import patricksongzy.skylines.processing.data.sensor.Heartbeat;
import patricksongzy.skylines.processing.data.sensor.HospitalTelemetry;
import patricksongzy.skylines.processing.data.sensor.IncidentTelemetry;
import patricksongzy.skylines.processing.streaming.processing.AmbulanceTelemetryProcessor;
import patricksongzy.skylines.processing.streaming.processing.HospitalTelemetryProcessor;
import patricksongzy.skylines.processing.streaming.processing.StalingProcessor;

import java.sql.Timestamp;

@Component
public class TelemetryStream {
    private static final JdbcExecutionOptions JDBC_EXECUTION_OPTIONS = new JdbcExecutionOptions.Builder()
            .withBatchSize(1000)
            .withBatchIntervalMs(200)
            .withMaxRetries(5)
            .build();
    private static final JdbcConnectionOptions JDBC_CONNECTION_OPTIONS = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
            .withUrl("jdbc:postgresql://timescale:5432/skylinestelemetry")
            .withDriverName("org.postgresql.Driver")
            .withUsername("postgres")
            .withPassword("password")
            .build();

    private final StreamExecutionEnvironment env;

    private final KafkaSource<Heartbeat> heartbeatSource;
    private final KafkaSource<AmbulanceTelemetry> ambulanceSource;
    private final KafkaSource<HospitalTelemetry> hospitalSource;
    private final KafkaSource<IncidentTelemetry> incidentSource;

    @Autowired
    public TelemetryStream(StreamExecutionEnvironment env, KafkaSource<Heartbeat> heartbeatSource,
            KafkaSource<AmbulanceTelemetry> ambulanceSource, KafkaSource<HospitalTelemetry> hospitalSource,
            KafkaSource<IncidentTelemetry> incidentSource) {
        this.env = env;
        this.heartbeatSource = heartbeatSource;
        this.ambulanceSource = ambulanceSource;
        this.hospitalSource = hospitalSource;
        this.incidentSource = incidentSource;
    }

    public void execute() throws Exception {
        BroadcastStream<Heartbeat> heartbeat = env.fromSource(heartbeatSource, Timestamps.getWatermarkStrategy(), "heartbeat")
                .broadcast(new MapStateDescriptor<>("heartbeat", BasicTypeInfo.STRING_TYPE_INFO, TypeInformation.of(new TypeHint<Heartbeat>() {})));

        DataStream<StalingData<AmbulanceTelemetry>> ambulanceTelemetry = env.fromSource(ambulanceSource, Timestamps.getWatermarkStrategy(), "ambulance_telemetry")
                .keyBy((KeySelector<AmbulanceTelemetry, Long>) data -> data.getKey()).connect(heartbeat)
                .process(new StalingProcessor<>()).returns(new TypeHint<StalingData<AmbulanceTelemetry>>() {});

        DataStream<IncidentTelemetry> incidentTelemetry = env.fromSource(incidentSource, Timestamps.getWatermarkStrategy(), "incident_telemetry");

        DataStream<StalingData<HospitalTelemetry>> hospitalTelemetry = env.fromSource(hospitalSource, Timestamps.getWatermarkStrategy(), "hospital_telemetry")
                .keyBy((KeySelector<HospitalTelemetry, Long>) data -> data.getKey()).connect(heartbeat)
                .process(new StalingProcessor<>()).returns(new TypeHint<StalingData<HospitalTelemetry>>() {});

        DataStream<HospitalData> hospitalData = hospitalTelemetry.keyBy(StalingData::getKey)
                .process(new HospitalTelemetryProcessor());

        hospitalData.addSink(JdbcSink.sink(
                "insert into hospital (id, x, y, ambulances) values (?, ?, ?, ?) on conflict (id) do update set x = excluded.x, y = excluded.y, ambulances = excluded.ambulances",
                (statement, telemetry) -> {
                    statement.setString(1, Timestamps.createKey(telemetry.getTrackTimestamp(), telemetry.getKey()));
                    statement.setDouble(2, telemetry.getPosition().f0);
                    statement.setDouble(3, telemetry.getPosition().f1);
                    statement.setInt(4, telemetry.getAmbulances());
                },
                JDBC_EXECUTION_OPTIONS,
                JDBC_CONNECTION_OPTIONS
        ));

        // TODO EDGE CASE WHERE TARGET IS HOSPITAL
        DataStream<IncidentData> incidentData = incidentTelemetry.map(IncidentData::new);
        incidentData.addSink(JdbcSink.sink(
                "insert into incident (id, time, enroute_time, citizen_id, building_id, priority, in_ambulance, x, y)\n" +
                        "values (?, ?, ?, ?, ?, ?, ?, ?, ?) on conflict (id) do update set\n" +
                        "time = excluded.time, enroute_time = excluded.enroute_time, \n" +
                        "citizen_id = excluded.citizen_id, building_id = excluded.building_id,\n" +
                        "priority = excluded.priority, in_ambulance = excluded.in_ambulance,\n" +
                        "x = excluded.x, y = excluded.y",
                (statement, incident) -> {
                    statement.setString(1, Timestamps.createKey(incident.getTimestamp(), incident.getCitizen()));
                    statement.setTimestamp(2, Timestamp.from(incident.getTimestamp()));
                    statement.setTimestamp(3, incident.getEnRouteTimestamp() == null ? null : Timestamp.from(incident.getEnRouteTimestamp()));
                    statement.setLong(4, incident.getCitizen());
                    statement.setLong(5, incident.getBuilding());
                    statement.setLong(6, incident.getPriority());
                    statement.setBoolean(7, incident.getIsInAmbulance());
                    statement.setDouble(8, incident.getLocation().f0);
                    statement.setDouble(9, incident.getLocation().f1);
                },
                JDBC_EXECUTION_OPTIONS,
                JDBC_CONNECTION_OPTIONS
        ));

        SingleOutputStreamOperator<AmbulanceData> ambulanceData = ambulanceTelemetry.keyBy(StalingData::getKey).process(new AmbulanceTelemetryProcessor());
        ambulanceData.addSink(JdbcSink.sink(
                "insert into ambulance_data (time, ambulance_id, speed, x, y) values (?, ?, ?, ?, ?)",
                (statement, data) -> {
                    statement.setTimestamp(1, Timestamp.from(data.getTimestamp()));
                    statement.setLong(2, data.getKey());
                    statement.setDouble(3, data.getSpeed());
                    statement.setDouble(4, data.getPosition().f0);
                    statement.setDouble(5, data.getPosition().f1);
                },
                JDBC_EXECUTION_OPTIONS,
                JDBC_CONNECTION_OPTIONS
        ));

        DataStream<AmbulanceTripSegment> segmentData = ambulanceData.getSideOutput(AmbulanceTelemetryProcessor.TRIP_SEGMENT_TAG);
        segmentData.addSink(JdbcSink.sink(
                "insert into ambulance_trip_segment (time, trip_id, ambulance_id, state) values (?, ?, ?, ?)",
                (statement, segment) -> {
                    statement.setTimestamp(1, Timestamp.from(segment.getTimestamp()));
                    statement.setString(2, Timestamps.createKey(segment.getStartTimestamp(), segment.getKey()));
                    statement.setLong(3, segment.getAmbulanceKey());
                    statement.setString(4, segment.getState().toString());
                },
                JDBC_EXECUTION_OPTIONS,
                JDBC_CONNECTION_OPTIONS
        ));

        env.execute();
    }
}
