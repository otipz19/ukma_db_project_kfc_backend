package ua.edu.ukma.db.kfc.configuration;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

@WebListener
public class FlywayMigrationApplier implements ServletContextListener {

    @Inject
    private DataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .load();
        flyway.migrate();
    }
}
