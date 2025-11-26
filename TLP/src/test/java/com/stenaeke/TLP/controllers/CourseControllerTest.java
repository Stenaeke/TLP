package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.course.CourseDto;
import com.stenaeke.TLP.dtos.auth.TokenResponse;
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

    //-----------Course endpoints---------------//

    @Test
    @DisplayName("add course return status code 201 created")
    void testAddCourse_whenValidDetailsProvided_returnsDtoAndHttpStatus201() throws JSONException {
        //Arrange
        JSONObject courseJson = new JSONObject();
        courseJson.put("title","Psychology");
        courseJson.put("description","Psychology is the course of Psychology");

        HttpEntity<String> courseRequest = new HttpEntity<>(courseJson.toString(), headers);

        //Act
        var courseDto = restTemplate.postForEntity(baseUrl + "/course", courseRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.CREATED, courseDto.getStatusCode());
        assertEquals(1, courseRepository.findAll().size());
        assertEquals(courseRepository.findById(courseDto.getBody().getId()).get().getTitle(), courseDto.getBody().getTitle());
    }

    @Test
    @DisplayName("add course with empty name return status code 400 bad request")
    void testAddCourse_whenInvalidNameProvided_returnsStatusCode400() throws JSONException {
        //Arrange
        JSONObject courseJson = new JSONObject();
        courseJson.put("title","");
        courseJson.put("description","Psychology is the course of Psychology");

        HttpEntity<String> courseRequest = new HttpEntity<>(courseJson.toString(), headers);

        //Act
        var courseDto = restTemplate.postForEntity(baseUrl + "/course", courseRequest, String.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, courseDto.getStatusCode());
        assertEquals(0, courseRepository.findAll().size());
    }

    @Test
    @DisplayName("updateCourseTitle updates title and returns status code 200 with updated Dto")
    void testUpdateCourseTitle_whenValidDetailsProvided_returnsDtoAndHttpStatus200() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        JSONObject updateJson = new JSONObject();
        updateJson.put("title", "newTitle");
        HttpEntity<String> updateRequest = new HttpEntity<>(updateJson.toString(), headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/title", HttpMethod.PUT, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.OK, courseDto.getStatusCode());
        assertEquals(updateJson.get("title"), courseDto.getBody().getTitle());
        assertEquals(updateJson.getString("title"), courseRepository.findById(testCourse.getId()).get().getTitle());
    }

    @Test
    @DisplayName("updateCourseTitle with empty title returns status code 400")
    void testUpdateCourseTitle_whenEmptyDetailsProvided_returnsHttpStatus400() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        JSONObject updateJson = new JSONObject();
        updateJson.put("title", "");
        HttpEntity<String> updateRequest = new HttpEntity<>(updateJson.toString(), headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/title", HttpMethod.PUT, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, courseDto.getStatusCode());
        assertNotEquals(updateJson.getString("title"), courseRepository.findById(testCourse.getId()).get().getTitle());
    }

    @Test
    @DisplayName("updateCourseDescription updates Description and returns status code 200 with updated Dto")
    void testUpdateCourseDescription_whenValidDetailsProvided_returnsDtoAndHttpStatus200() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        JSONObject updateJson = new JSONObject();
        updateJson.put("description", "NewDescription");
        HttpEntity<String> updateRequest = new HttpEntity<>(updateJson.toString(), headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/description", HttpMethod.PUT, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.OK, courseDto.getStatusCode());
        assertEquals(updateJson.get("description"), courseDto.getBody().getDescription());
        assertEquals(updateJson.getString("description"), courseRepository.findById(testCourse.getId()).get().getDescription());
    }

    @Test
    @DisplayName("updateCourseDescription with empty Description returns status code 400")
    void testUpdateCourseDescription_whenEmptyDetailsProvided_returnsHttpStatus400() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        JSONObject updateJson = new JSONObject();
        updateJson.put("description", "");
        HttpEntity<String> updateRequest = new HttpEntity<>(updateJson.toString(), headers);

        //Act
        var courseDto = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/description", HttpMethod.PUT, updateRequest, CourseDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, courseDto.getStatusCode());
        assertNotEquals(updateJson.getString("description"), courseRepository.findById(testCourse.getId()).get().getDescription());
    }

    @Test
    @DisplayName("getCourse returns course with correct details and status 200")
    void testGetCourse_whenValidCourseId_returnsCourseDtoWithHttpStatus200() {
        //Arrange
        var testCourse = createCourse();
        HttpEntity<String> getCourseRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId(), HttpMethod.GET, getCourseRequest, CourseDto.class);

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
        var response = restTemplate.exchange(baseUrl + "/course/10", HttpMethod.GET, getCourseRequest, CourseDto.class);

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
        var courses = restTemplate.exchange(baseUrl + "/course", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<CourseDto>>() {});

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
        var response = restTemplate.exchange(baseUrl + "/course/10", HttpMethod.DELETE, deleteRequest, String.class);

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
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId(), HttpMethod.DELETE, deleteRequest, String.class);

        //Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(courseRepository.findById(testCourse.getId()).isPresent());
    }

    //-----------Subcategory endpoints---------------//

    @Test
    @DisplayName("addSubcategory creates subcategory and associates with course and returns status 201")
    void testAddSubcategory_whenValidDetailsProvided_returnsHttpStatus201() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        JSONObject subcategoryJson = new JSONObject();
        subcategoryJson.put("title","Behavioral psychology");
        subcategoryJson.put("description","Behavioral psychology is the scientific study of how observable behaviors are learned and influenced by interactions with the environment.");
        HttpEntity<String> newSubcategoryRequest = new HttpEntity<>(subcategoryJson.toString(), headers);

        //Act
        var subcategoryResponse = restTemplate.postForEntity(baseUrl + "/course/" + testCourse.getId() + "/subcategories", newSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.CREATED, subcategoryResponse.getStatusCode());
        assertEquals(subcategoryResponse.getBody().getCourseId(), testCourse.getId());
        assertEquals(subcategoryJson.get("title"),subcategoryResponse.getBody().getTitle());
        assertEquals(subcategoryJson.get("description"),subcategoryResponse.getBody().getDescription());
        assertEquals(1, subcategoryRepository.findAll().size());
        assertEquals(subcategoryRepository.findById(subcategoryResponse.getBody().getId()).get().getTitle(), subcategoryResponse.getBody().getTitle());
    }

    @Test
    @DisplayName("addSubcategory with empty name returns status 400")
    void testAddSubcategory_whenEmptyNameProvided_returnsHttpStatus400() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        JSONObject subcategoryJson = new JSONObject();
        subcategoryJson.put("title","");
        subcategoryJson.put("description","Behavioral psychology is the scientific study of how observable behaviors are learned and influenced by interactions with the environment.");
        HttpEntity<String> newSubcategoryRequest = new HttpEntity<>(subcategoryJson.toString(), headers);

        //Act
        var subcategoryResponse = restTemplate.postForEntity(baseUrl + "/course/" + testCourse.getId() + "/subcategories", newSubcategoryRequest, SubcategoryDto.class);

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
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/subcategories", HttpMethod.GET, getSubcategoriesRequest, new  ParameterizedTypeReference<List<SubcategoryDto>>() {});

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
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/subcategories", HttpMethod.GET, getSubcategoriesRequest, new  ParameterizedTypeReference<List<SubcategoryDto>>() {});

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
        var response = restTemplate.exchange(baseUrl + "/course/" + + testCourse.getId() + "/subcategories/" + testSubcategory.getId(), HttpMethod.GET, newSubcategoryRequest, SubcategoryDto.class);

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
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/subcategories/10", HttpMethod.GET, newSubcategoryRequest, String.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(subcategoryRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("updateSubcategoryTitle with valid new title updates and returns updated subcategory dto with status code 200")
    void testUpdateSubcategoryTitle_whenValidDetailsProvided_returnsHttpStatus200() throws JSONException {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("title", "newTitle");

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/subcategories/" + testSubcategory.getId() + "/title", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedSubcategoryJson.get("title"), response.getBody().getTitle());
        assertEquals(updatedSubcategoryJson.get("title"), subcategoryRepository.findById(testSubcategory.getId()).get().getTitle());
    }

    @Test
    @DisplayName("updateSubcategoryTitle with invalid new title returns status 400")
    void testUpdateSubcategoryTitle_whenInvalidDetailsProvided_returnsHttpStatus400() throws JSONException {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("title", "");

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/subcategories/" + testSubcategory.getId() + "/title", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotEquals(subcategoryRepository.findById(testSubcategory.getId()).get().getTitle(), updatedSubcategoryJson.get("title"));
    }

    @Test
    @DisplayName("updateSubcategoryTitle with invalid subcategory id returns status 404")
    void testUpdateSubcategoryTitle_whenInvalidSubcategoryId_returnsHttpStatus404() throws JSONException {
        //Arrange
        var testCourse = createCourse();

        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("title", "newTitle");

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/subcategories/10/title", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(subcategoryRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("updateSubcategoryTitle with invalid course id returns status 404")
    void testUpdateSubcategoryTitle_whenInvalidCourseId_returnsHttpStatus404() throws JSONException {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("title", "newTitle");

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/10/subcategories/" + testSubcategory.getId() + "/title", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(subcategoryRepository.findById(testSubcategory.getId()).get().getTitle(), updatedSubcategoryJson.get("title"));
    }

    @Test
    @DisplayName("updateSubcategoryDescription returns updated subcategory with status 200")
    void testUpdateSubcategoryDescription_whenValidDetailsProvided_UpdatesAndReturnsHttpStatus200() throws JSONException {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);

        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("description", "new description");

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/subcategories/" + testSubcategory.getId() + "/description", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedSubcategoryJson.get("description"), response.getBody().getDescription());
        assertEquals(subcategoryRepository.findById(testSubcategory.getId()).get().getDescription(), updatedSubcategoryJson.get("description"));
    }

    @Test
    @DisplayName("updateSubcategoryCourse returns updated subcategory with status 200")
    void testUpdateSubcategoryCourse_whenValidDetailsProvided_UpdatesAndReturnsHttpStatus200() throws JSONException {
        //Arrange
        var testCourse1 = createCourse();
        var testCourse2 = createCourse();
        var testSubcategory = createSubcategory(testCourse1);

        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("courseId", testCourse2.getId());

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse1.getId() + "/subcategories/" + testSubcategory.getId() + "/course", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getCourseId(), testCourse2.getId());
        assertEquals(testCourse2.getId(), subcategoryRepository.findById(testSubcategory.getId()).get().getCourse().getId());
    }

    @Test
    @DisplayName("updateSubcategoryCourse with invalid new course id returns status 404")
    void testUpdateSubcategoryCourse_whenInvalidNewCourseDetailsProvided_returnsHttpStatus404() throws JSONException {
        //Arrange
        var testCourse = createCourse();
        var testSubcategory = createSubcategory(testCourse);


        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("courseId", 20);

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/subcategories/" + testSubcategory.getId() + "/course", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(subcategoryRepository.findById(testSubcategory.getId()).get().getCourse(), updatedSubcategoryJson.get("courseId"));
    }

    @Test
    @DisplayName("updateSubcategoryCourse with invalid course id returns status 404")
    void testUpdateSubcategoryCourse_whenInvalidCourseDetailsProvided_returnsHttpStatus404() throws JSONException {
        //Arrange
        var testCourse1 = createCourse();
        var testCourse2 = createCourse();
        var testSubcategory = createSubcategory(testCourse1);

        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("courseId", testCourse2.getId());

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/10/subcategories/" + testSubcategory.getId() + "/course", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(testCourse1.getId(), subcategoryRepository.findById(testSubcategory.getId()).get().getCourse().getId());
    }

    @Test
    @DisplayName("updateSubcategoryCourse with invalid subcategory id returns status 404")
    void testUpdateSubcategoryCourse_whenInvalidSubcategoryDetailsProvided_returnsHttpStatus404() throws JSONException {
        //Arrange
        var testCourse1 = createCourse();
        var testCourse2 = createCourse();
        var testSubcategory = createSubcategory(testCourse1);


        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("courseId", testCourse2.getId());

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse1.getId() + "/subcategories/10/course", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/subcategories/10", HttpMethod.DELETE, deleteSubcategoryRequest, String.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        var response = restTemplate.exchange(baseUrl + "/course/" + testCourse.getId() + "/subcategories/" + testSubcategory.getId(), HttpMethod.DELETE, deleteSubcategoryRequest, String.class);

        //Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(subcategoryRepository.findById(testSubcategory.getId()).isPresent());
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

    private Subcategory createSubcategory(Course course) {
        Subcategory subcategory = new Subcategory();
        subcategory.setTitle("Test Subcategory");
        subcategory.setDescription("Test Description");
        course.addSubcategory(subcategory);
        subcategoryRepository.save(subcategory);
        return subcategory;
        }
































}