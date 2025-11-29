package ru.practicum.dto.sensor;

import static lombok.AccessLevel.PRIVATE;
import static ru.practicum.dto.types.SensorEventType.CLIMATE_SENSOR_EVENT;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.SensorEventType;

@Data
@FieldDefaults(level = PRIVATE)
public class ClimateSensorEvent extends SensorEvent {
    @NotNull
    int temperatureC;

    @NotNull
    int humidity;

    @NotNull
    int co2Level;

    @Override
    public SensorEventType getType() {
        return CLIMATE_SENSOR_EVENT;
    }
}
