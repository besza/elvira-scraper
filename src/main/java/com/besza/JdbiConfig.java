package com.besza;

import io.agroal.api.AgroalDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Dependent
public class JdbiConfig {

    @Inject
    AgroalDataSource defaultDataSource;

    @ApplicationScoped
    @Produces
    public Jdbi setup() {
        return Jdbi.create(defaultDataSource)
                .installPlugin(new PostgresPlugin())
                .registerRowMapper(TimetableBE.class, new TimetableRowMapper());
    }
}
