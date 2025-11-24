package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.course.CourseDto;
import com.stenaeke.TLP.dtos.auth.TokenResponse;
import com.stenaeke.TLP.dtos.subcategory.SubcategoryDto;
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
    JSONObject subcategoryJson;

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

        subcategoryJson = new JSONObject();
        subcategoryJson.put("title","Behavioral psychology");
        subcategoryJson.put("description","Behavioral psychology is the scientific study of how observable behaviors are learned and influenced by interactions with the environment.");
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

    //-----------Course endpoints---------------//

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
    @DisplayName("getCourse returns course with correct details and status 200")
    @Order(8)
    void testGetCourse_whenValidCourseId_returnsCourseDtoWithHttpStatus200() {
        //Arrange
        HttpEntity<String> getCourseRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/1", HttpMethod.GET, getCourseRequest, CourseDto.class
        );

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    @DisplayName("getCourse returns 404 when course not found")
    @Order(9)
    void testGetCourse_whenInvalidCourseId_returnsHttpStatus404() {
        //Arrange
        HttpEntity<String> getCourseRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/9999", HttpMethod.GET, getCourseRequest, CourseDto.class
        );

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Get all courses returns correct amount of courses")
    @Order(10)
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
    @Order(11)
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
    @Order(12)
    void testDeleteCourse_whenValidDetailsProvided_returnsHttpStatus200() {
        //Arrange
        HttpEntity<String> deleteRequest = new HttpEntity<>(headers);
        //Act
        var response = restTemplate.exchange(baseUrl + "/course/1", HttpMethod.DELETE, deleteRequest, String.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    //-----------Subcategory endpoints---------------//

    @Test
    @DisplayName("addSubcategory creates subcategory and associates with course and returns status 201")
    @Order(13)
    void testAddSubcategory_whenValidDetailsProvided_returnsHttpStatus201() throws JSONException {
        //Arrange
        HttpEntity<String> newSubcategoryRequest = new HttpEntity<>(subcategoryJson.toString(), headers);

        HttpEntity<String> courseRequest = new HttpEntity<>(courseJson.toString(), headers);
        var courseResponse = restTemplate.postForEntity(baseUrl + "/course", courseRequest, CourseDto.class);

        //Act
        var subcategoryResponse = restTemplate.postForEntity(baseUrl + "/course/2/subcategories", newSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.CREATED, subcategoryResponse.getStatusCode());
        assertEquals(subcategoryResponse.getBody().getCourseId(), courseResponse.getBody().getId());
        assertEquals(subcategoryJson.get("title"),subcategoryResponse.getBody().getTitle());
        assertEquals(subcategoryJson.get("description"),subcategoryResponse.getBody().getDescription());
    }

    @Test
    @DisplayName("addSubcategory with empty name returns status 400")
    @Order(14)
    void testAddSubcategory_whenEmptyNameProvided_returnsHttpStatus400() throws JSONException {
        //Arrange
        subcategoryJson.put("title","");
        HttpEntity<String> newSubcategoryRequest = new HttpEntity<>(subcategoryJson.toString(), headers);

        //Act
        var subcategoryResponse = restTemplate.postForEntity(baseUrl + "/course/2/subcategories", newSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, subcategoryResponse.getStatusCode());
    }

    @Test
    @DisplayName("getSubcategoriesForCourse returns correct number of subcategories for given course with status 200")
    @Order(15)
    void testGetSubcategoriesForCourse_whenGetRequest_returnsCorrectNumberOfSubcategoriesWithHttpStatus200() {
        //Arrange
        HttpEntity<String> getSubcategoriesRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/2/subcategories", HttpMethod.GET, getSubcategoriesRequest, new  ParameterizedTypeReference<List<SubcategoryDto>>() {});

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getSubcategoriesForCourse returns correct subcategories for given course with status 200")
    @Order(16)
    void testGetSubcategoriesForCourse_whenGetRequest_returnsCorrectSubcategoriesWithHttpStatus200() throws JSONException {
        //Arrange
        HttpEntity<String> getSubcategoriesRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/2/subcategories", HttpMethod.GET, getSubcategoriesRequest, new  ParameterizedTypeReference<List<SubcategoryDto>>() {});

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subcategoryJson.get("title"), response.getBody().get(0).getTitle());
    }

    @Test
    @DisplayName("getSubcategory returns requested subcategory with status 200")
    @Order(17)
    void testGetSubcategory_whenValidDetailsProvided_returnsHttpStatus200() throws JSONException {
        //Arrange
        HttpEntity<String> newSubcategoryRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/2/subcategories/1", HttpMethod.GET, newSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subcategoryJson.get("title"), response.getBody().getTitle());
    }

    @Test
    @DisplayName("getSubcategory with invalid id returns 404 status")
    @Order(18)
    void testGetSubcategory_whenInvalidIdProvided_returnsHttpStatus404() throws JSONException {
        //Arrange
        HttpEntity<String> newSubcategoryRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/2/subcategories/100", HttpMethod.GET, newSubcategoryRequest, String.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("updateSubcategoryTitle with valid new title updates and returns updated subcategory dto with status code 200")
    @Order(19)
    void testUpdateSubcategoryTitle_whenValidDetailsProvided_returnsHttpStatus200() throws JSONException {
        //Arrange
        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("title", "newTitle");

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/2/subcategories/1/title", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedSubcategoryJson.get("title"), response.getBody().getTitle());
    }

    @Test
    @DisplayName("updateSubcategoryTitle with invalid new title updates and returns status 400")
    @Order(20)
    void testUpdateSubcategoryTitle_whenInvalidDetailsProvided_returnsHttpStatus400() throws JSONException {
        //Arrange
        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("title", "");

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/2/subcategories/1/title", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("updateSubcategoryTitle with invalid subcategory id returns status 404")
    @Order(21)
    void testUpdateSubcategoryTitle_whenInvalidSubcategoryId_returnsHttpStatus404() throws JSONException {
        //Arrange
        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("title", "newTitle");

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/2/subcategories/100/title", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("updateSubcategoryTitle with invalid course id returns status 404")
    @Order(22)
    void testUpdateSubcategoryTitle_whenInvalidCourseId_returnsHttpStatus404() throws JSONException {
        //Arrange
        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("title", "newTitle");

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/200/subcategories/1/title", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("updateSubcategoryDescription returns updated subcategory with status 200")
    @Order(23)
    void testUpdateSubcategoryDescription_whenValidDetailsProvided_returnsHttpStatus200() throws JSONException {
        //Arrange
        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("description", "new description");

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/2/subcategories/1/description", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedSubcategoryJson.get("description"), response.getBody().getDescription());
    }

    @Test
    @DisplayName("updateSubcategoryCourse returns updated subcategory with status 200")
    @Order(24)
    void testUpdateSubcategoryCourse_whenValidDetailsProvided_returnsHttpStatus200() throws JSONException {
        //Arrange
        courseJson.put("title", "Another Course");
        courseJson.put("description", "description of the course");
        HttpEntity<String> courseRequest = new HttpEntity<>(courseJson.toString(), headers);
        var courseDto = restTemplate.postForEntity(baseUrl + "/course", courseRequest, CourseDto.class);

        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("courseId", courseDto.getBody().getId());

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/2/subcategories/1/course", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getCourseId(), courseDto.getBody().getId());
    }

    @Test
    @DisplayName("updateSubcategoryCourse with invalid new course id returns status 404")
    @Order(25)
    void testUpdateSubcategoryCourse_whenInvalidNewCourseDetailsProvided_returnsHttpStatus200() throws JSONException {
        //Arrange

        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("courseId", 25);

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/3/subcategories/1/course", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("updateSubcategoryCourse with invalid course id returns status 404")
    @Order(26)
    void testUpdateSubcategoryCourse_whenInvalidCourseDetailsProvided_returnsHttpStatus200() throws JSONException {
        //Arrange
        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("courseId", 3);

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/200/subcategories/1/course", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("updateSubcategoryCourse with invalid subcategory id returns status 404")
    @Order(27)
    void testUpdateSubcategoryCourse_whenInvalidSubcategoryDetailsProvided_returnsHttpStatus200() throws JSONException {
        //Arrange
        JSONObject updatedSubcategoryJson = new JSONObject();
        updatedSubcategoryJson.put("courseId", 3);

        HttpEntity<String> updateSubcategoryRequest = new HttpEntity<>(updatedSubcategoryJson.toString(), headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/3/subcategories/100/course", HttpMethod.PUT, updateSubcategoryRequest, SubcategoryDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("deleteSubcategory with non-existing subcategory id returns 404")
    @Order(28)
    void testDeleteSubcategory_whenInvalidDetailsProvided_returnsHttpStatus404() {
        //Arrange
        HttpEntity<String> deleteSubcategoryRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/3/subcategories/10000", HttpMethod.DELETE, deleteSubcategoryRequest, String.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("deleteSubcategory deletes subcategory and returns 200")
    @Order(29)
    void testDeleteSubcategory_whenValidDetailsProvided_returnsHttpStatus200() {
        //Arrange
        HttpEntity<String> deleteSubcategoryRequest = new HttpEntity<>(headers);

        //Act
        var response = restTemplate.exchange(baseUrl + "/course/3/subcategories/1", HttpMethod.DELETE, deleteSubcategoryRequest, String.class);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


































}