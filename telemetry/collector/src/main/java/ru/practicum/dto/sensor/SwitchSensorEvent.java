package ru.practicum.dto.sensor;

import static lombok.AccessLevel.PRIVATE;
import static ru.practicum.dto.types.SensorEventType.SWITCH_SENSOR_EVENT;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.SensorEventType;

@Data
@FieldDefaults(level = PRIVATE)
public class SwitchSensorEvent extends SensorEvent {
    @NotNull
    boolean state;

    @Override
    public SensorEventType getType() {
        return SWITCH_SENSOR_EVENT;
    }
}
