package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@Component
public class ProtoToSensorEventMapper {

    public SensorEventAvro toAvro(SensorEventProto proto) {
        SensorEventAvro.Builder builder = SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        proto.getTimestamp().getSeconds(),
                        proto.getTimestamp().getNanos()
                ));
        return switch (proto.getPayloadCase()) {
            case MOTION_SENSOR_EVENT -> {
                MotionSensorProto m = proto.getMotionSensorEvent();
                yield builder.setPayload(MotionSensorAvro.newBuilder()
                                .setLinkQuality(m.getLinkQuality())
                                .setMotion(m.getMotion())
                                .setVoltage(m.getVoltage())
                                .build())
                        .build();
            }
            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorProto t = proto.getTemperatureSensorEvent();
                yield builder.setPayload(TemperatureSensorAvro.newBuilder()
                                .setTemperatureC(t.getTemperatureC())
                                .setTemperatureF(t.getTemperatureF())
                                .build())
                        .build();
            }
            case LIGHT_SENSOR_EVENT -> {
                LightSensorProto l = proto.getLightSensorEvent();
                yield builder.setPayload(LightSensorAvro.newBuilder()
                                .setLinkQuality(l.getLinkQuality())
                                .setLuminosity(l.getLuminosity())
                                .build())
                        .build();
            }
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorProto c = proto.getClimateSensorEvent();
                yield builder.setPayload(ClimateSensorAvro.newBuilder()
                                .setTemperatureC(c.getTemperatureC())
                                .setHumidity(c.getHumidity())
                                .setCo2Level(c.getCo2Level())
                                .build())
                        .build();
            }
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorProto s = proto.getSwitchSensorEvent();
                yield builder.setPayload(SwitchSensorAvro.newBuilder()
                                .setState(s.getState())
                                .build())
                        .build();
            }
            case PAYLOAD_NOT_SET -> builder.build();
        };
    }
}