package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    @Query("""
    SELECT s FROM Scenario s
    LEFT JOIN FETCH s.conditions c
    LEFT JOIN FETCH s.actions a
    LEFT JOIN FETCH c.sensor
    LEFT JOIN FETCH a.sensor
    LEFT JOIN FETCH c.condition
    LEFT JOIN FETCH a.action
    WHERE s.hubId = :hubId
""")
    List<Scenario> findByHubId(@Param("hubId") String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);
}
