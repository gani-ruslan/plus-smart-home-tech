package ru.practicum.dto.sensor;

import static lombok.AccessLevel.PRIVATE;
import static ru.practicum.dto.types.SensorEventType.TEMPERATURE_SENSOR_EVENT;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.SensorEventType;

@Data
@FieldDefaults(level = PRIVATE)
public  class TemperatureSensorEvent extends SensorEvent {
    @NotNull
    int temperatureC;

    @NotNull
    int temperatureF;

    @Override
    public SensorEventType getType() {
        return TEMPERATURE_SENSOR_EVENT;
    }
}
