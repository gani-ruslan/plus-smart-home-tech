package ru.practicum.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.sensor.*;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Slf4j
@Component
public class SensorEventMapper {

    public SensorEventAvro toAvro(SensorEvent event) {
        log.debug("Start mapping SensorEvent type: {}", event.getType());

        SensorEventAvro.Builder builder = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp()
                );

        switch (event.getType()) {
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent lightEvent = (LightSensorEvent) event;
                log.debug("LIGHT_SENSOR_EVENT received. LinkQuality={}, Luminosity={}",
                        lightEvent.getLinkQuality(), lightEvent.getLuminosity());
                builder.setPayload(
                        LightSensorAvro.newBuilder()
                                .setLinkQuality(lightEvent.getLinkQuality())
                                .setLuminosity(lightEvent.getLuminosity())
                                .build()
                );
            }

            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent motionEvent = (MotionSensorEvent) event;
                log.debug("MOTION_SENSOR_EVENT received. LinkQuality={}, Motion={}, Voltage={}",
                        motionEvent.getLinkQuality(), motionEvent.isMotion(), motionEvent.getVoltage());
                builder.setPayload(
                        MotionSensorAvro.newBuilder()
                                .setLinkQuality(motionEvent.getLinkQuality())
                                .setMotion(motionEvent.isMotion())
                                .setVoltage(motionEvent.getVoltage())
                                .build()
                );
            }

            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent climateEvent = (ClimateSensorEvent) event;
                log.debug("CLIMATE_SENSOR_EVENT received. Temp={}, Humidity={}, CO2={}",
                        climateEvent.getTemperatureC(), climateEvent.getHumidity(), climateEvent.getCo2Level());
                builder.setPayload(
                        ClimateSensorAvro.newBuilder()
                                .setTemperatureC(climateEvent.getTemperatureC())
                                .setHumidity(climateEvent.getHumidity())
                                .setCo2Level(climateEvent.getCo2Level())
                                .build()
                );
            }

            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent switchEvent = (SwitchSensorEvent) event;
                log.debug("SWITCH_SENSOR_EVENT received. State={}", switchEvent.isState());
                builder.setPayload(
                        SwitchSensorAvro.newBuilder()
                                .setState(switchEvent.isState())
                                .build()
                );
            }

            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent tempEvent = (TemperatureSensorEvent) event;
                log.debug("TEMPERATURE_SENSOR_EVENT received. TempC={}, TempF={}",
                        tempEvent.getTemperatureC(), tempEvent.getTemperatureF());
                builder.setPayload(
                        TemperatureSensorAvro.newBuilder()
                                .setTemperatureC(tempEvent.getTemperatureC())
                                .setTemperatureF(tempEvent.getTemperatureF())
                                .build()
                );
            }
        }

        return builder.build();
    }
}