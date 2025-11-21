package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.course.CreateCourseDto;
import com.stenaeke.TLP.dtos.course.CourseDto;
import com.stenaeke.TLP.dtos.course.UpdateCourseDescriptionDto;
import com.stenaeke.TLP.dtos.course.UpdateCourseTitleDto;
import com.stenaeke.TLP.dtos.subcategory.*;
import com.stenaeke.TLP.exceptions.ResourceNotFoundException;
import com.stenaeke.TLP.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    //-----------Course endpoints---------------//

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses(){

        try {
            var courseDtos = courseService.getAllCourses();
            if(courseDtos.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return ResponseEntity.ok(courseDtos);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourse(@PathVariable("courseId") int courseId){
        try {
            var courseDto = courseService.getCourse(courseId);
            return ResponseEntity.ok(courseDto);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<CourseDto> addCourse(@Valid @RequestBody CreateCourseDto createCourseDto,
                                                UriComponentsBuilder uriComponentsBuilder
    ){
        var createdCourseDto = courseService.createCourse(createCourseDto);

        var uri = uriComponentsBuilder.path("/course/{courseId}").buildAndExpand(createdCourseDto.getId()).toUri();
        return ResponseEntity.created(uri).body(createdCourseDto);
    }

    @PutMapping("/{courseId}/title")
    public ResponseEntity<CourseDto> updateCourseTitle(@Valid @RequestBody UpdateCourseTitleDto updateCourseTitleDto, @PathVariable int courseId){
        try {
            return ResponseEntity.ok(courseService.updateCourse(updateCourseTitleDto, courseId));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{courseId}/description")
    public ResponseEntity<CourseDto> updateCourseDescription(@Valid @RequestBody UpdateCourseDescriptionDto updateCourseDescriptionDto, @PathVariable int courseId){
        try {
            return ResponseEntity.ok(courseService.updateCourse(updateCourseDescriptionDto, courseId));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable int courseId){
        try {
            courseService.deleteCourse(courseId);
            return ResponseEntity.ok().build();
        } catch (
                ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/{courseId}")
    public ResponseEntity<?> addSubcategory(@PathVariable int courseId,
                                             @Valid @RequestBody CreateSubcategoryRequest createSubcategoryRequest,
                                             UriComponentsBuilder uriComponentsBuilder
    ) {
        try {
            var subcategoryDto = courseService.addSubcategoryToCourse(courseId, createSubcategoryRequest);
            var uri = uriComponentsBuilder.path("/course/{courseId}/").buildAndExpand(subcategoryDto.getId()).toUri();
            return ResponseEntity.created(uri).body(subcategoryDto);

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //-----------Subcategory endpoints---------------//

    @GetMapping("/{courseId}/subcategories")
    public ResponseEntity<List<SubcategoryDto>> getSubcategoriesForCourse(@PathVariable int courseId){
        try {
            var subcategoryDtos = courseService.getAllSubcategoriesForCourse(courseId);
            if(subcategoryDtos.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return ResponseEntity.ok(subcategoryDtos);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{courseId}/subcategories/{subcategoryId}")
    public ResponseEntity<SubcategoryDto> getSubcategory(@PathVariable int courseId, @PathVariable int subcategoryId){
        try {
            var subcategoryDto = courseService.getSubcategory(courseId, subcategoryId);
            return ResponseEntity.ok(subcategoryDto);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{courseId}/subcategories/{subcategoryId}/title")
    public ResponseEntity<SubcategoryDto> updateSubcategoryTitle(@PathVariable int courseId, @PathVariable int subcategoryId, @RequestBody @Valid UpdateSubcategoryTitle updateSubcategoryDto ){
        try {
            var updatedSubcategoryDto = courseService.updateSubcategory(courseId, subcategoryId, updateSubcategoryDto);
            return ResponseEntity.ok(updatedSubcategoryDto);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{courseId}/subcategories/{subcategoryId}/description")
    public ResponseEntity<SubcategoryDto> updateSubcategoryDescription(@PathVariable int courseId, @PathVariable int subcategoryId, @RequestBody @Valid UpdateSubcategoryDescription updateSubcategoryDto ){
        try {
            var updatedSubcategoryDto = courseService.updateSubcategory(courseId, subcategoryId, updateSubcategoryDto);
            return ResponseEntity.ok(updatedSubcategoryDto);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{courseId}/subcategories/{subcategoryId}/course")
    public ResponseEntity<SubcategoryDto> updateSubcategoryCourse(@PathVariable int courseId, @PathVariable int subcategoryId, @RequestBody @Valid UpdateSubcategoryCourse updateSubcategoryDto ){
        try {
            var updatedSubcategoryDto = courseService.updateSubcategoryCourse(courseId, subcategoryId, updateSubcategoryDto);
            return ResponseEntity.ok(updatedSubcategoryDto);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{courseId}/subcategories/{subcategoryId}")
    public ResponseEntity<?> deleteSubcategory(@PathVariable int courseId, @PathVariable int subcategoryId){
        try {
            courseService.deleteSubcategory(courseId, subcategoryId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}