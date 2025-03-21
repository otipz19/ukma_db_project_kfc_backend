package ua.edu.ukma.db.kfc.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.sql.DataSource;
import java.util.Properties;

@ApplicationScoped
@Getter
public class DataSourceConfig {

    @Inject
    @ConfigProperty(name = "dataSource.className")
    private String className;

    @Inject
    @ConfigProperty(name = "dataSource.user")
    private String user;

    @Inject
    @ConfigProperty(name = "dataSource.password")
    private String password;

    @Inject
    @ConfigProperty(name = "dataSource.databaseName")
    private String databaseName;

    @Inject
    @ConfigProperty(name = "dataSource.portNumber")
    private String portNumber;

    @Inject
    @ConfigProperty(name = "dataSource.serverName")
    private String serverName;

    @Produces
    @ApplicationScoped
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(className);
        config.setDataSourceProperties(dataSourceProperties());
        return new HikariDataSource(config);
    }

    private Properties dataSourceProperties() {
        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("user", user);
        dataSourceProperties.setProperty("password", password);
        dataSourceProperties.setProperty("databaseName", databaseName);
        dataSourceProperties.setProperty("portNumber", portNumber);
        dataSourceProperties.setProperty("serverName", serverName);
        return dataSourceProperties;
    }
}
