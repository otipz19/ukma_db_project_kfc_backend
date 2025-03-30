package ua.edu.ukma.db.kfc.configuration;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

@WebListener
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class FlywayMigrationApplier implements ServletContextListener {

    private final DataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .load();
        flyway.migrate();
    }
}
