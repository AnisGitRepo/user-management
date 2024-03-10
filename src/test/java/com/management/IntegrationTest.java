package com.management;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class IntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    public static PostgreSQLContainer postgreSQLContainer = CustomPostgresqlContainer.getInstance();

    @AfterEach
    public void tearDown() {
        // Clean up any inserted data after each test
        jdbcTemplate.execute("DELETE FROM public.user");
    }
}
