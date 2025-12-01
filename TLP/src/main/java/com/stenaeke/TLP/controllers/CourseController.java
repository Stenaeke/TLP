package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.course.CreateCourseDto;
import com.stenaeke.TLP.dtos.course.CourseDto;
import com.stenaeke.TLP.dtos.course.UpdateCourseContentDto;
import com.stenaeke.TLP.dtos.course.UpdateCourseTitleDto;
import com.stenaeke.TLP.dtos.module.CreateModuleRequest;
import com.stenaeke.TLP.dtos.module.ModuleDto;
import com.stenaeke.TLP.dtos.module.UpdateModuleTitleDto;
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

    //---------------Course endpoints---------------//

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

        var uri = uriComponentsBuilder.path("/courses/{courseId}").buildAndExpand(createdCourseDto.getId()).toUri();
        return ResponseEntity.created(uri).body(createdCourseDto);
    }

    @PutMapping("/{courseId}/title")
    public ResponseEntity<CourseDto> updateCourseTitle(@PathVariable Long courseId,
                                                       @Valid @RequestBody UpdateCourseTitleDto updateCourseTitleDto){
        return ResponseEntity.ok(courseService.updateCourse(updateCourseTitleDto, courseId));
    }

    @PutMapping("/{courseId}/description")
    public ResponseEntity<CourseDto> updateCourseDescription(@PathVariable Long courseId,
                                                             @Valid @RequestBody UpdateCourseContentDto updateCourseDescriptionDto){
        return ResponseEntity.ok(courseService.updateCourse(updateCourseDescriptionDto, courseId));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId){
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    //---------------Subcategory endpoints---------------//

    @PostMapping("/{courseId}/subcategories")
    public ResponseEntity<SubcategoryDto> addSubcategory(@PathVariable Long courseId,
                                                         @Valid @RequestBody CreateSubcategoryRequest createSubcategoryRequest,
                                                         UriComponentsBuilder uriComponentsBuilder
    ) {
        var subcategoryDto = courseService.addSubcategoryToCourse(courseId, createSubcategoryRequest);
        var uri = uriComponentsBuilder.path("/courses/{courseId}/subcategories/{subcategoryId}").buildAndExpand(courseId, subcategoryDto.getId()).toUri();
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

    //---------------Module endpoints---------------//

    @PostMapping("/{courseId}/subcategories/{subcategoryId}/modules")
    public ResponseEntity<ModuleDto> addModule(@PathVariable Long courseId, @PathVariable Long subcategoryId,
                                               @Valid @RequestBody CreateModuleRequest createModuleRequest,
                                               UriComponentsBuilder uriComponentsBuilder) {
        var moduleDto = courseService.addModuleToSubcategory(courseId, subcategoryId, createModuleRequest);
        var uri = uriComponentsBuilder.path("/courses/{courseId}/subcategories/{subcategoryId}/modules/{moduleId}").buildAndExpand(courseId, subcategoryId, moduleDto.getId()).toUri();
        return ResponseEntity.created(uri).body(moduleDto);
    }

    @GetMapping("/{courseId}/subcategories/{subcategoryId}/modules/{moduleId}")
    public ResponseEntity<ModuleDto> getModule(@PathVariable Long courseId, @PathVariable Long subcategoryId, @PathVariable Long moduleId) {
        var moduleDto = courseService.getModule(courseId, subcategoryId, moduleId);
        return ResponseEntity.ok(moduleDto);
    }

    @GetMapping("/{courseId}/subcategories/{subcategoryId}/modules/")
    public ResponseEntity<List<ModuleDto>> getModulesInSubcategory(@PathVariable Long courseId, @PathVariable Long subcategoryId) {
        var moduleDtos = courseService.getAllModulesForSubcategory(courseId, subcategoryId);
        return ResponseEntity.ok(moduleDtos);
    }

    @PutMapping("/{courseId}/subcategories/{subcategoryId}/modules/{moduleId}/title")
    public ResponseEntity<ModuleDto> updateModuleTitle(@PathVariable Long courseId,
                                                       @PathVariable Long subcategoryId,
                                                       @PathVariable Long moduleId,
                                                       @Valid @RequestBody UpdateModuleTitleDto updateModuleDto){
        var moduleDto = courseService.updateModule(courseId, subcategoryId, moduleId, updateModuleDto);
        return ResponseEntity.ok(moduleDto);
    }

    @PutMapping("/{courseId}/subcategories/{subcategoryId}/modules/{moduleId}/description")
    public ResponseEntity<ModuleDto> updateModuleDescription(@PathVariable Long courseId,
                                                       @PathVariable Long subcategoryId,
                                                       @PathVariable Long moduleId,
                                                       @Valid @RequestBody UpdateModuleTitleDto updateModuleDto){
        var moduleDto = courseService.updateModule(courseId, subcategoryId, moduleId, updateModuleDto);
        return ResponseEntity.ok(moduleDto);
    }

    @PutMapping("/{courseId}/subcategories/{subcategoryId}/modules/{moduleId}/content")
    public ResponseEntity<ModuleDto> updateModuleContent(@PathVariable Long courseId,
                                                       @PathVariable Long subcategoryId,
                                                       @PathVariable Long moduleId,
                                                       @Valid @RequestBody UpdateModuleTitleDto updateModuleDto){
        var moduleDto = courseService.updateModule(courseId, subcategoryId, moduleId, updateModuleDto);
        return ResponseEntity.ok(moduleDto);
    }





































}