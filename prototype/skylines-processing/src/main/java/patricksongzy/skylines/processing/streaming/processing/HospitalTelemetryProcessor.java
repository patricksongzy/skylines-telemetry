package patricksongzy.skylines.processing.streaming.processing;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import patricksongzy.skylines.processing.data.StalingData;
import patricksongzy.skylines.processing.data.enriched.HospitalData;
import patricksongzy.skylines.processing.data.sensor.HospitalTelemetry;

import java.time.Instant;

public class HospitalTelemetryProcessor extends KeyedProcessFunction<Long, StalingData<HospitalTelemetry>, HospitalData> {
    private ValueState<HospitalData> cachedTelemetry;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        cachedTelemetry = getRuntimeContext().getState(new ValueStateDescriptor<>("cachedTelemetry", HospitalData.class));
    }

    @Override
    public void processElement(StalingData<HospitalTelemetry> stalingTelemetry, KeyedProcessFunction<Long, StalingData<HospitalTelemetry>, HospitalData>.Context context,
            Collector<HospitalData> collector) throws Exception {
        if (stalingTelemetry.getData() != null) {
            HospitalTelemetry hospitalTelemetry = stalingTelemetry.getData();
            if (cachedTelemetry.value() == null) {
                cachedTelemetry.update(new HospitalData(hospitalTelemetry, hospitalTelemetry.getTimestamp()));
                collector.collect(cachedTelemetry.value());
            } else if (cachedTelemetry.value().hasChanged(hospitalTelemetry)) {
                cachedTelemetry.update(new HospitalData(hospitalTelemetry, cachedTelemetry.value().getTrackTimestamp()));
                collector.collect(cachedTelemetry.value());
            }
        } else if (cachedTelemetry.value() != null) {
            collector.collect(new HospitalData(new HospitalTelemetry(context.getCurrentKey(), Instant.ofEpochMilli(context.timestamp()), 0),
                    cachedTelemetry.value().getTrackTimestamp()));
            cachedTelemetry.clear();
        }
    }
}
