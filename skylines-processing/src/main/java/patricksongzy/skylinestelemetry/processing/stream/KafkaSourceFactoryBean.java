package patricksongzy.skylinestelemetry.processing.stream;

import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.springframework.beans.factory.FactoryBean;

public class KafkaSourceFactoryBean<T> implements FactoryBean<KafkaSource<T>> {
    private final String topics;
    private OffsetsInitializer offsetsInitializer;
    private KafkaRecordDeserializationSchema<T> deserializer;

    public KafkaSourceFactoryBean(String topics, OffsetsInitializer offsetsInitializer, KafkaRecordDeserializationSchema<T> deserializer) {
        this.topics = topics;
    }

    @Override
    public KafkaSource<T> getObject() throws Exception {
        return KafkaSource.<T>builder().setBootstrapServers("localhost:29092")
                .setGroupId("skylines-processing")
                .setTopics(topics)
                .setStartingOffsets(offsetsInitializer)
                .setDeserializer(deserializer)
                .build();
    }

    @Override
    public Class<?> getObjectType() {
        return KafkaSource.class;
    }
}
