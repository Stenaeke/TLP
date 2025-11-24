package com.stenaeke.TLP.services;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.course.*;
import com.stenaeke.TLP.dtos.subcategory.CreateSubcategoryRequest;
import com.stenaeke.TLP.dtos.subcategory.SubcategoryDto;
import com.stenaeke.TLP.dtos.subcategory.UpdateSubcategoryCourse;
import com.stenaeke.TLP.dtos.subcategory.UpdateSubcategoryDto;
import com.stenaeke.TLP.exceptions.ResourceMismatchException;
import com.stenaeke.TLP.exceptions.ResourceNotFoundException;
import com.stenaeke.TLP.mappers.CourseMapper;
import com.stenaeke.TLP.mappers.SubcategoryMapper;
import com.stenaeke.TLP.repositories.CourseRepository;
import com.stenaeke.TLP.repositories.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final CourseMapper courseMapper;
    private final SubcategoryMapper subcategoryMapper;

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

        updateCourseDto.applyToCourse(course);
        courseRepository.save(course);

        return courseMapper.courseToCourseDto(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        var course =  courseRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));
        courseRepository.delete(course);

    }

    //-------Subcategory methods-------//

    @Transactional
    public SubcategoryDto addSubcategoryToCourse(Long courseId, CreateSubcategoryRequest createSubcategoryRequest) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        Subcategory subcategory = new Subcategory();
        subcategory.setTitle(createSubcategoryRequest.getTitle());
        subcategory.setDescription(createSubcategoryRequest.getDescription());
        course.addSubcategory(subcategory);

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

        updateSubcategoryDto.applyToSubcategory(subcategory);
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

    private void validateSubcategoryBelongsToCourse(Course course, Long subcategoryId) {
        if (!course.getSubcategories().stream().anyMatch(s -> s.getId().equals(subcategoryId))) {
            throw new ResourceMismatchException("subcategory not found under course");
        }
    }

    //-------Module methods-------//

    @Transactional(readOnly = true)
    public List<SubcategoryDto> getAllModulesForSubcategory(Long courseId, Long subcategoryId) {
        var course =  courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        validateSubcategoryBelongsToCourse(course, subcategoryId);

        var subcategory = subcategoryRepository.findById(subcategoryId);

        return course.getSubcategories().stream().map(subcategoryMapper::subcategoryToSubcategoryDto).collect(Collectors.toList());
    }

}
