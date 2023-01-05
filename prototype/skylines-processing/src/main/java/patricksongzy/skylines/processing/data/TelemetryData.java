package patricksongzy.skylines.processing.data;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

public abstract class TelemetryData {
    private long key;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX")
    private Instant timestamp;

    public TelemetryData() {
    }

    public TelemetryData(long key, Instant timestamp) {
        this.key = key;
        this.timestamp = timestamp;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().findAndRegisterModules().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
