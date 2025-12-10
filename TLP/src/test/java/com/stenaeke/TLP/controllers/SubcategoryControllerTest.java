package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.auth.TokenResponse;
import com.stenaeke.TLP.dtos.subcategory.CreateSubcategoryRequest;
import com.stenaeke.TLP.dtos.subcategory.SubcategoryDto;
import com.stenaeke.TLP.dtos.subcategory.UpdateSubcategoryRequest;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubcategoryControllerTest {

    @Autowired
    CourseRepository courseRepository;
    @Autowired
    SubcategoryRepository subcategoryRepository;
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
        courseRepository.deleteAll();
        subcategoryRepository.deleteAll();
    }

    @Test
    @DisplayName("The postgresSQL container is running")
    void postgreSQLContainerIsRunning() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    @DisplayName("addSubcategory creates subcategory and associates with course and returns status 201")
    void testAddSubcategory_whenValidDetailsProvided_returnsHttpStatus201(){
        //Arrange
        var testCourse = createCourse();

        CreateSubcategoryRequest requestDto = new CreateSubcategoryRequest();
        requestDto.setCourseId(testCourse.getId());
        requestDto.setTitle("Behavioral psychology");
        requestDto.setDescription("Behavioral psychology is the scientific study of how observable behaviors are learned and influenced by interactions with the environment.");
        HttpEntity<CreateSubcategoryRequest> request = new HttpEntity<>(requestDto, headers);

        //Act
        var subcategoryResponse = restTemplate.postForEntity(baseUrl + "/subcategories", request, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.CREATED, subcategoryResponse.getStatusCode());
        assertEquals(subcategoryResponse.getBody().getCourseId(), testCourse.getId());
        assertEquals(requestDto.getTitle(),subcategoryResponse.getBody().getTitle());
        assertEquals(requestDto.getDescription(),subcategoryResponse.getBody().getDescription());
        assertEquals(1, subcategoryRepository.findAll().size());
        assertEquals(subcategoryRepository.findById(subcategoryResponse.getBody().getId()).get().getTitle(), subcategoryResponse.getBody().getTitle());
    }

    @Test
    @DisplayName("addSubcategory with empty name returns status 400")
    void testAddSubcategory_whenEmptyNameProvided_returnsHttpStatus400(){
        //Arrange
        var testCourse = createCourse();

        CreateSubcategoryRequest requestDto = new CreateSubcategoryRequest();
        requestDto.setCourseId(testCourse.getId());
        requestDto.setTitle("");
        requestDto.setDescription("Behavioral psychology is the scientific study of how observable behaviors are learned and influenced by interactions with the environment.");
        HttpEntity<CreateSubcategoryRequest> request = new HttpEntity<>(requestDto, headers);

        //Act
        var subcategoryResponse = restTemplate.postForEntity(baseUrl + "/subcategories", request, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, subcategoryResponse.getStatusCode());
        assertTrue(subcategoryRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("getSubcategoriesForCourse returns correct number of subcategories for given course with status 200")
    void testGetSubcategoriesForCourse_whenGetRequest_returnsCorrectNumberOfSubcategoriesWithHttpStatus200() {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory1 = createSubcategory(testCourse);
        var testSubcategory2 = createSubcategory(testCourse);

        HttpEntity<String> getSubcategoriesRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories?courseId=" + testCourse.getId(), HttpMethod.GET, getSubcategoriesRequest, new  ParameterizedTypeReference<List<SubcategoryDto>>() {});

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size(), "Response included wrong amount of subcategories");
        assertEquals(2, subcategoryRepository.findAll().size(), "Database included wrong amount of subcategories");
    }

    @Test
    @DisplayName("getSubcategoriesForCourse returns correct subcategories for given course with status 200")
    void testGetSubcategoriesForCourse_whenGetRequest_returnsCorrectSubcategoriesWithHttpStatus200() {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory1 = createSubcategory(testCourse);
        var testSubcategory2 = createSubcategory(testCourse);

        HttpEntity<String> getSubcategoriesRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories?courseId=" + testCourse.getId(), HttpMethod.GET, getSubcategoriesRequest, new  ParameterizedTypeReference<List<SubcategoryDto>>() {});

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testSubcategory1.getTitle(), response.getBody().get(0).getTitle());
        assertEquals(testSubcategory2.getTitle(), response.getBody().get(1).getTitle());
        assertEquals(testSubcategory1.getTitle(), subcategoryRepository.findById(testSubcategory1.getId()).get().getTitle());
        assertEquals(testSubcategory2.getTitle(), subcategoryRepository.findById(testSubcategory2.getId()).get().getTitle());
    }

    @Test
    @DisplayName("getSubcategory returns requested subcategory with status 200")
    void testGetSubcategory_whenValidDetailsProvided_returnsHttpStatus200() {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        HttpEntity<String> newSubcategoryRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + testSubcategory.getId(), HttpMethod.GET, newSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testSubcategory.getTitle(), response.getBody().getTitle());
        assertEquals(testSubcategory.getTitle(), subcategoryRepository.findById(testSubcategory.getId()).get().getTitle());
    }

    @Test
    @DisplayName("getSubcategory with invalid id returns 404 status")
    void testGetSubcategory_whenInvalidIdProvided_returnsHttpStatus404() {
        //Arrange
        HttpEntity<String> newSubcategoryRequest = new HttpEntity<>(headers);
        var testCourse = createCourse();

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + Long.MAX_VALUE, HttpMethod.GET, newSubcategoryRequest, String.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(subcategoryRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("updateSubcategoryTitle with valid new title updates and returns updated subcategory dto with status code 200")
    void testUpdateSubcategoryTitle_whenValidDetailsProvided_returnsHttpStatus200()  {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        UpdateSubcategoryRequest updateSubcategoryRequest = new UpdateSubcategoryRequest();
        updateSubcategoryRequest.setTitle("newTitle");

        HttpEntity<UpdateSubcategoryRequest> request = new HttpEntity<>(updateSubcategoryRequest, headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + testSubcategory.getId(), HttpMethod.PATCH, request, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updateSubcategoryRequest.getTitle(), response.getBody().getTitle());
        assertEquals(updateSubcategoryRequest.getTitle(), subcategoryRepository.findById(testSubcategory.getId()).get().getTitle());
    }

    @Test
    @DisplayName("updateSubcategoryTitle with invalid new title returns status 400")
    void testUpdateSubcategoryTitle_whenInvalidDetailsProvided_returnsHttpStatus400()  {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        UpdateSubcategoryRequest updateSubcategoryRequest = new UpdateSubcategoryRequest();
        updateSubcategoryRequest.setTitle("");

        HttpEntity<UpdateSubcategoryRequest> request = new HttpEntity<>(updateSubcategoryRequest, headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + testSubcategory.getId(), HttpMethod.PATCH, request, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotEquals(updateSubcategoryRequest.getTitle(), subcategoryRepository.findById(testSubcategory.getId()).get().getTitle());
    }

    @Test
    @DisplayName("updateSubcategoryTitle with invalid subcategory id returns status 404")
    void testUpdateSubcategoryTitle_whenInvalidSubcategoryId_returnsHttpStatus404()  {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        UpdateSubcategoryRequest updateSubcategoryRequest = new UpdateSubcategoryRequest();
        updateSubcategoryRequest.setTitle("NewTitle");

        HttpEntity<UpdateSubcategoryRequest> request = new HttpEntity<>(updateSubcategoryRequest, headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + Long.MAX_VALUE, HttpMethod.PATCH, request, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    @DisplayName("updateSubcategoryDescription returns updated subcategory with status 200")
    void testUpdateSubcategoryDescription_whenValidDetailsProvided_UpdatesAndReturnsHttpStatus200()  {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        UpdateSubcategoryRequest updateSubcategoryRequest = new UpdateSubcategoryRequest();
        updateSubcategoryRequest.setDescription("New description");

        HttpEntity<UpdateSubcategoryRequest> request = new HttpEntity<>(updateSubcategoryRequest, headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + testSubcategory.getId(), HttpMethod.PATCH, request, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updateSubcategoryRequest.getDescription(), response.getBody().getDescription());
        assertEquals(updateSubcategoryRequest.getDescription(), subcategoryRepository.findById(testSubcategory.getId()).get().getDescription());
    }

    @Test
    @DisplayName("updateSubcategoryCourse returns updated subcategory with status 200")
    void testUpdateSubcategoryCourse_whenValidDetailsProvided_UpdatesAndReturnsHttpStatus200()  {
        //Arrange
        var testCourse1 = createCourse();
        var testCourse2 = createCourse();
        var testSubcategory = createSubcategory(testCourse1);

        UpdateSubcategoryRequest updateSubcategoryRequest = new UpdateSubcategoryRequest();
        updateSubcategoryRequest.setCourseId(testCourse2.getId());

        HttpEntity<UpdateSubcategoryRequest> request = new HttpEntity<>(updateSubcategoryRequest, headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + testSubcategory.getId(), HttpMethod.PATCH, request, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getCourseId(), testCourse2.getId());
        assertEquals(testCourse2.getId(), subcategoryRepository.findById(testSubcategory.getId()).get().getCourse().getId());
    }

    @Test
    @DisplayName("updateSubcategoryCourse with invalid new course id returns status 404")
    void testUpdateSubcategoryCourse_whenInvalidNewCourseDetailsProvided_returnsHttpStatus404()  {
        //Arrange
        var testCourse1 = createCourse();
        var testSubcategory = createSubcategory(testCourse1);

        UpdateSubcategoryRequest updateSubcategoryRequest = new UpdateSubcategoryRequest();
        updateSubcategoryRequest.setCourseId(Long.MAX_VALUE);

        HttpEntity<UpdateSubcategoryRequest> request = new HttpEntity<>(updateSubcategoryRequest, headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + testSubcategory.getId(), HttpMethod.PATCH, request, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(updateSubcategoryRequest.getCourseId(), subcategoryRepository.findById(testSubcategory.getId()).get().getCourse());
    }

    @Test
    @DisplayName("updateSubcategoryCourse with invalid subcategory id returns status 404")
    void testUpdateSubcategoryCourse_whenInvalidSubcategoryDetailsProvided_returnsHttpStatus404()  {
        //Arrange
        var testCourse1 = createCourse();
        var testCourse2 = createCourse();
        var testSubcategory = createSubcategory(testCourse1);

        UpdateSubcategoryRequest updateSubcategoryRequest = new UpdateSubcategoryRequest();
        updateSubcategoryRequest.setCourseId(testCourse2.getId());

        HttpEntity<UpdateSubcategoryRequest> request = new HttpEntity<>(updateSubcategoryRequest, headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + Long.MAX_VALUE, HttpMethod.PATCH, request, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(testCourse1.getId(), subcategoryRepository.findById(testSubcategory.getId()).get().getCourse().getId());
    }

    @Test
    @DisplayName("deleteSubcategory with non-existing subcategory id returns 404")
    void testDeleteSubcategory_whenInvalidDetailsProvided_returnsHttpStatus404() {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        HttpEntity<String> deleteSubcategoryRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + Long.MAX_VALUE, HttpMethod.DELETE, deleteSubcategoryRequest, String.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(subcategoryRepository.findById(testSubcategory.getId()).isPresent());
    }

    @Test
    @DisplayName("deleteSubcategory deletes subcategory and returns 200")
    void testDeleteSubcategory_whenValidDetailsProvided_returnsHttpStatus200() {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        HttpEntity<String> deleteSubcategoryRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/subcategories/" + testSubcategory.getId(), HttpMethod.DELETE, deleteSubcategoryRequest, String.class);

        //Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(subcategoryRepository.findById(testSubcategory.getId()).isPresent());
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

    private Course createCourse() {
        Course course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Test Description");
        courseRepository.save(course);
        return course;
    }

    private Subcategory createSubcategory(Course course) {
        Subcategory subcategory = new Subcategory();
        subcategory.setTitle("Test Subcategory");
        subcategory.setDescription("Test Description");
        course.addSubcategory(subcategory);
        subcategoryRepository.save(subcategory);
        return subcategory;
    }

}
