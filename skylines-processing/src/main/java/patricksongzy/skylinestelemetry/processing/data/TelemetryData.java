package patricksongzy.skylinestelemetry.processing.data;

import java.time.Instant;

public interface TelemetryData<T> {
    T getKey();

    void setKey(T key);

    Instant getTimestamp();

    void setTimestamp(Instant timestamp);
}
