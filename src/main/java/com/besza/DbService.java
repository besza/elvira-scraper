package com.besza;

import io.quarkus.panache.common.Sort;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public List<TimetableBE> findAll() {
        return TimetableBE.listAll(Sort.descending("plannedDeparture"));
    }
}
