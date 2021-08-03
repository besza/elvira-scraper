package com.besza;

import java.sql.Timestamp;

public record TimetableBE (
    Long id,
    String origin,
    String destination,
    Timestamp plannedDeparture,
    Timestamp departure,
    Timestamp plannedArrival,
    Timestamp arrival,
    Long delayMinutes
) {}
