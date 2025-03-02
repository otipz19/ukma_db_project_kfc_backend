package ua.edu.ukma.db.kfc.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import javax.sql.DataSource;

@ApplicationScoped
public class DataSourceConfig {

    private static final String PROPERTIES_FILE = "data-source.properties";

    @Produces
    @ApplicationScoped
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig(PROPERTIES_FILE);
        return new HikariDataSource(config);
    }
}
