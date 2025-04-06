package com.example.demo;

import com.example.demo.reader.PersonItemReader;
import com.example.demo.service.PersonService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;


import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBatchTest
@SpringBootTest
@TestPropertySource("classpath:application.properties")
class JobStepTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;



    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");


    @DynamicPropertySource
    static void containersProperties(DynamicPropertyRegistry registry) {
        postgreSQLContainer.start();
        String postgresUrl = "jdbc:postgresql://" + postgreSQLContainer.getHost() + ":" + postgreSQLContainer.getMappedPort(5432) + "/testdb";
        registry.add("spring.datasource.url", () -> postgresUrl);
        registry.add("spring.datasource.username", () -> "user");
        registry.add("spring.datasource.password", () -> "password");
    }

    @AfterAll
    static void tearDown() {
        postgreSQLContainer.stop();
    }

    @Test
    void testUploadFileStep()  {
        jobLauncherTestUtils.setJobRepository(jobRepository);

        jobLauncherTestUtils.launchStep("step");

    }


}