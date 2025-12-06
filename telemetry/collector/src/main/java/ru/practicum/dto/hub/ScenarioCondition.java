package ru.practicum.dto.hub;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.ConditionOperation;
import ru.practicum.dto.types.ConditionType;

@Data
@FieldDefaults(level = PRIVATE)
public class ScenarioCondition {
    @NotBlank
    String sensorId;

    @NotNull
    ConditionType type;

    @NotNull
    ConditionOperation operation;

    Integer value;
}
