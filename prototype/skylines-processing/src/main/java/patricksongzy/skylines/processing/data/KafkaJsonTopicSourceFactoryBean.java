package patricksongzy.skylines.processing.data;

import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import patricksongzy.skylines.processing.data.serialization.JsonKeyValueDeserializer;

public class KafkaJsonTopicSourceFactoryBean<T extends TelemetryData> extends KafkaSourceFactoryBean<T> {
    private String topic;
    private Class<T> genericType;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Class<T> getGenericType() {
        return genericType;
    }

    public void setGenericType(Class<T> genericType) {
        this.genericType = genericType;
    }

    @Override
    public String[] getTopics() {
        return new String[] { topic };
    }

    @Override
    public KafkaRecordDeserializationSchema<T> getDeserializer() {
        return new JsonKeyValueDeserializer<>(genericType);
    }
}
