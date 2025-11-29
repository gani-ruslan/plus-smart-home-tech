package ru.practicum.dto.hub;

import static lombok.AccessLevel.PRIVATE;
import static ru.practicum.dto.types.HubEventType.SCENARIO_REMOVED;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.HubEventType;

@Data
@FieldDefaults(level = PRIVATE)
public class ScenarioRemovedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3)
    String name;

    @Override
    public HubEventType getType() {
        return SCENARIO_REMOVED;
    }
}
