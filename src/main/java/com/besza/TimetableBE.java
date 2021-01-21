package com.besza;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.time.Duration;

@Entity
@Table(name = "mav_timetable")
public class TimetableBE extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Long id;

    @Column(nullable = false)
    public String origin;

    @Column(name = "planned_departure", nullable = false)
    public Timestamp plannedDeparture;

    public Timestamp departure;

    @Column(nullable = false)
    public String destination;

    @Column(name = "planned_arrival", nullable = false)
    public Timestamp plannedArrival;

    public Timestamp arrival;

    @Transient
    public long delayMinutes;

    @PostLoad
    public void calculateDelay() {
        if (arrival != null) {
            delayMinutes = Duration.between(plannedArrival.toInstant(), arrival.toInstant()).toMinutes();
        }
    }
}
