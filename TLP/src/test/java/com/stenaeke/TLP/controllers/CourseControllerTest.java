package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.course.CourseDto;
import com.stenaeke.TLP.dtos.auth.TokenResponse;
import com.stenaeke.TLP.dtos.course.CreateCourseDto;
import com.stenaeke.TLP.dtos.course.UpdateCourseDto;
import com.stenaeke.TLP.dtos.subcategory.SubcategoryDto;
import com.stenaeke.TLP.dtos.teacher.TeacherDto;
import com.stenaeke.TLP.repositories.CourseRepository;
import com.stenaeke.TLP.repositories.SubcategoryRepository;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CourseControllerTest {

    @Autowired
    CourseRepository courseRepository;
    TestRestTemplate restTemplate;
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
        courseRepository.deleteAll();
    }

    @Test
    @DisplayName("The postgresSQL container is running")
    void postgreSQLContainerIsRunning() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    //-----------Course endpoints---------------//

    @Test
    @DisplayName("add course return status code 201 created")
    void testAddCourse_whenValidDetailsProvided_returnsDtoAndHttpStatus201() {
        //Arrange

        CreateCourseDto createCourseDto = new CreateCourseDto();
        createCourseDto.setTitle("Psychology");
        createCourseDto.setDescription("Psychology is the course of Psychology");

        HttpEntity<CreateCourseDto> courseRequest = new HttpEntity<>(createCourseDto, headers);

        //Act
        var courseDto = restTemplate.postForEntity(baseUrl + "/courses", courseRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.CREATED, courseDto.getStatusCode());
        assertEquals(1, courseRepository.findAll().size());
        assertEquals(courseRepository.findById(courseDto.getBody().getId()).get().getTitle(), courseDto.getBody().getTitle());
    }

    @Test
    @DisplayName("add course with empty name return status code 400 bad request")
    void testAddCourse_whenInvalidNameProvided_returnsStatusCode400() {
        //Arrange
        CreateCourseDto createCourseDto = new CreateCourseDto();
        createCourseDto.setTitle("");
        createCourseDto.setDescription("Psychology is the course of Psychology");

        HttpEntity<CreateCourseDto> courseRequest = new HttpEntity<>(createCourseDto, headers);

        //Act
        var courseDto = restTemplate.postForEntity(baseUrl + "/courses", courseRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, courseDto.getStatusCode());
        assertEquals(0, courseRepository.findAll().size());
    }

    @Test
    @DisplayName("updateCourseTitle updates title and returns status code 200 with updated Dto")
    void testUpdateCourseTitle_whenValidDetailsProvided_returnsDtoAndHttpStatus200() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("NewTitle");
        HttpEntity<UpdateCourseDto> updateRequest = new HttpEntity<>(updateCourseDto, headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/courses/" + testCourse.getId(), HttpMethod.PATCH, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.OK, courseDto.getStatusCode());
        assertEquals(updateCourseDto.getTitle(), courseDto.getBody().getTitle());
        assertEquals(updateCourseDto.getTitle(), courseRepository.findById(testCourse.getId()).get().getTitle());
    }

    @Test
    @DisplayName("updateCourseTitle with empty title returns status code 400")
    void testUpdateCourseTitle_whenEmptyDetailsProvided_returnsHttpStatus400() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("");
        HttpEntity<UpdateCourseDto> updateRequest = new HttpEntity<>(updateCourseDto, headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/courses/" + testCourse.getId(), HttpMethod.PATCH, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, courseDto.getStatusCode());
        assertNotEquals(updateCourseDto.getTitle(), courseRepository.findById(testCourse.getId()).get().getTitle());
    }

    @Test
    @DisplayName("updateCourseDescription updates Description and returns status code 200 with updated Dto")
    void testUpdateCourseDescription_whenValidDetailsProvided_returnsDtoAndHttpStatus200() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setDescription("New Description");
        HttpEntity<UpdateCourseDto> updateRequest = new HttpEntity<>(updateCourseDto, headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/courses/" + testCourse.getId(), HttpMethod.PATCH, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.OK, courseDto.getStatusCode());
        assertEquals(updateCourseDto.getDescription(), courseDto.getBody().getDescription());
        assertEquals(updateCourseDto.getDescription(), courseRepository.findById(testCourse.getId()).get().getDescription());
    }

    @Test
    @DisplayName("updateCourseDescription with empty Description returns status code 400")
    void testUpdateCourseDescription_whenEmptyDetailsProvided_returnsHttpStatus400() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setDescription("");
        HttpEntity<UpdateCourseDto> updateRequest = new HttpEntity<>(updateCourseDto, headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/courses/" + testCourse.getId(), HttpMethod.PATCH, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, courseDto.getStatusCode());
        assertNotEquals(updateCourseDto.getDescription(), courseRepository.findById(testCourse.getId()).get().getDescription());
    }

    @Test
    @DisplayName("getCourse returns course with correct details and status 200")
    void testGetCourse_whenValidCourseId_returnsCourseDtoWithHttpStatus200() {
        //Arrange
        var testCourse = createCourse();
        HttpEntity<String> getCourseRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/courses/" + testCourse.getId(), HttpMethod.GET, getCourseRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCourse.getId(), response.getBody().getId());
        assertEquals(testCourse.getTitle(), courseRepository.findById(testCourse.getId()).get().getTitle());
    }

    @Test
    @DisplayName("getCourse returns 404 when course not found")
    void testGetCourse_whenInvalidCourseId_returnsHttpStatus404() {
        //Arrange
        HttpEntity<String> getCourseRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/courses/" + Long.MAX_VALUE, HttpMethod.GET, getCourseRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(courseRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Get all courses returns correct amount of courses")
    void testGetAllCourses_whenGetRequest_returnsCorrectAmountOfCoursesWithHttpStatus200() {
        //Arrange
        var testCourse1 = createCourse();
        var testCourse2 = createCourse();

        //Act
        var courses = restTemplate.exchange(baseUrl + "/courses", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<CourseDto>>() {});

        //Assert
        assertEquals(HttpStatus.OK, courses.getStatusCode());
        assertEquals(2, courses.getBody().size());
        assertTrue(courseRepository.findAll().size() == 2);
    }

    @Test
    @DisplayName("deleteCourse with non-existing course and returns status 400")
    void testDeleteCourse_whenInvalidDetailsProvided_returnsHttpStatus200() {
        //Arrange
        HttpEntity<String> deleteRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/courses/" + Long.MAX_VALUE, HttpMethod.DELETE, deleteRequest, String.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(courseRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("deleteCourse deletes course and returns status 200")
    void testDeleteCourse_whenValidDetailsProvided_returnsHttpStatus200() {
        //Arrange
        var testCourse = createCourse();
        HttpEntity<String> deleteRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/courses/" + testCourse.getId(), HttpMethod.DELETE, deleteRequest, String.class);

        //Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(courseRepository.findById(testCourse.getId()).isPresent());
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

    private Course createCourse() {
        Course course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Test Description");
        courseRepository.save(course);
        return course;
    }

}