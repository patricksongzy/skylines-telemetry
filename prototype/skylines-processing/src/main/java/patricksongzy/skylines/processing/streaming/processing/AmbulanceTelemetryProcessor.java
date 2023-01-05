package patricksongzy.skylines.processing.streaming.processing;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import patricksongzy.skylines.processing.data.StalingData;
import patricksongzy.skylines.processing.data.enriched.AmbulanceData;
import patricksongzy.skylines.processing.data.enriched.AmbulanceTripSegment;
import patricksongzy.skylines.processing.data.sensor.AmbulanceTelemetry;

import java.time.Instant;

public class AmbulanceTelemetryProcessor extends KeyedProcessFunction<Long, StalingData<AmbulanceTelemetry>, AmbulanceData> {
    public static final OutputTag<AmbulanceTripSegment> TRIP_SEGMENT_TAG = new OutputTag<AmbulanceTripSegment>("ambulance_trip_segments") {};
    private transient ValueState<Instant> trackTime;
    private transient ValueState<AmbulanceTelemetry.AmbulanceState> cachedState;
    private transient ValueState<Boolean> isArriving;
    private transient ValueState<AmbulanceTripSegment> tripSegment;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        trackTime = getRuntimeContext().getState(new ValueStateDescriptor<>("trackTime", Instant.class));
        cachedState = getRuntimeContext().getState(new ValueStateDescriptor<>("cachedState", AmbulanceTelemetry.AmbulanceState.class));
        isArriving = getRuntimeContext().getState(new ValueStateDescriptor<>("isArriving", Boolean.class));
        tripSegment = getRuntimeContext().getState(new ValueStateDescriptor<>("tripSegment", AmbulanceTripSegment.class));
    }

    @Override
    public void processElement(StalingData<AmbulanceTelemetry> stalingTelemetry,
            KeyedProcessFunction<Long, StalingData<AmbulanceTelemetry>, AmbulanceData>.Context context,
            Collector<AmbulanceData> collector) throws Exception {
        if (stalingTelemetry.getData() == null) {
            // if the trip has already been ended, then do not end it again
            if (tripSegment.value() != null) {
                endTrip(context);
                clearState();
            }

            return;
        }

        AmbulanceTelemetry telemetry = stalingTelemetry.getData();
        long patient;
        if (telemetry.getPatients().length == 1) {
            patient = telemetry.getPatients()[0];
        } else if (tripSegment.value() != null) {
            patient = tripSegment.value().getPatient();
        } else {
            return;
        }

        if (tripSegment.value() == null) {
            // start new trip, no existing trip
            Instant timestamp = Instant.ofEpochMilli(context.timestamp());
            tripSegment.update(new AmbulanceTripSegment(timestamp, telemetry.getKey(), telemetry.getState(), patient, timestamp));
            context.output(TRIP_SEGMENT_TAG, tripSegment.value());
        } else if (cachedState.value() != telemetry.getState()) {
            AmbulanceTripSegment currentTrip = tripSegment.value();
            if (telemetry.getState() == AmbulanceTelemetry.AmbulanceState.EMERGENCY) {
                if (tripSegment.value() != null) {
                    endTrip(context);
                }

                clearState();

                // start new trip, transitioned from empty or non-emergency to emergency state
                Instant timestamp = Instant.ofEpochMilli(context.timestamp());
                tripSegment.update(new AmbulanceTripSegment(timestamp, telemetry.getKey(), telemetry.getState(), patient, timestamp));
                context.output(TRIP_SEGMENT_TAG, tripSegment.value());
            } else {
                // create trip segment, change in state from cached state
                tripSegment.update(new AmbulanceTripSegment(currentTrip.getKey(), Instant.ofEpochMilli(context.timestamp()),
                        telemetry.getKey(), telemetry.getState(), currentTrip.getPatient(), currentTrip.getStartTimestamp()));
                context.output(TRIP_SEGMENT_TAG, tripSegment.value());
            }
        }

        if (trackTime.value() == null) {
            trackTime.update(telemetry.getTimestamp());
        }

        collector.collect(new AmbulanceData(telemetry, trackTime.value()));
        cachedState.update(telemetry.getState());
        isArriving.update(telemetry.getArriving());
    }

    private void clearState() {
        trackTime.clear();
        cachedState.clear();
        isArriving.clear();
        tripSegment.clear();
    }

    private <K, I, O> void endTrip(KeyedProcessFunction<K, I, O>.Context context) throws Exception {
        AmbulanceTelemetry.AmbulanceState state =
                AmbulanceTelemetry.AmbulanceState.isReturning(cachedState.value()) && isArriving != null &&
                        isArriving.value() ? AmbulanceTelemetry.AmbulanceState.DESTROYED
                        : AmbulanceTelemetry.AmbulanceState.STALE;
        context.output(TRIP_SEGMENT_TAG, new AmbulanceTripSegment(tripSegment.value().getKey(), Instant.ofEpochMilli(context.timestamp()),
                tripSegment.value().getAmbulanceKey(), state, tripSegment.value().getPatient(), tripSegment.value().getStartTimestamp()));
    }
}
