package patricksongzy.skylinestelemetry.processing.stream;

import java.lang.reflect.Type;

public interface TelemetryKafkaStream {
    String getTopic();
    Type getType();
}
