package ru.practicum.dto.sensor;

import static lombok.AccessLevel.PRIVATE;
import static ru.practicum.dto.types.SensorEventType.LIGHT_SENSOR_EVENT;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.SensorEventType;

@Data
@FieldDefaults(level = PRIVATE)
public class LightSensorEvent extends SensorEvent {
    @NotNull
    int linkQuality;

    @NotNull
    int luminosity;

    @Override
    public SensorEventType getType() {
        return LIGHT_SENSOR_EVENT;
    }
}
