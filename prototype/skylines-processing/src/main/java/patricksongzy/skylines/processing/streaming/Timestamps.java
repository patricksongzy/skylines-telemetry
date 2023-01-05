package patricksongzy.skylines.processing.streaming;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import patricksongzy.skylines.processing.data.TelemetryData;

import java.time.Duration;
import java.time.Instant;

public class Timestamps {
    private static final Duration MAX_OUT_OF_ORDERNESS = Duration.ofMillis(100);

    public static String createKey(Instant trackTimestamp, long id) {
        return String.format("%s-%d", trackTimestamp.toString(), id);
    }

    public static <T extends TelemetryData> WatermarkStrategy<T> getWatermarkStrategy() {
        return WatermarkStrategy.<T>forBoundedOutOfOrderness(MAX_OUT_OF_ORDERNESS).withTimestampAssigner((event, timestamp) -> event.getTimestamp().toEpochMilli()).withIdleness(Duration.ofSeconds(1));
    }
}
