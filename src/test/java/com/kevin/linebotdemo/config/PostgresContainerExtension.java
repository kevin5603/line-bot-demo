package com.kevin.linebotdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
public class PostgresContainerExtension implements AfterAllCallback {

    private static final PostgreSQLContainer postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
                .withDatabaseName("test-db")
                .withUsername("sa")
                .withPassword("Passw0rd");
        postgreSQLContainer.start();

        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
    }


    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }
}
