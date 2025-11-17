package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.auth.TokenResponse;
import com.stenaeke.TLP.dtos.teacher.TeacherDto;
import org.jetbrains.annotations.NotNull;
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
class TeacherControllerTest {


    TestRestTemplate restTemplate;
    JSONObject userDetailsRequestJson;
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

        userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstName", "Test");
        userDetailsRequestJson.put("lastName", "Teacher");
        userDetailsRequestJson.put("email", "test@test.com");
        userDetailsRequestJson.put("password","12345678");
        userDetailsRequestJson.put("confirmPassword", "12345678");

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (token != null) {
            headers.setBearerAuth(token);
        }

        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("The postgresSQL container is running")
    @Order(1)
    void postgreSQLContainerIsRunning() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    @Order(2)
    @DisplayName("Teacher can be created")
    void testCreateTeacher_whenValidDetailsProvided_returnUserDetails() throws JSONException {
        //Arrange
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.CREATED, createdTeacherDetails.getStatusCode());
        assertEquals(userDetailsRequestJson.get("firstName"), createdTeacherDetails.getBody().getFirstName());
        assertEquals(userDetailsRequestJson.get("lastName"), createdTeacherDetails.getBody().getLastName());
        assertEquals(userDetailsRequestJson.get("email"), createdTeacherDetails.getBody().getEmail());
    }

    @Test
    @Order(3)
    @DisplayName("Teacher can log in and receive JWT token")
    void testTeacherLogin_whenValidCredentialsProvided_returnJWTToken() throws JSONException {
        //Arrange

        String url = baseUrl + "/auth/teacher/login";
        JSONObject loginJson = new JSONObject();

        loginJson.put("email", userDetailsRequestJson.get("email"));
        loginJson.put("password", userDetailsRequestJson.get("password"));

        //Act
        HttpEntity<String> request = new HttpEntity<>(loginJson.toString(), headers);

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(url, request, TokenResponse.class);

        token = response.getBody().token();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(token);
        assertTrue(token.length() > 10);
        }


    @Test
    @Order(4)
    @DisplayName("CreateTeacher with empty first name returns status code 400")
    void testCreateTeacher_whenEmptyFirstName_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("firstName", "");
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdTeacherDetails.getStatusCode());
    }

    @Test
    @Order(5)
    @DisplayName("CreateTeacher with empty name returns status code 400")
    void testCreateTeacher_whenEmptyLastName_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("lastName", "");
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdTeacherDetails.getStatusCode());
    }

    @Test
    @Order(6)
    @DisplayName("CreateTeacher with empty email returns status code 400")
    void testCreateTeacher_whenEmptyEmail_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("email", "");
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdTeacherDetails.getStatusCode());
    }

    @Test
    @Order(7)
    @DisplayName("CreateTeacher with wrong format email returns status code 400")
    void testCreateTeacher_whenWrongFormatEmail_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("email", "thisEmailIsWrongFormat");
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdTeacherDetails.getStatusCode());
    }

    @Test
    @Order(8)
    @DisplayName("CreateTeacher with wrong format email returns status code 400")
    void testCreateTeacher_whenWrongPasswordDoNotMatch_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("password", "hello");
        userDetailsRequestJson.put("confirmPassword", "bye");
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdTeacherDetails.getStatusCode());
    }

    @Test
    @Order(9)
    @DisplayName("getAllTeachers returns 1 user with status code 200")
    void testGetAllTeachers_whenGetRequestSent_returnAllUsers() {
        //Arrange
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
        //Act
        ResponseEntity<@NotNull List<TeacherDto>> returnedTeachersResponse = restTemplate.exchange(baseUrl + "/teacher", HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });

        //Assert
        assertEquals(HttpStatus.OK, returnedTeachersResponse.getStatusCode());
        assert returnedTeachersResponse.getBody() != null;
        assertEquals(1, returnedTeachersResponse.getBody().size());
    }

    @Test
    @Order(10)
    @DisplayName("getAllTeachers without auth token returns status code 403")
    void testGetAllTeachers_whenAuthTokenMissing_returnForbidden() {
        //Arrange
        headers.remove(HttpHeaders.AUTHORIZATION);
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
        //Act
        ResponseEntity<@NotNull List<TeacherDto>> returnedTeachersResponse = restTemplate.exchange(baseUrl + "/teacher", HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });

        //Assert
        assertEquals(HttpStatus.FORBIDDEN, returnedTeachersResponse.getStatusCode());
    }


    @Test
    @Order(11)
    @DisplayName("getTeacher returns correct user")
    void testGetTeacher_whenValidIdGivenInURL_returnUserWithGivenId() throws JSONException {
        //Arrange
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull TeacherDto> returnedTeacher = restTemplate.exchange(baseUrl + "/teacher/1", HttpMethod.GET, request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.OK, returnedTeacher.getStatusCode());
        assert returnedTeacher.getBody() != null;
        assertEquals(1, (long) returnedTeacher.getBody().getId());
        assertEquals(userDetailsRequestJson.get("firstName"), returnedTeacher.getBody().getFirstName());
    }

    @Test
    @Order(12)
    @DisplayName("getTeacher with invalid id returns status code 404")
    void testGetTeacher_whenInvalidIdGivenInURL_returnUserWithGivenId() {
        //Arrange
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull TeacherDto> returnedTeacher = restTemplate.exchange(baseUrl + "/teacher/10000000000", HttpMethod.GET, request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, returnedTeacher.getStatusCode());
    }

    @Test
    @Order(13)
    @DisplayName("updateTeacher returns Teacher with updated data")
    void testUpdateTeacher_whenValidIdAndDetailsGivenInURL_returnUpdatedTeacher() throws JSONException {
        //Arrange
        String updatedFirstName = "MyNewFirstName";
        JSONObject updateTeacherRequestJson = new JSONObject();
        updateTeacherRequestJson.put("firstName",updatedFirstName);
        HttpEntity<String> requestEntity = new HttpEntity<>(updateTeacherRequestJson.toString(), headers);

        System.out.println(requestEntity.getBody().toString());

        //Act
        ResponseEntity<@NotNull TeacherDto> returnedTeachersResponse = restTemplate.exchange(baseUrl + "/teacher/1", HttpMethod.PATCH, requestEntity, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.OK, returnedTeachersResponse.getStatusCode());
        assertNotNull(returnedTeachersResponse.getBody());
        Assertions.assertEquals(updatedFirstName, returnedTeachersResponse.getBody().getFirstName());
    }

    @Test
    @Order(14)
    @DisplayName("updateTeacher with invalid id returns status code 404")
    void testUpdateTeacher_whenInvalidIdAndDetailsGivenInURL_returnUpdatedTeacher() throws JSONException {
        //Arrange
        String updatedFirstName = "MyNewFirstName";
        JSONObject updateTeacherRequestJson = new JSONObject();
        updateTeacherRequestJson.put("firstName",updatedFirstName);
        HttpEntity<String> requestEntity = new HttpEntity<>(updateTeacherRequestJson.toString(), headers);

        System.out.println(requestEntity.getBody().toString());

        //Act
        ResponseEntity<@NotNull TeacherDto> returnedTeacherResponse = restTemplate.exchange(baseUrl + "/teacher/100000000000", HttpMethod.PATCH, requestEntity, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, returnedTeacherResponse.getStatusCode());
    }

    @Test
    @Order(15)
    @DisplayName("updateTeacher with invalid details returns status code 400")
    void testUpdateTeacher_whenValidIdAndInvalidDetailsGivenInURL_returnUpdatedTeacher() throws JSONException {
        //Arrange
        String updatedFirstName = "";
        JSONObject updateTeacherRequestJson = new JSONObject();
        updateTeacherRequestJson.put("firstName",updatedFirstName);
        HttpEntity<String> requestEntity = new HttpEntity<>(updateTeacherRequestJson.toString(), headers);

        System.out.println(requestEntity.getBody().toString());

        //Act
        ResponseEntity<@NotNull TeacherDto> returnedTeacherResponse = restTemplate.exchange(baseUrl + "/teacher/1", HttpMethod.PATCH, requestEntity, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, returnedTeacherResponse.getStatusCode());
    }

    @Test
    @Order(16)
    @DisplayName("Teacher can be deleted")
    void testDeleteTeacher_whenValidIdPassed_returnStatusOk(){
        //Arrange
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        //Act
        ResponseEntity<?> response = restTemplate.exchange(
                baseUrl + "/teacher/1",
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(17)
    @DisplayName("deleteTeacher with invalid id returns status code 400")
    void testDeleteTeacher_whenInvalidIdPassed_returnStatusNotFound(){
        //Arrange
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        //Act
        ResponseEntity<?> response = restTemplate.exchange(
                baseUrl + "/teacher/10000000000",
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}