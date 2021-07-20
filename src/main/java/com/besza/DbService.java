package com.besza;

import io.quarkus.panache.common.Sort;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@RequestScoped
@Transactional
public class DbService {

    public void save(List<TimetableDTO> timetable) {
        var currentLocalDate = LocalDate.now();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var textDate = currentLocalDate.format(formatter);
        var template = "%s %s:00";
        for (TimetableDTO t : timetable) {
            var entity = new TimetableBE();
            entity.origin = t.getOrigin();
            entity.destination = t.getDestination();
            entity.plannedDeparture = Timestamp.valueOf(String.format(template, textDate, t.getPlannedDeparture()));
            entity.plannedArrival = Timestamp.valueOf(String.format(template, textDate, t.getPlannedArrival()));
            if (t.getDeparture() != null) {
                entity.departure = Timestamp.valueOf(String.format(template, textDate, t.getDeparture()));
            }
            if (t.getArrival() != null) {
                entity.arrival = Timestamp.valueOf(String.format(template, textDate, t.getArrival()));
            }
            entity.persist();
        }
    }

    public List<TimetableBE> findRecent() {
        return TimetableBE.find("plannedDeparture >= ?1",
                Timestamp.from(Instant.now().minus(28, ChronoUnit.DAYS))).list();
    }

    public List<TimetableBE> findByOriginAndDestination(String origin, String destination) {
        Objects.requireNonNull(origin);
        Objects.requireNonNull(destination);
        return TimetableBE.find("origin = ?1 and destination = ?2 and plannedDeparture >= ?3 and arrival is not null",
                Sort.by("plannedDeparture"),
                origin, destination, Timestamp.from(Instant.now().minus(28, ChronoUnit.DAYS))).list();
    }
}
