package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.hub.HubEvent;
import ru.practicum.dto.sensor.SensorEvent;
import ru.practicum.mapper.HubEventMapper;
import ru.practicum.mapper.SensorEventMapper;
import ru.practicum.service.CollectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class CollectorController {
    private final SensorEventMapper sensorMapper;
    private final HubEventMapper hubMapper;
    private final CollectorService service;

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.debug("sensorEvent received: {}", event);
        service.sendSensorEvent(sensorMapper.toAvro(event));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.debug("hubEvent received: {}", event);
        service.sendHubEvent(hubMapper.toAvro(event));
        return ResponseEntity.ok().build();
    }
}