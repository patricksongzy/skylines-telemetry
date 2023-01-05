package patricksongzy.skylines.processing.data.serialization;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import patricksongzy.skylines.processing.data.TelemetryData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonKeyValueDeserializer<T extends TelemetryData> implements KafkaRecordDeserializationSchema<T> {
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private Class<T> genericType;

    public JsonKeyValueDeserializer() {}

    public JsonKeyValueDeserializer(Class<T> genericType) {
        this.genericType = genericType;
    }

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<T> collector) throws IOException {
        String key = new String(consumerRecord.key(), StandardCharsets.UTF_8);
        String value = new String(consumerRecord.value(), StandardCharsets.UTF_8);
        T telemetry = mapper.readValue(value, genericType);
        telemetry.setKey(Integer.parseInt(key));
        collector.collect(telemetry);
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(genericType);
    }

    public void setGenericType(Class<T> genericType) {
        this.genericType = genericType;
    }
}
