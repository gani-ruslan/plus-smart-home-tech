package ru.practicum.dto.hub;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
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
