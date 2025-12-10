package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.course.*;
import com.stenaeke.TLP.dtos.subcategory.*;
import com.stenaeke.TLP.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses(){
        var courseDtos = courseService.getAllCourses();
        return ResponseEntity.ok(courseDtos);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourse(@PathVariable Long courseId){
        var courseDto = courseService.getCourse(courseId);
        return ResponseEntity.ok(courseDto);
    }

    @PostMapping
    public ResponseEntity<CourseDto> addCourse(@Valid @RequestBody CreateCourseDto createCourseDto,
                                                UriComponentsBuilder uriComponentsBuilder
    ){
        var createdCourseDto = courseService.createCourse(createCourseDto);

        var uri = uriComponentsBuilder.path("/courses/{courseId}").buildAndExpand(createdCourseDto.getId()).toUri(); //TODO:Double check
        return ResponseEntity.created(uri).body(createdCourseDto);
    }

    @PatchMapping("/{courseId}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable long courseId, @Valid @RequestBody UpdateCourseRequest updateCourseRequest) {
        var updatedCourseDto = courseService.updateCourse(courseId, updateCourseRequest);

        return ResponseEntity.ok(updatedCourseDto);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId){
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

}