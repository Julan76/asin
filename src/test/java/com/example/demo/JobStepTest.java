package com.example.demo;

import com.example.demo.domain.Person;
import com.example.demo.domain.Status;
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


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


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

        int rowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Person", Integer.class);

        List<Person> personList = findAll();
        assertEquals("Samuel",personList.get(0).getNom());
        assertEquals("Etoo",personList.get(0).getPrenom());
        assertEquals(Status.ACTIF,personList.get(0).getStatus());
        assertEquals(LocalDate.of(1984,9,21),personList.get(0).getDatedenaissance());

        assertEquals("Didier",personList.get(1).getNom());
        assertEquals("Drogba",personList.get(1).getPrenom());
        assertEquals(Status.INACTIF,personList.get(1).getStatus());
        assertEquals(LocalDate.of(1971,3,13),personList.get(1).getDatedenaissance());
        assertEquals(2, rowCount);
    }


    public List<Person> findAll() {
        String sql = "SELECT * FROM Person";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Person person = new Person();
            person.setMatricule(rs.getString("matricule"));
            person.setNom(rs.getString("nom"));
            person.setPrenom(rs.getString("prenom"));
            person.setDatedenaissance(rs.getDate("datedenaissance").toLocalDate());
            person.setStatus(Status.valueOf(rs.getString("status")));
            return person;
        });
    }
}