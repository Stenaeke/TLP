package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.course.CreateCourseDto;
import com.stenaeke.TLP.dtos.course.CourseDto;
import com.stenaeke.TLP.dtos.course.UpdateCourseDescriptionDto;
import com.stenaeke.TLP.dtos.course.UpdateCourseTitleDto;
import com.stenaeke.TLP.dtos.subcategory.CreateSubcategoryRequest;
import com.stenaeke.TLP.exceptions.ResourceNotFoundException;
import com.stenaeke.TLP.mappers.CourseMapper;
import com.stenaeke.TLP.mappers.SubcategoryMapper;
import com.stenaeke.TLP.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseMapper courseMapper;
    private final SubcategoryMapper subcategoryMapper;


    @GetMapping
    private ResponseEntity<List<CourseDto>> getAllCourses(){
        var courses = courseService.getAllCourses();

        if(courses.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return ResponseEntity.ok(courses.stream().map(courseMapper::courseToCourseDto).collect(Collectors.toList()));
    }

    @PostMapping
    private ResponseEntity<CourseDto> addCourse(@Valid @RequestBody CreateCourseDto createCourseDto,
                                                UriComponentsBuilder uriComponentsBuilder
    ){
        var createdCourseDto = courseService.createCourse(createCourseDto);

        var uri = uriComponentsBuilder.path("/course/{courseId}").buildAndExpand(createdCourseDto.getId()).toUri();
        return ResponseEntity.created(uri).body(createdCourseDto);
    }

    @PutMapping("/{courseId}/title")
    private ResponseEntity<CourseDto> updateCourseTitle(@Valid @RequestBody UpdateCourseTitleDto updateCourseTitleDto, @PathVariable int courseId){
        try {
            return ResponseEntity.ok(courseService.updateCourse(updateCourseTitleDto, courseId));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{courseId}/description")
    private ResponseEntity<CourseDto> updateCourseDescription(@Valid @RequestBody UpdateCourseDescriptionDto updateCourseDescriptionDto, @PathVariable int courseId){
        try {
            return ResponseEntity.ok(courseService.updateCourse(updateCourseDescriptionDto, courseId));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{courseId}")
    private ResponseEntity<CourseDto> deleteCourse(@PathVariable int courseId){
        try {
            courseService.deleteCourse(courseId);
            return ResponseEntity.ok().build();
        } catch (
                ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/{courseId}")
    private ResponseEntity<?> addSubcategory(@PathVariable int courseId,
                                             @Valid @RequestBody CreateSubcategoryRequest createSubcategoryRequest,
                                             UriComponentsBuilder uriComponentsBuilder
    ) {
        try {
            var subcategory = courseService.addSubcategoryToCourse(courseId, createSubcategoryRequest);
            var subDto = subcategoryMapper.subcategoryToSubcategoryDto(subcategory);
            var uri = uriComponentsBuilder.path("/course/{courseId}/").buildAndExpand(subDto.getId()).toUri();
            return ResponseEntity.created(uri).body(subDto);

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}