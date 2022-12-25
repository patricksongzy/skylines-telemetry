package patricksongzy.skylinestelemetry.processing.stream;

import java.lang.reflect.Type;

public interface KafkaTelemetryStream {
    String getTopic();

    Type getType();
}
