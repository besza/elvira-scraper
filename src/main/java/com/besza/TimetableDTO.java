package com.besza;

public final class TimetableDTO {
    private final String origin;
    private final String plannedDeparture;
    private final String departure;
    private final String destination;
    private final String plannedArrival;
    private final String arrival;

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

    @Override
    public String toString() {
        return "TimetableDTO{origin: %s, destination: %s, plannedDeparture: %s}"
                .formatted(origin, destination, plannedDeparture);
    }
}
