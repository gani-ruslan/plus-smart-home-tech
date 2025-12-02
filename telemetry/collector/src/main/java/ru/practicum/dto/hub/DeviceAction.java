package ru.practicum.dto.hub;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.ActionType;

@Data
@FieldDefaults(level = PRIVATE)
public class DeviceAction {
    @NotBlank
    String sensorId;

    @NotNull
    ActionType type;

    private Integer value;
}
