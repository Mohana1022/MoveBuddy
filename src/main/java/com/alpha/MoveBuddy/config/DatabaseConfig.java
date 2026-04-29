package com.alpha.MoveBuddy.Config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Runs before Spring context loads.
 * If DATABASE_URL is set (Render/Heroku format: postgresql://user:pass@host/db),
 * it transforms it into a valid JDBC URL and injects username/password
 * so that Spring Boot's auto-configured DataSource picks them up correctly.
 */
public class DatabaseConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl == null || databaseUrl.isEmpty()) {
            return; // Use application.properties defaults (local dev)
        }

        try {
            // Render provides: postgresql://user:pass@host:port/db
            // We need:         jdbc:postgresql://host:port/db
            if (!databaseUrl.startsWith("postgresql://") && !databaseUrl.startsWith("postgres://")) {
                return; // Already a JDBC URL or unknown format — leave it alone
            }

            // Strip the scheme
            String withoutScheme = databaseUrl.replaceFirst("^(postgresql|postgres)://", "");

            // Split user:pass@rest
            String[] atSplit = withoutScheme.split("@", 2);
            String userInfo = atSplit[0];
            String hostAndDb = atSplit[1];

            String username = userInfo.split(":")[0];
            String password = userInfo.contains(":") ? userInfo.split(":", 2)[1] : "";

            // Handle optional port — host:port/db or host/db
            String jdbcUrl;
            if (hostAndDb.contains(":")) {
                // Has explicit port: host:port/db
                jdbcUrl = "jdbc:postgresql://" + hostAndDb;
            } else {
                // No port: host/db — insert default port 5432
                String[] hostDb = hostAndDb.split("/", 2);
                jdbcUrl = "jdbc:postgresql://" + hostDb[0] + ":5432/" + (hostDb.length > 1 ? hostDb[1] : "");
            }

            Map<String, Object> props = new HashMap<>();
            props.put("spring.datasource.url", jdbcUrl);
            props.put("spring.datasource.username", username);
            props.put("spring.datasource.password", password);
            props.put("spring.datasource.driver-class-name", "org.postgresql.Driver");

            environment.getPropertySources().addFirst(
                    new MapPropertySource("renderDatabaseUrlOverride", props)
            );

        } catch (Exception e) {
            System.err.println("[DatabaseConfig] Failed to parse DATABASE_URL: " + e.getMessage());
        }
    }
}
