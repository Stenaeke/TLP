package com.stenaeke.TLP.services;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.domain.Module;
import com.stenaeke.TLP.dtos.course.*;
import com.stenaeke.TLP.dtos.module.*;
import com.stenaeke.TLP.dtos.subcategory.*;
import com.stenaeke.TLP.exceptions.ResourceMismatchException;
import com.stenaeke.TLP.exceptions.ResourceNotFoundException;
import com.stenaeke.TLP.mappers.CourseMapper;
import com.stenaeke.TLP.mappers.ModuleMapper;
import com.stenaeke.TLP.mappers.SubcategoryMapper;
import com.stenaeke.TLP.repositories.CourseRepository;
import com.stenaeke.TLP.repositories.ModuleRepository;
import com.stenaeke.TLP.repositories.SubcategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final CourseMapper courseMapper;
    private final SubcategoryMapper subcategoryMapper;
    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;

    //-------Course methods-------//

    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses(){
        return courseRepository.findAll().stream().map(courseMapper::courseToCourseDto).collect(Collectors.toList());
    }

    @Transactional
    public CourseDto createCourse(CreateCourseDto createCourseDto) {
        Course course = new Course();
        course.setTitle(createCourseDto.getTitle());
        course.setDescription(createCourseDto.getDescription());
        courseRepository.save(course);

        return courseMapper.courseToCourseDto(course);
    }

    @Transactional(readOnly = true)
    public CourseDto getCourse(Long courseId) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));
        return courseMapper.courseToCourseDto(course);
    }

    @Transactional
    public CourseDto updateCourse(UpdateCourseDto updateCourseDto, Long id) {
        var course = courseRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        switch (updateCourseDto){
            case UpdateCourseTitleDto title -> course.setTitle(title.getTitle());
            case UpdateCourseDescriptionDto description -> course.setDescription(description.getDescription());
        }
        courseRepository.save(course);

        return courseMapper.courseToCourseDto(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        var course =  courseRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));
        courseRepository.delete(course);

    }

    //---------------Subcategory methods---------------//

    @Transactional
    public SubcategoryDto addSubcategoryToCourse(Long courseId, CreateSubcategoryRequest createSubcategoryRequest) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        Subcategory subcategory = new Subcategory();
        subcategory.setTitle(createSubcategoryRequest.getTitle());
        subcategory.setDescription(createSubcategoryRequest.getDescription());
        course.addSubcategory(subcategory);

        subcategoryRepository.save(subcategory);
        courseRepository.save(course);
        return subcategoryMapper.subcategoryToSubcategoryDto(subcategory);
    }

    @Transactional(readOnly = true)
    public List<SubcategoryDto> getAllSubcategoriesForCourse(Long courseId) {
        var course =  courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        return course.getSubcategories().stream().map(subcategoryMapper::subcategoryToSubcategoryDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubcategoryDto getSubcategory(Long courseId, Long subcategoryId) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        validateSubcategoryBelongsToCourse(course, subcategoryId);

        return subcategoryRepository.findById(subcategoryId).map(subcategoryMapper::subcategoryToSubcategoryDto)
                .orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));
    }

    @Transactional
    public SubcategoryDto updateSubcategory(Long courseId, Long subcategoryId, UpdateSubcategoryDto updateSubcategoryDto) {
        var course =  courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        validateSubcategoryBelongsToCourse(course, subcategoryId);

        var subcategory = subcategoryRepository.findById(subcategoryId)
                .orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));

        switch (updateSubcategoryDto) {
            case UpdateSubcategoryTitle title -> subcategory.setTitle(title.getTitle());
            case UpdateSubcategoryDescription description-> subcategory.setDescription(description.getDescription());
        }

        subcategoryRepository.save(subcategory);
        return subcategoryMapper.subcategoryToSubcategoryDto(subcategory);
    }

    @Transactional
    public SubcategoryDto updateSubcategoryCourse(Long courseId, Long subcategoryId, UpdateSubcategoryCourse updateSubcategoryDto) {
        var currentCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("current course not found"));

        validateSubcategoryBelongsToCourse(currentCourse, subcategoryId);

        var newCourse = courseRepository.findById(updateSubcategoryDto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("new course not found"));

        var subcategory = subcategoryRepository.findById(subcategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("subcategory not found"));

        if (!currentCourse.getId().equals(newCourse.getId()) && !newCourse.getSubcategories().contains(subcategory)) {
            currentCourse.removeSubcategory(subcategory);
            newCourse.addSubcategory(subcategory);

            courseRepository.save(currentCourse);
            courseRepository.save(newCourse);

        }
        return subcategoryMapper.subcategoryToSubcategoryDto(subcategory);
    }

    @Transactional
    public void deleteSubcategory(Long courseId, Long subcategoryId) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        validateSubcategoryBelongsToCourse(course, subcategoryId);

        var subcategory = subcategoryRepository.findById(subcategoryId)
                .orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));

        course.removeSubcategory(subcategory);
        subcategoryRepository.delete(subcategory);
        courseRepository.save(course);
    }

    //---------------Module methods---------------//

    @Transactional
    public ModuleDto addModuleToSubcategory(Long courseId, Long subcategoryId, @Valid CreateModuleRequest createModuleRequest) {
        var course =  courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        validateSubcategoryBelongsToCourse(course, subcategoryId);

        var subcategory = subcategoryRepository.findById(subcategoryId)
                .orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));

        Module module = new Module();
        module.setTitle(createModuleRequest.getTitle());
        module.setContent(createModuleRequest.getContent());
        module.setPublished(createModuleRequest.getPublished());
        module.setCreatedAt(OffsetDateTime.now());
        module.setUpdatedAt(OffsetDateTime.now());

        subcategory.addModule(module);

        moduleRepository.save(module);
        subcategoryRepository.save(subcategory);

        return moduleMapper.moduleToModuleDto(module);
    }

    @Transactional(readOnly = true)
    public ModuleDto getModule(Long courseId, Long subcategoryId, Long moduleId) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));
        validateSubcategoryBelongsToCourse(course, subcategoryId);
        var subcategory = subcategoryRepository.findById(subcategoryId)
                .orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));
        validateModuleBelongsToSubcategory(subcategory, moduleId);
        var module = moduleRepository.findById(moduleId)
                .orElseThrow(()-> new ResourceNotFoundException("module not found"));

        return moduleMapper.moduleToModuleDto(module);
    }

    @Transactional(readOnly = true)
    public List<ModuleDto> getAllModulesForSubcategory(Long courseId, Long subcategoryId) {
        var course =  courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        validateSubcategoryBelongsToCourse(course, subcategoryId);

        var subcategory = subcategoryRepository.findById(subcategoryId);

        return subcategory.get().getModules().stream().map(moduleMapper::moduleToModuleDto).collect(Collectors.toList());
    }

    @Transactional
    public ModuleDto updateModule(Long courseId, Long subcategoryId, Long moduleId, @Valid UpdateModuleDto updateModuleDto) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));
        validateSubcategoryBelongsToCourse(course, subcategoryId);
        var subcategory = subcategoryRepository.findById(subcategoryId)
                .orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));
        validateModuleBelongsToSubcategory(subcategory, moduleId);
        var module = moduleRepository.findById(moduleId)
                .orElseThrow(()-> new ResourceNotFoundException("module not found"));

        switch (updateModuleDto) {
            case UpdateModuleTitleDto title -> subcategory.setTitle(title.getTitle());
            case UpdateModuleDescriptionDto description-> subcategory.setDescription(description.getDescription());
        }

        return moduleMapper.moduleToModuleDto(module);

    }

    //---------------Helper methods---------------//
    private void validateSubcategoryBelongsToCourse(Course course, Long subcategoryId) {
        if (!course.getSubcategories().stream().anyMatch(s -> s.getId().equals(subcategoryId))) {
            throw new ResourceMismatchException("subcategory not found under course");
        }
    }

    private void validateModuleBelongsToSubcategory(Subcategory subcategory, Long moduleId) {
        if (!subcategory.getModules().stream().anyMatch(s -> s.getId().equals(moduleId))) {
            throw new ResourceMismatchException("module not found under subcategory");
        }
    }
}
