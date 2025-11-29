package ru.practicum.dto.hub;

import static lombok.AccessLevel.PRIVATE;
import static ru.practicum.dto.types.HubEventType.SCENARIO_ADDED;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.HubEventType;
import java.util.List;

@Data
@FieldDefaults(level = PRIVATE)
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3)
    String name;

    @Valid
    @NotEmpty
    List<ScenarioCondition> conditions;

    @Valid
    @NotEmpty
    List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return SCENARIO_ADDED;
    }
}
