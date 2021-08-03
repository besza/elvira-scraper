package com.besza;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class TimetableRowMapper implements RowMapper<TimetableBE> {
    @Override
    public TimetableBE map(ResultSet rs, StatementContext ctx) throws SQLException {
        var plannedArrival = rs.getTimestamp("planned_arrival");
        var arrival = rs.getTimestamp("arrival");
        var delayMinutes = arrival != null
                ? Duration.between(plannedArrival.toInstant(), arrival.toInstant()).toMinutes()
                : null;
        return new TimetableBE(
                rs.getLong("id"),
                rs.getString("origin"),
                rs.getString("destination"),
                rs.getTimestamp("planned_departure"),
                rs.getTimestamp("departure"),
                plannedArrival,
                arrival,
                delayMinutes
        );
    }
}
