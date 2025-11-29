package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.serializer.AvroSerializer;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorService {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${collector.topics.sensors}")
    private String sensorsTopic;

    @Value("${collector.topics.hubs}")
    private String hubsTopic;

    public void sendSensorEvent(SensorEventAvro event) {
        byte[] payload = AvroSerializer.serialize(event);
        kafkaTemplate.send(sensorsTopic, event.getId(), payload);
    }

    public void sendHubEvent(HubEventAvro event) {
        byte[] payload = AvroSerializer.serialize(event);
        kafkaTemplate.send(hubsTopic, event.getHubId(), payload);
    }
}