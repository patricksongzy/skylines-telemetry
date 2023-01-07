package patricksongzy.skylinestelemetry.processing;

import jnr.ffi.*;
import jnr.ffi.Runtime;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.types.size_t;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import patricksongzy.skylinestelemetry.processing.data.Complex;
import patricksongzy.skylinestelemetry.processing.data.transfer.CorrelatedTransfer;
import patricksongzy.skylinestelemetry.processing.data.transfer.EnrichedTransfer;
import patricksongzy.skylinestelemetry.processing.data.transfer.EnrichedTransferData;
import patricksongzy.skylinestelemetry.processing.data.vehicle.EnrichedVehicle;
import patricksongzy.skylinestelemetry.processing.data.vehicle.EnrichedVehicleData;
import patricksongzy.skylinestelemetry.processing.skylines.TransferReason;
import patricksongzy.skylinestelemetry.processing.skylines.VehicleCategory;
import patricksongzy.skylinestelemetry.processing.skylines.VehicleFlag;
import patricksongzy.skylinestelemetry.processing.stream.FourierWindowFunction;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SkylinesProcessing {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // begin experimental code
        // the code below is for experimentation purposes only
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        WatermarkStrategy<ObjectNode> watermarkStrategy = WatermarkStrategy.<ObjectNode>forBoundedOutOfOrderness(Duration.ofMinutes(1))
                .withTimestampAssigner((event, timestamp) -> Instant.parse(event.get("Timestamp").asText()).toEpochMilli()).withIdleness(Duration.ofSeconds(1));

        KafkaSource<ObjectNode> citySource = getSource("in.telemetry.city");
        DataStream<ObjectNode> cityStream = env.fromSource(citySource, watermarkStrategy, "in.telemetry.city");
        DataStream<Long> deathsStream = cityStream.map(c -> c.get("Deaths").asLong());
        // this is actually pretty bad, since the window is really large and the slide is small
        // an iterative DFT or similar would be a smarter idea, as this would allow us to reuse our computations
        // but because this is for experimentation, we will just use an RFFT for now
        DataStream<Complex[]> deathsFrequencyDomain = deathsStream.windowAll(SlidingEventTimeWindows.of(Time.days(3650), Time.days(365))).apply(new FourierWindowFunction());

        // cim lifespan is about 6 years
        final int CIM_LIFESPAN = 6;

        // just about everything in this game is considered a material
        // we break problems down into:
        // * deficits (services, goods, raw materials, processed materials, workers)
        // * surpluses (goods, workers, pollution)
        // we will treat bodies differently, due to a phenomenon known as death waves
        // * there can either be an actual deficit of the service, meaning within parameters, the service is still stretched thin
        // * or there can be a large death wave that couldn't reasonably be handled
        // * we will also try to detect imbalances in the age pyramid, as it is at this stage for which this problem can be averted
        // certain problems may lead to others
        // * just about every problem leads to traffic
        // * garbage and pollution lead to sickness which leads to death which leads to abandonment which leads to potential outages, loss of land value, more abandonment, etc.
        //   * each of these problems is addressed by services, but this can actually be detrimental as this also causes traffic
        // * raw material and processed material deficits lead to imports which reduce margins, increased traffic and delivery problems (which anyway are a part of the deficit)
        // the game despawns vehicles if they take too long to reach their destination, in the case of cargo, teleporting the cargo
        // * no special consideration is given to this problem, as taking too long implies congestion issues

        KafkaSource<ObjectNode> transferSource = getSource("in.telemetry.transfer");
        KafkaSource<ObjectNode> garbageSource = getSource("in.telemetry.vehicle.garbage");
        KafkaSource<ObjectNode> buildingSource = getSource("in.telemetry.building");
        DataStream<ObjectNode> transferStream = env.fromSource(transferSource, watermarkStrategy, "in.telemetry.transfer");
        DataStream<ObjectNode> garbageStream = env.fromSource(garbageSource, watermarkStrategy, "in.telemetry.vehicle.garbage");
        DataStream<ObjectNode> buildingStream = env.fromSource(buildingSource, watermarkStrategy, "in.telemetry.building");
        DataStream<EnrichedTransfer> enrichedTransfers = transferStream.map(t -> {
            EnrichedTransfer et = new EnrichedTransferData();
            et.setTimestamp(Instant.parse(t.get("Timestamp").asText()));
            et.setAmount(t.get("Amount").asInt());
            et.setBuilding((short) t.get("Building").asInt());
            et.setCitizen(t.get("Citizen").asInt());
            et.setIsActive(t.get("Active").asBoolean());
            et.setPriority(t.get("Priority").asInt());
            et.setVehicle((short) t.get("Vehicle").asInt());
            et.setReason(TransferReason.getTransferReason(t.get("Reason").asInt()));
            et.setPositionNorthward(t.get("PositionNorthward").asInt());
            et.setPositionEastward(t.get("PositionEastward").asInt());
            return et;
        });
        DataStream<EnrichedVehicle> enrichedGarbage = garbageStream.map(v -> {
            EnrichedVehicle ev = new EnrichedVehicleData();
            ev.setTimestamp(Instant.parse(v.get("Timestamp").asText()));
            ev.setSpeed(v.get("Speed").floatValue());
            ev.setPositionEastward(v.get("PositionEastward").floatValue());
            ev.setPositionNorthward(v.get("PositionNorthward").floatValue());
            ev.setSource(v.get("Source").intValue());
            ev.setTarget(v.get("Target").intValue());
            ev.setVehicleCategories(VehicleCategory.getVehicleCategories(v.get("VehicleCategory").longValue()));
            ev.setVehicleFlags(VehicleFlag.getVehicleFlags(v.get("Flags").longValue()));
            return ev;
        });

        DataStream<CorrelatedTransfer> correlatedGarbage = enrichedTransfers.filter(t -> t.getReason() ==
                        TransferReason.GARBAGE)
                .coGroup(enrichedGarbage).where(EnrichedTransfer::getBuilding).equalTo(EnrichedVehicle::getTarget)
                .window(TumblingEventTimeWindows.of(Time.minutes(1))).apply((CoGroupFunction<EnrichedTransfer, EnrichedVehicle, CorrelatedTransfer>) (first, second, out) -> {
                }, TypeInformation.of(CorrelatedTransfer.class));
        // service deficit with threshold of 20 units of piled up garbage
        DataStream<CorrelatedTransfer> garbagePileups = correlatedGarbage.filter(g -> g.getVehicle() == null ||
                g.getTransfer().getAmount() - g.getVehicle().getCapacity() > 20);

        DataStream<CorrelatedTransfer> unfulfilledGarbage = garbagePileups.filter(g -> g.getVehicle() == null);
        DataStream<CorrelatedTransfer> lateGarbage = garbagePileups.filter(g -> g.getVehicle() != null);
    }

    /**
     * For experimentation purposes, creates a source from the topic deserializing as a JSON object
     */
    private static KafkaSource<ObjectNode> getSource(final String topic) {
        return KafkaSource.<ObjectNode>builder().setBootstrapServers("localhost:9092")
                .setTopics(topic)
                .setGroupId("skylines-processing")
                .setDeserializer(new KafkaRecordDeserializationSchema<ObjectNode>() {
                    @Override
                    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<ObjectNode> out) throws IOException {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode value = (ObjectNode) mapper.reader().readTree(record.value());
                            value.set("Key", mapper.reader().readTree(record.key()).get("Key"));
                            out.collect(value);
                        } catch (Exception ignored) {}
                    }

                    @Override
                    public TypeInformation<ObjectNode> getProducedType() {
                        return TypeInformation.of(ObjectNode.class);
                    }
                })
                .build();
    }
}
