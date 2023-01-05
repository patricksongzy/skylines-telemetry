package patricksongzy.skylines.processing.streaming.processing;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import patricksongzy.skylines.processing.data.StalingData;
import patricksongzy.skylines.processing.data.sensor.Heartbeat;

import java.time.Duration;

public class StalingProcessor<T> extends KeyedBroadcastProcessFunction<Long, T, Heartbeat, StalingData<T>> {
    private static final int MIN_FRAMES = 50;
    private static final Duration UPDATE_TIMEOUT = Duration.ofHours(1);
    private transient ValueStateDescriptor<Long> lastUpdateStateDescriptor;
    private transient ValueState<Long> lastUpdateState;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        lastUpdateStateDescriptor = new ValueStateDescriptor<>("lastUpdateState", Long.class);
        lastUpdateState = getRuntimeContext().getState(lastUpdateStateDescriptor);
    }

    @Override
    public void processElement(T telemetry, KeyedBroadcastProcessFunction<Long, T, Heartbeat, StalingData<T>>.ReadOnlyContext context, Collector<StalingData<T>> collector) throws Exception {
        lastUpdateState.update(context.timestamp());
        collector.collect(new StalingData<>(context.getCurrentKey(), telemetry));
    }

    @Override
    public void processBroadcastElement(Heartbeat heartbeat, KeyedBroadcastProcessFunction<Long, T, Heartbeat, StalingData<T>>.Context context, Collector<StalingData<T>> collector) throws Exception {
        context.applyToKeyedState(lastUpdateStateDescriptor, (key, lastUpdateTimestamp) -> {
            if (lastUpdateTimestamp.value() != null && (context.timestamp() - lastUpdateTimestamp.value()) > Math.max(UPDATE_TIMEOUT.toMillis(), MIN_FRAMES * heartbeat.getTimePerFrameMillis())) {
                collector.collect(new StalingData<>(key, null));
                lastUpdateTimestamp.clear();
            }
        });
    }
}
