package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.domain.Teacher;
import com.stenaeke.TLP.dtos.auth.TokenResponse;
import com.stenaeke.TLP.dtos.teacher.TeacherDto;
import com.stenaeke.TLP.repositories.TeacherRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeacherControllerTest {

    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    TestRestTemplate restTemplate;
    JSONObject userDetailsRequestJson;
    HttpHeaders headers;
    String token;
    String baseUrl;

    @LocalServerPort
    private int port;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest")
            .withReuse(true);

    static {
        postgres.start();
    }

    @BeforeEach
    void setUp() throws JSONException {
        restTemplate = new TestRestTemplate();
        baseUrl = "http://localhost:" + port;

        teacherRepository.deleteAll();
        createTeacher("loginTeacher@test.com");

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        token = loginAndGetToken("loginTeacher@test.com", "1234");
        headers.setBearerAuth(token);

        userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstName", "Test");
        userDetailsRequestJson.put("lastName", "Teacher");
        userDetailsRequestJson.put("email", "test@test.com");
        userDetailsRequestJson.put("password","12345678");
        userDetailsRequestJson.put("confirmPassword", "12345678");
    }

    @AfterEach
    void cleanUp() {
        teacherRepository.deleteAll();
    }

    @Test
    @DisplayName("The postgresSQL container is running")
    void postgreSQLContainerIsRunning() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    @DisplayName("Teacher can be created")
    void testCreateTeacher_whenValidDetailsProvided_returnUserDetails() throws JSONException {
        //Arrange
        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.CREATED, createdTeacherDetails.getStatusCode());
        assertEquals(userDetailsRequestJson.get("firstName"), createdTeacherDetails.getBody().getFirstName());
        assertEquals(userDetailsRequestJson.get("lastName"), createdTeacherDetails.getBody().getLastName());
        assertEquals(userDetailsRequestJson.get("email"), createdTeacherDetails.getBody().getEmail());
        assertEquals(userDetailsRequestJson.get("firstName"), teacherRepository.findById(createdTeacherDetails.getBody().getId()).get().getFirstName());
        assertEquals(userDetailsRequestJson.get("lastName"), teacherRepository.findById(createdTeacherDetails.getBody().getId()).get().getLastName());
        assertEquals(userDetailsRequestJson.get("email"), teacherRepository.findById(createdTeacherDetails.getBody().getId()).get().getEmail());
    }

    @Test
    @DisplayName("Teacher can log in and receive JWT token")
    void testTeacherLogin_whenValidCredentialsProvided_returnJWTToken() throws JSONException {
        //Arrange
        String url = baseUrl + "/auth/teacher/login";
        Teacher testTeacher = createTeacher("test@email.com");

        JSONObject loginJson = new JSONObject();
        loginJson.put("email", testTeacher.getEmail());
        loginJson.put("password", "1234");

        //Act
        HttpEntity<String> request = new HttpEntity<>(loginJson.toString(), headers);

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(url, request, TokenResponse.class);

        token = response.getBody().token();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(token);
        assertTrue(token.length() > 10);
        assertTrue(teacherRepository.findById(testTeacher.getId()).isPresent());
        }


    @Test
    @DisplayName("CreateTeacher with empty first name returns status code 400")
    void testCreateTeacher_whenEmptyFirstName_returns400StatusCode() throws JSONException {
        //Arrange

        userDetailsRequestJson.put("firstName", "");
        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdTeacherDetails.getStatusCode());
        assertFalse(teacherRepository.findByEmail(createdTeacherDetails.getBody().getEmail()).isPresent());
    }

    @Test
    @DisplayName("CreateTeacher with empty name returns status code 400")
    void testCreateTeacher_whenEmptyLastName_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("lastName", "");
        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdTeacherDetails.getStatusCode());
        assertFalse(teacherRepository.findByEmail(createdTeacherDetails.getBody().getEmail()).isPresent());    }

    @Test
    @DisplayName("CreateTeacher with empty email returns status code 400")
    void testCreateTeacher_whenEmptyEmail_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("email", "");
        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdTeacherDetails.getStatusCode());
        assertFalse(teacherRepository.findByEmail(createdTeacherDetails.getBody().getEmail()).isPresent());    }

    @Test
    @DisplayName("CreateTeacher with wrong format email returns status code 400")
    void testCreateTeacher_whenWrongFormatEmail_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("email", "thisEmailIsWrongFormat");
        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdTeacherDetails.getStatusCode());
        assertFalse(teacherRepository.findByEmail(createdTeacherDetails.getBody().getEmail()).isPresent());    }

    @Test
    @DisplayName("CreateTeacher with unmatched passwords returns status code 400")
    void testCreateTeacher_whenPasswordDoNotMatch_returns400StatusCode() throws JSONException {
        //Arrange
        userDetailsRequestJson.put("password", "hello");
        userDetailsRequestJson.put("confirmPassword", "bye");
        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<TeacherDto> createdTeacherDetails = restTemplate.postForEntity(baseUrl + "/teacher/register", request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, createdTeacherDetails.getStatusCode());
        assertFalse(teacherRepository.findByEmail(createdTeacherDetails.getBody().getEmail()).isPresent());
    }

    @Test
    @DisplayName("getAllTeachers returns correct number of users with status code 200")
    void testGetAllTeachers_whenGetRequestSent_returnAllUsers() {
        //Arrange
        Teacher testTeacher1 = createTeacher("test@email.com");
        Teacher testTeacher2 = createTeacher("test2@email.com");

        HttpEntity<String> request = new HttpEntity<>(headers);
        //Act
        ResponseEntity<List<TeacherDto>> returnedTeachersResponse = restTemplate.exchange(baseUrl + "/teacher", HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });

        //Assert
        assertEquals(HttpStatus.OK, returnedTeachersResponse.getStatusCode());
        assert returnedTeachersResponse.getBody() != null;
        assertEquals(3, returnedTeachersResponse.getBody().size());
        assertEquals(3, teacherRepository.findAll().size());
    }

    @Test
    @DisplayName("getAllTeachers without auth token returns status code 403")
    void testGetAllTeachers_whenAuthTokenMissing_returnForbidden() {
        //Arrange
        headers.remove(HttpHeaders.AUTHORIZATION);
        HttpEntity<String> request = new HttpEntity<>(headers);
        //Act
        ResponseEntity<String> returnedTeachersResponse = restTemplate.exchange(baseUrl + "/teacher", HttpMethod.GET, request, String.class);

        //Assert
        assertEquals(HttpStatus.FORBIDDEN, returnedTeachersResponse.getStatusCode());
    }


    @Test
    @DisplayName("getTeacher returns correct user")
    void testGetTeacher_whenValidIdGivenInURL_returnUserWithGivenId() throws JSONException {
        //Arrange
        Teacher testTeacher = createTeacher("testTeacher@email.com");

        HttpEntity<String> request = new HttpEntity<>(headers);

        //Act
        ResponseEntity<TeacherDto> returnedTeacher = restTemplate.exchange(baseUrl + "/teacher/" + testTeacher.getId(), HttpMethod.GET, request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.OK, returnedTeacher.getStatusCode());
        assertEquals(testTeacher.getId(), (long) returnedTeacher.getBody().getId());
        assertEquals(testTeacher.getFirstName(), returnedTeacher.getBody().getFirstName());
        assertEquals(testTeacher.getLastName(), returnedTeacher.getBody().getLastName());
        assertEquals(testTeacher.getFirstName(), teacherRepository.findById(testTeacher.getId()).get().getFirstName());
        assertEquals(testTeacher.getLastName(), teacherRepository.findById(testTeacher.getId()).get().getLastName());
    }

    @Test
    @DisplayName("getTeacher with invalid id returns status code 404")
    void testGetTeacher_whenInvalidIdGivenInURL_returnUserWithGivenId() {
        //Arrange
        Teacher testTeacher = createTeacher("testTeacher@email.com");

        HttpEntity<String> request = new HttpEntity<>(headers);

        //Act
        ResponseEntity<TeacherDto> returnedTeacher = restTemplate.exchange(baseUrl + "/teacher/" + Long.MAX_VALUE, HttpMethod.GET, request, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, returnedTeacher.getStatusCode());
        assertFalse(teacherRepository.findById(Long.MAX_VALUE).isPresent());
    }

    @Test
    @DisplayName("updateTeacher returns Teacher with updated data")
    void testUpdateTeacher_whenValidIdAndDetailsGivenInURL_returnUpdatedTeacher() throws JSONException {
        //Arrange
        Teacher testTeacher = createTeacher("test@gmail.com");

        JSONObject updateTeacherRequestJson = new JSONObject();
        updateTeacherRequestJson.put("firstName","NewFirstName");
        HttpEntity<String> requestEntity = new HttpEntity<>(updateTeacherRequestJson.toString(), headers);

        //Act
        ResponseEntity<TeacherDto> returnedTeachersResponse = restTemplate.exchange(baseUrl + "/teacher/" + testTeacher.getId(), HttpMethod.PATCH, requestEntity, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.OK, returnedTeachersResponse.getStatusCode());
        assertNotNull(returnedTeachersResponse.getBody());
        assertEquals(updateTeacherRequestJson.get("firstName"), returnedTeachersResponse.getBody().getFirstName());
        assertEquals(updateTeacherRequestJson.get("firstName"), teacherRepository.findById(testTeacher.getId()).get().getFirstName());
    }

    @Test
    @DisplayName("updateTeacher with invalid id returns status code 404")
    void testUpdateTeacher_whenInvalidIdAndDetailsGivenInURL_returnStatus404() throws JSONException {
        //Arrange
        Teacher testTeacher = createTeacher("test@gmail.com");

        JSONObject updateTeacherRequestJson = new JSONObject();
        updateTeacherRequestJson.put("firstName","NewFirstName");
        HttpEntity<String> requestEntity = new HttpEntity<>(updateTeacherRequestJson.toString(), headers);

        //Act
        ResponseEntity<TeacherDto> returnedTeacherResponse = restTemplate.exchange(baseUrl + "/teacher/" + Long.MAX_VALUE, HttpMethod.PATCH, requestEntity, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, returnedTeacherResponse.getStatusCode());
        assertFalse(teacherRepository.findById(Long.MAX_VALUE).isPresent());
    }

    @Test
    @DisplayName("updateTeacher with invalid details returns status code 400")
    void testUpdateTeacher_whenValidIdAndInvalidDetailsGivenInURL_returnUpdatedTeacher() throws JSONException {
        //Arrange
        Teacher testTeacher = createTeacher("test@gmail.com");

        JSONObject updateTeacherRequestJson = new JSONObject();
        updateTeacherRequestJson.put("firstName", "");
        HttpEntity<String> requestEntity = new HttpEntity<>(updateTeacherRequestJson.toString(), headers);

        //Act
        ResponseEntity<TeacherDto> returnedTeacherResponse = restTemplate.exchange(baseUrl + "/teacher/" + testTeacher.getId(), HttpMethod.PATCH, requestEntity, TeacherDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, returnedTeacherResponse.getStatusCode());
        assertNotEquals(updateTeacherRequestJson.get("firstName"), teacherRepository.findById(testTeacher.getId()).get().getFirstName());
    }

    @Test
    @DisplayName("Teacher can be deleted")
    void testDeleteTeacher_whenValidIdPassed_returnStatusOk(){
        //Arrange
        Teacher testTeacher = createTeacher("test@gmail.com");
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        //Act
        ResponseEntity<?> response = restTemplate.exchange(
                baseUrl + "/teacher/" + testTeacher.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(teacherRepository.findById(testTeacher.getId()).isPresent());
    }

    @Test
    @DisplayName("deleteTeacher with invalid id returns status code 400")
    void testDeleteTeacher_whenInvalidIdPassed_returnStatusNotFound(){
        //Arrange
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        Teacher testTeacher = createTeacher("test@gmail.com");
        int preDeleteCount = teacherRepository.findAll().size();

        //Act
        ResponseEntity<?> response = restTemplate.exchange(
                baseUrl + "/teacher/" + Long.MAX_VALUE,
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(preDeleteCount, teacherRepository.findAll().size());
    }

    private String loginAndGetToken(String email, String password) throws JSONException {
        Teacher testTeacher = new Teacher();

        JSONObject loginJson = new JSONObject();
        loginJson.put("email", email);
        loginJson.put("password", password);

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> loginRequest = new HttpEntity<>(loginJson.toString(), loginHeaders);

        ResponseEntity<TokenResponse> loginResponse = restTemplate.postForEntity(baseUrl + "/auth/teacher/login", loginRequest, TokenResponse.class);

        return loginResponse.getBody().token();
    }

    private Teacher createTeacher(String email) {
        Teacher teacher = Teacher.builder()
                .firstName("test")
                .lastName("testson")
                .passwordHash(passwordEncoder.encode("1234"))
                .email(email)
                .build();
        teacherRepository.save(teacher);
        return teacher;
    }
}