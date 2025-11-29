package ru.practicum.dto.hub;

import static lombok.AccessLevel.PRIVATE;
import static ru.practicum.dto.types.HubEventType.DEVICE_REMOVED;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.HubEventType;

@Data
@FieldDefaults(level = PRIVATE)
public class DeviceRemovedEvent extends HubEvent {
    @NotBlank
    String id;

    @Override
    public HubEventType getType() {
        return DEVICE_REMOVED;
    }
}
