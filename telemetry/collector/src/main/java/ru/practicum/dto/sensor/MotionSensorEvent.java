package ru.practicum.dto.sensor;

import static lombok.AccessLevel.PRIVATE;
import static ru.practicum.dto.types.SensorEventType.MOTION_SENSOR_EVENT;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.SensorEventType;

@Data
@FieldDefaults(level = PRIVATE)
public class MotionSensorEvent extends SensorEvent{
    @NotNull
    int linkQuality;

    @NotNull
    boolean motion;

    @NotNull
    private Integer voltage;

    @Override
    public SensorEventType getType() {
        return MOTION_SENSOR_EVENT;
    }
}
