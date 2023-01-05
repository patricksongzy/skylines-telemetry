package patricksongzy.skylines.processing.data;

import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;

public abstract class KafkaSourceFactoryBean<T> implements FactoryBean<KafkaSource<T>> {
    @Value("${BOOTSTRAP_SERVERS:kafka:29092}")
    private String bootstrapServers;
    private String groupId = "skylines-telemetry";

    @Override
    public KafkaSource<T> getObject() {
        return KafkaSource.<T>builder()
                        .setBootstrapServers(bootstrapServers)
                        .setGroupId(groupId)
                        .setTopics(getTopics())
                        .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST))
                        .setDeserializer(getDeserializer())
                        .build();
    }

    @Override
    public Class<?> getObjectType() {
        return KafkaSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public abstract String[] getTopics();

    public abstract KafkaRecordDeserializationSchema<T> getDeserializer();
}
