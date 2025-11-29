package ru.practicum.dto.hub;

import static lombok.AccessLevel.PRIVATE;
import static ru.practicum.dto.types.HubEventType.DEVICE_ADDED;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.types.DeviceType;
import ru.practicum.dto.types.HubEventType;

@Data
@FieldDefaults(level = PRIVATE)
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    String id;

    @NotNull
    DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return DEVICE_ADDED;
    }
}
