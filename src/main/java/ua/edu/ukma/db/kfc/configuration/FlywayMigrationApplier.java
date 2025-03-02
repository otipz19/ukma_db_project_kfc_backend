package ua.edu.ukma.db.kfc.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import javax.sql.DataSource;

@ApplicationScoped
public class FlywayMigrationApplier implements ApplicationEventListener {

    @Inject
    private DataSource dataSource;

    @Override
    public void onEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent.getType() == ApplicationEvent.Type.INITIALIZATION_FINISHED) {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .load();
            flyway.migrate();
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }
}
