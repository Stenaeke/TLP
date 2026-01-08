package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.domain.Module;
import com.stenaeke.TLP.dtos.auth.TokenResponse;
import com.stenaeke.TLP.dtos.module.CreateModuleDto;
import com.stenaeke.TLP.dtos.module.ModuleDto;
import com.stenaeke.TLP.dtos.module.UpdateModuleDto;
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
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ModuleControllerTest {

    @Autowired
    ModuleRepository moduleRepository;
    @Autowired
    SubcategoryRepository subcategoryRepository;
    @Autowired
    CourseRepository courseRepository;
    Course testcourse;
    Subcategory testsubcategory;
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
        testcourse = createCourse();
        testsubcategory = createSubcategory(testcourse);

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
        moduleRepository.deleteAll();
    }

    @Test
    @DisplayName("The postgresSQL container is running")
    void postgreSQLContainerIsRunning() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    @DisplayName("addModule creates module and associates with subcategory and returns status 201")
    void testAddModule_whenValidDetailsProvided_ReturnsCreatedModuleWithStatus201() {
        //Arrange
        CreateModuleDto createModuleDto = new CreateModuleDto();
        createModuleDto.setTitle("test");
        createModuleDto.setContent("testing addModule");
        createModuleDto.setPublished(true);
        createModuleDto.setSubcategoryId(testsubcategory.getId());
        HttpEntity<CreateModuleDto> request = new HttpEntity<>(createModuleDto, headers);


        //Act
        var moduleResponse = restTemplate.postForEntity(baseUrl + "/modules", request, ModuleDto.class);

        //Assert
        assertEquals(HttpStatus.CREATED, moduleResponse.getStatusCode());
        assertEquals(moduleResponse.getBody().getSubcategoryId(), testsubcategory.getId());
        assertEquals(createModuleDto.getTitle(),moduleResponse.getBody().getTitle());
        assertEquals(createModuleDto.getContent(),moduleResponse.getBody().getContent());
        assertEquals(1, moduleRepository.findAll().size());
        assertEquals(moduleRepository.findById(moduleResponse.getBody().getId()).get().getTitle(), moduleResponse.getBody().getTitle());
    }

    @Test
    @DisplayName("Addmodule returns status 400 when empty name provided")
    void testAddModule_whenEmptyNameProvided_ReturnHttpStatus400() {
        //Arrange
        CreateModuleDto createModuleDto = new CreateModuleDto();
        createModuleDto.setTitle("");
        createModuleDto.setContent("testing addModule");
        createModuleDto.setPublished(true);
        createModuleDto.setSubcategoryId(testsubcategory.getId());
        HttpEntity<CreateModuleDto> request = new HttpEntity<>(createModuleDto, headers);

        //Act
        var moduleResponse = restTemplate.postForEntity(baseUrl + "/modules", request, ModuleDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, moduleResponse.getStatusCode());
        assertTrue(moduleRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("getModule returns moduleDto and http status 200")
    void testGetModule_whenValidDetailsProvided_returnDtoAndHttpStatus200() {
        //Arrange
        Module testModule = createModule(testsubcategory);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        //Act
        var moduleResponse = restTemplate.exchange(baseUrl + "/modules/" + testModule.getId(), HttpMethod.GET, request, ModuleDto.class);

        //Assert
        assertEquals(HttpStatus.OK, moduleResponse.getStatusCode());
        assertEquals(moduleResponse.getBody().getId(), testModule.getId());
        assertEquals(testModule.getTitle(), moduleRepository.findById(moduleResponse.getBody().getId()).get().getTitle());
    }

    @Test
    @DisplayName("getModule with invalid Id returns http status 404")
    void testGetModule_whenInvalidIdProvided_returnHttpStatus404() {
        //Arrange
        HttpEntity<Void> request = new HttpEntity<>(headers);

        //Act
        var moduleResponse = restTemplate.exchange(baseUrl + "/modules/" + Long.MAX_VALUE, HttpMethod.GET, request, ModuleDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, moduleResponse.getStatusCode());
        assertTrue(moduleRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("getModulesBySubcategoryId returns modules belonging to subcategory and http status 200")
    void testGetModulesBySubcategoryId_whenValidSubcategoryIdProvided_ReturnSubcategoriesModulesAndHttpStatus200() {
        //Arrange
        HttpEntity<Void> request = new HttpEntity<>(headers);
        createModule(testsubcategory);
        createModule(testsubcategory);

        //Act
        var moduleListResponse = restTemplate.exchange(baseUrl + "/modules?subcategoryId=" + testsubcategory.getId(), HttpMethod.GET, request, List.class);

        //Assert
        assertEquals(HttpStatus.OK, moduleListResponse.getStatusCode());
        assertEquals(moduleListResponse.getBody().size(), moduleRepository.findBySubcategoryId(testsubcategory.getId()).size());
    }

    @Test
    @DisplayName("updateModule with valid new title updates and returns updated module dto with status code 200")
    void testUpdateModule_whenValidNewTitleProvided_returnsHttpStatus200() {
        //Arrange
        UpdateModuleDto updateModuleDto = new UpdateModuleDto();
        updateModuleDto.setTitle("New title");
        HttpEntity<UpdateModuleDto> request = new  HttpEntity<>(updateModuleDto, headers);
        Module testModule = createModule(testsubcategory);

        //Act
        var updateModuleResponse = restTemplate.exchange(baseUrl + "/modules/" + testModule.getId(), HttpMethod.PATCH, request, ModuleDto.class);

        //Assert
        assertEquals(HttpStatus.OK, updateModuleResponse.getStatusCode());
        assertEquals(moduleRepository.findById(testModule.getId()).get().getTitle(), updateModuleResponse.getBody().getTitle());
    }

    @Test
    @DisplayName("updateModule with valid new published updates and returns updated module dto with status code 200")
    void testUpdateModule_whenValidNewPublishedProvided_returnsHttpStatus200() {
        //Arrange
        UpdateModuleDto updateModuleDto = new UpdateModuleDto();
        updateModuleDto.setPublished(false);
        HttpEntity<UpdateModuleDto> request = new  HttpEntity<>(updateModuleDto, headers);
        Module testModule = createModule(testsubcategory);

        //Act
        var updateModuleResponse = restTemplate.exchange(baseUrl + "/modules/" + testModule.getId(), HttpMethod.PATCH, request, ModuleDto.class);

        //Assert
        assertEquals(HttpStatus.OK, updateModuleResponse.getStatusCode());
        assertEquals(moduleRepository.findById(testModule.getId()).get().getPublished(), updateModuleResponse.getBody().getPublished());
    }

    @Test
    @DisplayName("updateModule with valid new content updates and returns updated module dto with status code 200")
    void testUpdateModule_whenValidNewContentProvided_returnsHttpStatus200() {
        //Arrange
        UpdateModuleDto updateModuleDto = new UpdateModuleDto();
        updateModuleDto.setContent("The album is a sampler of material from the first four Kraftwerk albums – Kraftwerk, Kraftwerk 2, Ralf und Florian, Autobahn – and includes some versions of tracks that " +
                "had been edited down for release on singles, such as \"Autobahn\", as well as simple excerpts from longer album tracks, such as \"Kling-Klang\". Only the pair of tracks from the Ralf und Florian " +
                "album, \"Tongebirge\" and \"Kristallo\", are entirely free of cuts.\n" + "Track selection was by Alan Cowderoy (later an A&R manager at Stiff Records) and sound engineering is credited to Steve Brown, " +
                "though it is unclear if they created the edited versions of the two tracks released as singles. The three-minute version of \"Autobahn\" (edited down from an original album length of 22:30) is the same as " +
                "the three-minute UK single mix, which had been a top 20 hit in the UK. A different 3:28 edit, released in 1974, received considerable airplay in the US. \"Comet Melody 2\" failed to chart.\n" +
                "The album was issued on vinyl, 8-Track and cassette and deleted in 1980.");
        HttpEntity<UpdateModuleDto> request = new  HttpEntity<>(updateModuleDto, headers);
        Module testModule = createModule(testsubcategory);

        //Act
        var updateModuleResponse = restTemplate.exchange(baseUrl + "/modules/" + testModule.getId(), HttpMethod.PATCH, request, ModuleDto.class);

        //Assert
        assertEquals(HttpStatus.OK, updateModuleResponse.getStatusCode());
        assertEquals(moduleRepository.findById(testModule.getId()).get().getContent(), updateModuleResponse.getBody().getContent());
    }

    @Test
    @DisplayName("updateModule with valid new subcategory updates and returns updated module dto with status code 200")
    void testUpdateModule_whenValidNewSubcategoryProvided_returnsHttpStatus200() {
        //Arrange
        Subcategory subcategory2 = createSubcategory(testcourse);
        UpdateModuleDto updateModuleDto = new UpdateModuleDto();
        updateModuleDto.setSubcategoryId(subcategory2.getId());
        HttpEntity<UpdateModuleDto> request = new  HttpEntity<>(updateModuleDto, headers);
        Module testModule = createModule(testsubcategory);

        //Act
        var updateModuleResponse = restTemplate.exchange(baseUrl + "/modules/" + testModule.getId(), HttpMethod.PATCH, request, ModuleDto.class);

        //Assert
        assertEquals(HttpStatus.OK, updateModuleResponse.getStatusCode());
        assertEquals(moduleRepository.findById(testModule.getId()).get().getSubcategory().getId(), updateModuleResponse.getBody().getSubcategoryId());
    }

    @Test
    @DisplayName("updateModule with invalid new title updates and returns updated module dto with status code 400")
    void testUpdateModule_whenInvalidNewTitleProvided_returnsHttpStatus400() {
        //Arrange
        UpdateModuleDto updateModuleDto = new UpdateModuleDto();
        updateModuleDto.setTitle(" ");
        HttpEntity<UpdateModuleDto> request = new  HttpEntity<>(updateModuleDto, headers);
        Module testModule = createModule(testsubcategory);

        //Act
        var updateModuleResponse = restTemplate.exchange(baseUrl + "/modules/" + testModule.getId(), HttpMethod.PATCH, request, ModuleDto.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, updateModuleResponse.getStatusCode());
        assertNotEquals(moduleRepository.findById(testModule.getId()).get().getTitle(), updateModuleDto.getTitle());
    }

    @Test
    @DisplayName("updateModule with invalid new Subcategory updates and returns updated module dto with status code 400")
    void testUpdateModule_whenInvalidNewSubcategoryProvided_returnsHttpStatus400() {
        //Arrange
        UpdateModuleDto updateModuleDto = new UpdateModuleDto();
        updateModuleDto.setSubcategoryId(Long.MAX_VALUE);
        HttpEntity<UpdateModuleDto> request = new  HttpEntity<>(updateModuleDto, headers);
        Module testModule = createModule(testsubcategory);

        //Act
        var updateModuleResponse = restTemplate.exchange(baseUrl + "/modules/" + testModule.getId(), HttpMethod.PATCH, request, ModuleDto.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, updateModuleResponse.getStatusCode());
        assertNotEquals(moduleRepository.findById(testModule.getId()).get().getSubcategory().getId(), updateModuleDto.getSubcategoryId());
    }

    @Test
    @DisplayName("deleteModule with valid module id provided deletes module and returns status code 204")
    void testDeleteModule_whenValidModuleIdProvided_returnsHttpStatus204() {
        //Arrange
        HttpEntity<Void> request = new  HttpEntity<>(headers);
        Module testModule = createModule(testsubcategory);

        //Act
        var deleteModuleResponse = restTemplate.exchange(baseUrl + "/modules/" + testModule.getId(), HttpMethod.DELETE, request, Void.class);

        //Assert
        assertEquals(HttpStatus.NO_CONTENT, deleteModuleResponse.getStatusCode());
        assertFalse(moduleRepository.findById(testModule.getId()).isPresent());
    }

    @Test
    @DisplayName("deleteModule with valid module id provided deletes module and returns status code 204")
    void testDeleteModule_whenInvalidModuleIdProvided_returnsHttpStatus404() {
        //Arrange
        HttpEntity<Void> request = new  HttpEntity<>(headers);

        //Act
        var deleteModuleResponse = restTemplate.exchange(baseUrl + "/modules/" + Long.MAX_VALUE, HttpMethod.DELETE, request, Void.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, deleteModuleResponse.getStatusCode());
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

    private Module createModule(Subcategory subcategory) {
        Module module = new Module();
        module.setTitle("Test Module");
        module.setContent("Test Content");
        module.setPublished(true);
        module.setUpdatedAt(OffsetDateTime.now());
        module.setCreatedAt(OffsetDateTime.now());
        module.setSubcategory(subcategory);
        moduleRepository.save(module);
        return module;
    }

}