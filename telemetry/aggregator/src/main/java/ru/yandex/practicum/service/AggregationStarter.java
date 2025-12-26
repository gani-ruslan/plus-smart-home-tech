package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.serializer.AvroSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final SnapshotAggregator snapshotAggregator;

    @Value("${aggregator.topics.sensors}")
    private String sensorsTopic;

    @Value("${aggregator.topics.snapshots}")
    private String snapshotsTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public void start() {
        log.info("Aggregator started. Subscribe to topic: {}", sensorsTopic);

        Properties consProps = new Properties();
        consProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consProps.put(ConsumerConfig.GROUP_ID_CONFIG, "aggregator-group");
        consProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.deserializer.SensorEventDeserializer");
        consProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Properties prodProps = new Properties();
        prodProps.put("bootstrap.servers", bootstrapServers);
        prodProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prodProps.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        try (KafkaConsumer<String, SensorEventAvro> consumer = new KafkaConsumer<>(consProps);
             KafkaProducer<String, byte[]> producer = new KafkaProducer<>(prodProps)) {

            consumer.subscribe(Collections.singletonList(sensorsTopic));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();

                    log.debug("Event: {} from HUB: {}", event.getId(), event.getHubId());

                    Optional<SensorsSnapshotAvro> updatedSnap = snapshotAggregator.updateState(event);

                    updatedSnap.ifPresent(snapshot -> {
                        byte[] payload = AvroSerializer.serialize(snapshot);
                        producer.send(new ProducerRecord<>(snapshotsTopic, snapshot.getHubId(), payload));
                        log.info("Updated HUB snapshot sent {} to topic {}", snapshot.getHubId(), snapshotsTopic);
                    });
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignore) {
            // Ignore exception if closing
        } catch (Exception e) {
            log.error("Event process error: ", e);
        } finally {
            log.info("Shutdown aggregator service.");
        }
    }
}
