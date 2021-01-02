package com.besza;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.time.Duration;

@Entity
@Table(name = "mav_timetable")
@JsonPropertyOrder(value = {"origin, plannedDeparture, departure, destination, plannedArrival, arrival, delay"})
public class TimetableBE extends PanacheEntity {

    @Column(nullable = false)
    public String origin;

    @Column(name = "planned_departure", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
    public Timestamp plannedDeparture;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
    public Timestamp departure;

    @Column(nullable = false)
    public String destination;

    @Column(name = "planned_arrival", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
    public Timestamp plannedArrival;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
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
