package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.course.CourseDto;
import com.stenaeke.TLP.dtos.auth.TokenResponse;
import com.stenaeke.TLP.dtos.teacher.TeacherDto;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CourseControllerTest {

    TestRestTemplate restTemplate;
    HttpHeaders headers;
    String token;
    String baseUrl;
    JSONObject courseJson;

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

        courseJson = new JSONObject();
        courseJson.put("title","Psychology");
        courseJson.put("description","Psychology is the course of Psychology");
    }


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


    @Test
    @DisplayName("The postgresSQL container is running")
    @Order(1)
    void postgreSQLContainerIsRunning() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    @DisplayName("add course return status code 201 created")
    @Order(2)
    void testAddCourse_whenValidDetailsProvided_returnsDtoAndHttpStatus201() {
        //Arrange

        HttpEntity<String> courseRequest = new HttpEntity<>(courseJson.toString(), headers);

        //Act
        var courseDto = restTemplate.postForEntity(baseUrl + "/course", courseRequest, String.class);

        //Assert
        assertEquals(HttpStatus.CREATED, courseDto.getStatusCode());
    }

    @Test
    @DisplayName("add course with empty name return status code 400 bad request")
    @Order(3)
    void testAddCourse_whenInvalidNameProvided_returnsStatusCode400() throws JSONException {
        //Arrange
        courseJson.put("title","");

        HttpEntity<String> courseRequest = new HttpEntity<>(courseJson.toString(), headers);

        //Act
        var courseDto = restTemplate.postForEntity(baseUrl + "/course", courseRequest, String.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, courseDto.getStatusCode());
    }

    @Test
    @DisplayName("updateCourseTitle updates title and returns status code 200 with updated Dto")
    @Order(4)
    void testUpdateCourseTitle_whenValidDetailsProvided_returnsDtoAndHttpStatus200() throws JSONException {
        //Arrange
        JSONObject updateJson = new JSONObject();
        updateJson.put("title", "newTitle");
        HttpEntity<String> updateRequest = new HttpEntity<>(updateJson.toString(), headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/course/1/title", HttpMethod.PUT, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.OK, courseDto.getStatusCode());
        assertEquals(updateJson.get("title"), courseDto.getBody().getTitle());
    }

    @Test
    @DisplayName("updateCourseTitle with empty title returns status code 400")
    @Order(5)
    void testUpdateCourseTitle_whenEmptyDetailsProvided_returnsHttpStatus400() throws JSONException {
        //Arrange
        JSONObject updateJson = new JSONObject();
        updateJson.put("title", "");
        HttpEntity<String> updateRequest = new HttpEntity<>(updateJson.toString(), headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/course/1/title", HttpMethod.PUT, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, courseDto.getStatusCode());
    }

    @Test
    @DisplayName("updateCourseDescription updates Description and returns status code 200 with updated Dto")
    @Order(6)
    void testUpdateCourseDescription_whenValidDetailsProvided_returnsDtoAndHttpStatus200() throws JSONException {
        //Arrange
        JSONObject updateJson = new JSONObject();
        updateJson.put("description", "NewDescription");
        HttpEntity<String> updateRequest = new HttpEntity<>(updateJson.toString(), headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/course/1/description", HttpMethod.PUT, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.OK, courseDto.getStatusCode());
        assertEquals(updateJson.get("description"), courseDto.getBody().getDescription());
    }

    @Test
    @DisplayName("updateCourseDescription with empty Description returns status code 400")
    @Order(7)
    void testUpdateCourseDescription_whenEmptyDetailsProvided_returnsHttpStatus400() throws JSONException {
        //Arrange
        JSONObject updateJson = new JSONObject();
        updateJson.put("description", "");
        HttpEntity<String> updateRequest = new HttpEntity<>(updateJson.toString(), headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/course/1/description", HttpMethod.PUT, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, courseDto.getStatusCode());
    }

    @Test
    @DisplayName("Get all courses returns correct amount of courses")
    @Order(8)
    void testGetAllCourses_whenGetRequest_returnsCorrectAmountOfCoursesWithHttpStatus200() {
        //Arrange

        //Act
        var courses = restTemplate.exchange(baseUrl + "/course", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<CourseDto>>() {});

        //Assert
        assertEquals(HttpStatus.OK, courses.getStatusCode());
        assertNotNull(courses.getBody());
        assertEquals(1, courses.getBody().size());
    }

    @Test
    @DisplayName("deleteCourse with non-existing course and returns status 400")
    @Order(9)
    void testDeleteCourse_whenInvalidDetailsProvided_returnsHttpStatus200() {
        //Arrange
        HttpEntity<String> deleteRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/11213", HttpMethod.DELETE, deleteRequest, String.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("deleteCourse deletes course and returns status 200")
    @Order(10)
    void testDeleteCourse_whenValidDetailsProvided_returnsHttpStatus200() {
        //Arrange
        HttpEntity<String> deleteRequest = new HttpEntity<>(headers);
        //Act
        var response = restTemplate.exchange(baseUrl + "/course/1", HttpMethod.DELETE, deleteRequest, String.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}