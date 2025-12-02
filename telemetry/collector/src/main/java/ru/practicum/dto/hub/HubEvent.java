package ru.practicum.dto.hub;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.HubEventType;
import java.time.Instant;

@JsonTypeInfo(
    property = "type",
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
    @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
    @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
    @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})
@Data
@FieldDefaults(level = PRIVATE)
public abstract class HubEvent {
    @NotBlank
    String hubId;

    @NotNull
    Instant timestamp = Instant.now();

    @NotNull
    public abstract HubEventType getType();
}
