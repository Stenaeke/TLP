package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.StudentDTO;
import com.stenaeke.TLP.dtos.UpdateUserRequest;
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
class StudentControllerTest {

    TestRestTemplate restTemplate;
    JSONObject userDetailsRequestJson;
    HttpHeaders headers;

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
        userDetailsRequestJson.put("lastName", "Student");
        userDetailsRequestJson.put("email", "test@test.com");
        userDetailsRequestJson.put("password","12345678");
        userDetailsRequestJson.put("confirmPassword", "12345678");

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

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
    @DisplayName("Student can be created")
    void testCreateStudent_whenValidDetailsProvided_returnUserDetails() throws JSONException {
        //Arrange
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull StudentDTO> createdStudentDetails = restTemplate.postForEntity("http://localhost:" + port + "/student/register", request, StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.CREATED, createdStudentDetails.getStatusCode());
        assertEquals(userDetailsRequestJson.get("firstName"), createdStudentDetails.getBody().getFirstName());
        assertEquals(userDetailsRequestJson.get("lastName"), createdStudentDetails.getBody().getLastName());
        assertEquals(userDetailsRequestJson.get("email"), createdStudentDetails.getBody().getEmail());
    }

    @Test
    @Order(3)
    @DisplayName("CreateStudent with empty first name returns status code 400")
    void testCreateStudent_whenEmptyFirstName_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("firstName", "");
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull StudentDTO> createdStudentDetails = restTemplate.postForEntity("http://localhost:" + port + "/student/register", request, StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdStudentDetails.getStatusCode());
    }

    @Test
    @Order(4)
    @DisplayName("CreateStudent with empty name returns status code 400")
    void testCreateStudent_whenEmptyLastName_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("lastName", "");
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull StudentDTO> createdStudentDetails = restTemplate.postForEntity("http://localhost:" + port + "/student/register", request, StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdStudentDetails.getStatusCode());
    }

    @Test
    @Order(5)
    @DisplayName("CreateStudent with empty email returns status code 400")
    void testCreateStudent_whenEmptyEmail_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("email", "");
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull StudentDTO> createdStudentDetails = restTemplate.postForEntity("http://localhost:" + port + "/student/register", request, StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdStudentDetails.getStatusCode());
    }

    @Test
    @Order(6)
    @DisplayName("CreateStudent with wrong format email returns status code 400")
    void testCreateStudent_whenWrongFormatEmail_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("email", "thisEmailIsWrongFormat");
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull StudentDTO> createdStudentDetails = restTemplate.postForEntity("http://localhost:" + port + "/student/register", request, StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdStudentDetails.getStatusCode());
    }

    @Test
    @Order(7)
    @DisplayName("CreateStudent with wrong format email returns status code 400")
    void testCreateStudent_whenWrongPasswordDoNotMatch_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("password", "hello");
        userDetailsRequestJson.put("confirmPassword", "bye");
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<@NotNull StudentDTO> createdStudentDetails = restTemplate.postForEntity("http://localhost:" + port + "/student/register", request, StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdStudentDetails.getStatusCode());
    }

    @Test
    @Order(8)
    @DisplayName("getAllStudents returns 1 user with status code 200")
    void testgetAllStudents_whenGetRequestSent_returnAllUsers() {
        //Arrange
        HttpEntity<@NotNull String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
        //Act
        ResponseEntity<@NotNull List<StudentDTO>> returnedStudentsResponse = restTemplate.exchange("http://localhost:" + port + "/student", HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });

        //Assert
        assertEquals(HttpStatus.OK, returnedStudentsResponse.getStatusCode());
        assert returnedStudentsResponse.getBody() != null;
        assertEquals(1, returnedStudentsResponse.getBody().size());
    }

    @Test
    @Order(9)
    @DisplayName("getStudent returns correct user")
    void testGetStudent_whenValidIdGivenInURL_returnUserWithGivenId() throws JSONException {
        //Arrange

        //Act
        ResponseEntity<@NotNull StudentDTO> returnedStudent = restTemplate.getForEntity("http://localhost:" + port + "/student/1", StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.OK, returnedStudent.getStatusCode());
        assert returnedStudent.getBody() != null;
        assertEquals(1, (long) returnedStudent.getBody().getId());
        assertEquals(userDetailsRequestJson.get("firstName"), returnedStudent.getBody().getFirstName());
    }

    @Test
    @Order(10)
    @DisplayName("getStudent with invalid id returns status code 404")
    void testGetStudent_whenInvalidIdGivenInURL_returnUserWithGivenId() {
        //Arrange

        //Act
        ResponseEntity<@NotNull StudentDTO> returnedStudent = restTemplate.getForEntity("http://localhost:" + port + "/student/100000000000", StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, returnedStudent.getStatusCode());
    }

    @Test
    @Order(11)
    @DisplayName("updateStudent returns student with updated data")
    void testUpdateStudent_whenValidIdAndDetailsGivenInURL_returnUpdatedStudent() throws JSONException {
        //Arrange
        String updatedFirstName = "MyNewFirstName";
        JSONObject updateStudentRequestJson = new JSONObject();
        updateStudentRequestJson.put("firstName",updatedFirstName);

        HttpEntity<String> requestEntity = new HttpEntity<>(updateStudentRequestJson.toString(), headers);

        System.out.println(requestEntity.getBody().toString());

        //Act
        ResponseEntity<@NotNull StudentDTO> returnedStudentsResponse = restTemplate.exchange("http://localhost:" + port + "/student/1", HttpMethod.PATCH, requestEntity, StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.OK, returnedStudentsResponse.getStatusCode());
        assertNotNull(returnedStudentsResponse.getBody());
        Assertions.assertEquals(updatedFirstName, returnedStudentsResponse.getBody().getFirstName());
    }

    @Test
    @Order(11)
    @DisplayName("updateStudent with invalid id returns status code 404")
    void testUpdateStudent_whenInvalidIdAndDetailsGivenInURL_returnUpdatedStudent() throws JSONException {
        //Arrange
        String updatedFirstName = "MyNewFirstName";
        JSONObject updateStudentRequestJson = new JSONObject();
        updateStudentRequestJson.put("firstName",updatedFirstName);

        HttpEntity<String> requestEntity = new HttpEntity<>(updateStudentRequestJson.toString(), headers);

        System.out.println(requestEntity.getBody().toString());

        //Act
        ResponseEntity<@NotNull StudentDTO> returnedStudentsResponse = restTemplate.exchange("http://localhost:" + port + "/student/100000000000", HttpMethod.PATCH, requestEntity, StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, returnedStudentsResponse.getStatusCode());
    }

    @Test
    @Order(12)
    @DisplayName("updateStudent with invalid details returns status code 400")
    void testUpdateStudent_whenValidIdAndInvalidDetailsGivenInURL_returnUpdatedStudent() throws JSONException {
        //Arrange
        String updatedFirstName = "";
        JSONObject updateStudentRequestJson = new JSONObject();
        updateStudentRequestJson.put("firstName",updatedFirstName);

        HttpEntity<String> requestEntity = new HttpEntity<>(updateStudentRequestJson.toString(), headers);

        System.out.println(requestEntity.getBody().toString());

        //Act
        ResponseEntity<@NotNull StudentDTO> returnedStudentsResponse = restTemplate.exchange("http://localhost:" + port + "/student/1", HttpMethod.PATCH, requestEntity, StudentDTO.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, returnedStudentsResponse.getStatusCode());
    }

    @Test
    @Order(13)
    @DisplayName("Student can be deleted")
    void testDeleteStudent_whenValidIdPassed_returnStatusOk(){
        //Arrange
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        //Act
        ResponseEntity<?> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/1",
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(14)
    @DisplayName("deleteStudent with invalid id returns status code 400")
    void testDeleteStudent_whenInvalidIdPassed_returnStatusNotFound(){
        //Arrange
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        //Act
        ResponseEntity<?> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/10000000000",
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}