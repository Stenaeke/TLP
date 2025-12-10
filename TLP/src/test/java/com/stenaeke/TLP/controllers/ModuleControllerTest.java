package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.auth.TokenResponse;
import com.stenaeke.TLP.dtos.teacher.TeacherDto;
import com.stenaeke.TLP.repositories.CourseRepository;
import com.stenaeke.TLP.repositories.ModuleRepository;
import com.stenaeke.TLP.repositories.SubcategoryRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ModuleControllerTest {

    @Autowired
    ModuleRepository moduleRepository;
    TestRestTemplate restTemplate;
    HttpHeaders headers;
    String token;
    String baseUrl;

    @LocalServerPort
    private int port;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");

    static {
        postgres.start();
    }

    @BeforeEach
    void setUp() throws JSONException {
        restTemplate = new TestRestTemplate();

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        baseUrl = "http://localhost:" + port;

        if (token == null) {
            token = registerAndLoginTeacher();
        }
        headers.setBearerAuth(token);
    }

    @AfterEach
    void cleanUp() {
        moduleRepository.deleteAll();
    }

    @Test
    @DisplayName("The postgresSQL container is running")
    void postgreSQLContainerIsRunning() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }











    //Helpers
    private String registerAndLoginTeacher() throws JSONException {
        JSONObject registerJson = new JSONObject();
        registerJson.put("firstName", "Test");
        registerJson.put("lastName", "Teacher");
        registerJson.put("email", "teacher@test.com");
        registerJson.put("password", "12345678");
        registerJson.put("confirmPassword", "12345678");

        HttpEntity<String> registerRequest = new HttpEntity<>(registerJson.toString(), headers);
        restTemplate.postForEntity(baseUrl + "/teacher/register", registerRequest, TeacherDto.class);

        JSONObject loginJson = new JSONObject();
        loginJson.put("email", "teacher@test.com");
        loginJson.put("password", "12345678");

        HttpEntity<String> loginRequest = new HttpEntity<>(loginJson.toString(), headers);
        ResponseEntity<TokenResponse> loginResponse = restTemplate.postForEntity(baseUrl + "/auth/teacher/login", loginRequest, TokenResponse.class);

        return loginResponse.getBody().token();
    }
}
