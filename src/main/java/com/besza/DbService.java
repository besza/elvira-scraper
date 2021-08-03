package com.besza;

import org.jdbi.v3.core.Jdbi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ApplicationScoped
public class DbService {

    @Inject
    Jdbi jdbi;

    public void save(List<TimetableDTO> timetable) {
        var currentLocalDate = LocalDate.now();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var textDate = currentLocalDate.format(formatter);
        var template = "%s %s:00";
        jdbi.useHandle(handle -> {
            var batch = handle.prepareBatch("""
                insert into mav_timetable(origin, destination, planned_departure, planned_arrival, departure, arrival)
                values (:origin, :destination, :planned_departure, :planned_arrival, :departure, :arrival)
                """);
            for (TimetableDTO t : timetable) {
                batch.bind("origin", t.getOrigin())
                        .bind("destination", t.getDestination())
                        .bind("planned_departure", Timestamp.valueOf(String.format(template, textDate, t.getPlannedDeparture())))
                        .bind("planned_arrival", Timestamp.valueOf(String.format(template, textDate, t.getPlannedArrival())))
                        .bind("departure", t.getDeparture() != null
                                ? Timestamp.valueOf(String.format(template, textDate, t.getDeparture()))
                                : null)
                        .bind("arrival", t.getArrival() != null
                                ? Timestamp.valueOf(String.format(template, textDate, t.getArrival()))
                                : null)
                        .add();
            }
            batch.execute();
        });
    }

    public Map<String, Set<String>> findRoutes() {
        Map<String, Set<String>> routes = new HashMap<>();
        jdbi.withHandle(handle -> handle.createQuery("""
                    select distinct origin, destination
                    from mav_timetable
                    where planned_departure >= now() - interval '28 days'
                    """)
                .mapToMap(String.class)
                .list())
            .forEach(m -> routes.computeIfAbsent(m.get("origin"), key -> new HashSet<>()).add(m.get("destination")));
        return routes;
    }

    public List<TimetableBE> findByOriginAndDestination(String origin, String destination) {
        Objects.requireNonNull(origin);
        Objects.requireNonNull(destination);
        return jdbi.withHandle(handle -> handle.createQuery("""
                    select *
                    from mav_timetable
                    where (origin, destination) = (?, ?)
                      and planned_departure >= now() - interval '28 days'
                      and arrival is not null
                    order by planned_departure""")
                .bind(0, origin)
                .bind(1, destination)
                .mapTo(TimetableBE.class)
                // TODO: Do not star-select, leverage projections, throw out MapStruct
                .list());
    }
}
