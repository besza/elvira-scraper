package com.besza;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"origin, plannedDeparture, departure, destination, plannedArrival, arrival, delayMinutes"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TimetableDTO {
    private String origin;
    private String plannedDeparture;
    private String departure;
    private String destination;
    private String plannedArrival;
    private String arrival;
    private Long delayMinutes;

    public TimetableDTO() {}

    public TimetableDTO(String[] arr) {
        if (arr == null || arr.length < 6) {
            throw new IllegalArgumentException("Array should have 6 elements");
        }
        this.origin = arr[0];
        this.plannedDeparture = arr[1];
        this.departure = arr[2];
        this.destination = arr[3];
        this.plannedArrival = arr[4];
        this.arrival = arr[5];
    }

    public String getOrigin() {
        return origin;
    }

    public String getPlannedDeparture() {
        return plannedDeparture;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public String getPlannedArrival() {
        return plannedArrival;
    }

    public String getArrival() {
        return arrival;
    }

    public Long getDelayMinutes() {
        return delayMinutes;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setPlannedDeparture(String plannedDeparture) {
        this.plannedDeparture = plannedDeparture;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setPlannedArrival(String plannedArrival) {
        this.plannedArrival = plannedArrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public void setDelayMinutes(long delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    @Override
    public String toString() {
        return "TimetableDTO{origin: %s, destination: %s, plannedDeparture: %s}"
                .formatted(origin, destination, plannedDeparture);
    }
}
