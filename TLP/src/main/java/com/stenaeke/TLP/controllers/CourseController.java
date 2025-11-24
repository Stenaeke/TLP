package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.course.CreateCourseDto;
import com.stenaeke.TLP.dtos.course.CourseDto;
import com.stenaeke.TLP.dtos.course.UpdateCourseDescriptionDto;
import com.stenaeke.TLP.dtos.course.UpdateCourseTitleDto;
import com.stenaeke.TLP.dtos.subcategory.*;
import com.stenaeke.TLP.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

        var uri = uriComponentsBuilder.path("/course/{courseId}").buildAndExpand(createdCourseDto.getId()).toUri();
        return ResponseEntity.created(uri).body(createdCourseDto);
    }

    @PutMapping("/{courseId}/title")
    public ResponseEntity<CourseDto> updateCourseTitle(@PathVariable Long courseId,
                                                       @Valid @RequestBody UpdateCourseTitleDto updateCourseTitleDto){
        return ResponseEntity.ok(courseService.updateCourse(updateCourseTitleDto, courseId));
    }

    @PutMapping("/{courseId}/description")
    public ResponseEntity<CourseDto> updateCourseDescription(@PathVariable Long courseId,
                                                             @Valid @RequestBody UpdateCourseDescriptionDto updateCourseDescriptionDto){
        return ResponseEntity.ok(courseService.updateCourse(updateCourseDescriptionDto, courseId));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId){
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    //-----------Subcategory endpoints---------------//

    @PostMapping("/{courseId}/subcategories")
    public ResponseEntity<SubcategoryDto> addSubcategory(@PathVariable Long courseId,
                                                         @Valid @RequestBody CreateSubcategoryRequest createSubcategoryRequest,
                                                         UriComponentsBuilder uriComponentsBuilder
    ) {
        var subcategoryDto = courseService.addSubcategoryToCourse(courseId, createSubcategoryRequest);
        var uri = uriComponentsBuilder.path("/course/{courseId}/subcategories/{subcategoryId}").buildAndExpand(courseId, subcategoryDto.getId()).toUri();
        return ResponseEntity.created(uri).body(subcategoryDto);
    }

    @GetMapping("/{courseId}/subcategories")
    public ResponseEntity<List<SubcategoryDto>> getSubcategoriesForCourse(@PathVariable Long courseId){
        var subcategoryDtos = courseService.getAllSubcategoriesForCourse(courseId);
        return ResponseEntity.ok(subcategoryDtos);
    }

    @GetMapping("/{courseId}/subcategories/{subcategoryId}")
    public ResponseEntity<SubcategoryDto> getSubcategory(@PathVariable Long courseId, @PathVariable Long subcategoryId){
        var subcategoryDto = courseService.getSubcategory(courseId, subcategoryId);
        return ResponseEntity.ok(subcategoryDto);
    }

    @PutMapping("/{courseId}/subcategories/{subcategoryId}/title")
    public ResponseEntity<SubcategoryDto> updateSubcategoryTitle(@PathVariable Long courseId, @PathVariable Long subcategoryId, @Valid @RequestBody UpdateSubcategoryTitle updateSubcategoryDto){
        var updatedSubcategoryDto = courseService.updateSubcategory(courseId, subcategoryId, updateSubcategoryDto);
        return ResponseEntity.ok(updatedSubcategoryDto);
    }

    @PutMapping("/{courseId}/subcategories/{subcategoryId}/description")
    public ResponseEntity<SubcategoryDto> updateSubcategoryDescription(@PathVariable Long courseId, @PathVariable Long subcategoryId, @Valid @RequestBody UpdateSubcategoryDescription updateSubcategoryDto){
        var updatedSubcategoryDto = courseService.updateSubcategory(courseId, subcategoryId, updateSubcategoryDto);
        return ResponseEntity.ok(updatedSubcategoryDto);
    }

    @PutMapping("/{courseId}/subcategories/{subcategoryId}/course")
    public ResponseEntity<SubcategoryDto> updateSubcategoryCourse(@PathVariable Long courseId, @PathVariable Long subcategoryId, @Valid @RequestBody UpdateSubcategoryCourse updateSubcategoryDto){
        var updatedSubcategoryDto = courseService.updateSubcategoryCourse(courseId, subcategoryId, updateSubcategoryDto);
        return ResponseEntity.ok(updatedSubcategoryDto);
    }

    @DeleteMapping("/{courseId}/subcategories/{subcategoryId}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable Long courseId, @PathVariable Long subcategoryId){
        courseService.deleteSubcategory(courseId, subcategoryId);
        return ResponseEntity.noContent().build();
    }

}