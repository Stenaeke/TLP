package com.stenaeke.TLP.services;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.course.*;
import com.stenaeke.TLP.dtos.subcategory.CreateSubcategoryRequest;
import com.stenaeke.TLP.dtos.subcategory.SubcategoryDto;
import com.stenaeke.TLP.dtos.subcategory.UpdateSubcategoryDto;
import com.stenaeke.TLP.exceptions.ResourceNotFoundException;
import com.stenaeke.TLP.mappers.CourseMapper;
import com.stenaeke.TLP.mappers.SubcategoryMapper;
import com.stenaeke.TLP.repositories.CourseRepository;
import com.stenaeke.TLP.repositories.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final CourseMapper courseMapper;
    private final SubcategoryMapper subcategoryMapper;

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

        return (courseMapper.courseToCourseDto(course));
    }

    @Transactional(readOnly = true)
    public CourseDto getCourse(int courseId) {
        var course = courseRepository.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("course not found"));
        return courseMapper.courseToCourseDto(course);
    }

    @Transactional
    public CourseDto updateCourse(UpdateCourseDto updateCourseDto, int id) {
        var course = courseRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("course not found"));

        updateCourseDto.applyToCourse(course);
        courseRepository.save(course);

        return (courseMapper.courseToCourseDto(course));
    }

    @Transactional
    public void deleteCourse(int id) {
        var course =  courseRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));
        courseRepository.delete(course);

    }

    @Transactional
    public SubcategoryDto addSubcategoryToCourse(int courseId, CreateSubcategoryRequest createSubcategoryRequest) {
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("course not found"));

        Subcategory subcategory = new Subcategory();
        subcategory.setTitle(createSubcategoryRequest.getTitle());
        subcategory.setDescription(createSubcategoryRequest.getDescription());
        course.addSubcategory(subcategory);

        courseRepository.save(course);
        return subcategoryMapper.subcategoryToSubcategoryDto(subcategory);
    }

    @Transactional(readOnly = true)
    public List<SubcategoryDto> getAllSubcategoriesForCourse(int courseId) {
        var course =  courseRepository.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("course not found"));

        return course.getSubcategories().stream().map(subcategoryMapper::subcategoryToSubcategoryDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubcategoryDto getSubcategory(int courseId, int subcategoryId) {
        var course = courseRepository.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("course not found"));

        return course.getSubcategories().stream().filter(subcategory ->
                subcategory.getId() == subcategoryId).findFirst().
                map(subcategoryMapper::subcategoryToSubcategoryDto).orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));
    }

    @Transactional
    public SubcategoryDto updateSubcategory(int courseId, int subcategoryId, UpdateSubcategoryDto updateSubcategoryDto) {
        var course =  courseRepository.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("course not found"));
        var subcategory = subcategoryRepository.findById(subcategoryId).orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));

        updateSubcategoryDto.applyToSubcategory(subcategory);
        courseRepository.save(course);
        return subcategoryMapper.subcategoryToSubcategoryDto(subcategory);
    }

    @Transactional
    public void deleteSubcategory(int courseId, int subcategoryId) {
        var course = courseRepository.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("course not found"));
        var subcategory = subcategoryRepository.findById(subcategoryId).orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));

        course.removeSubcategory(subcategory);
        subcategoryRepository.delete(subcategory);
        courseRepository.save(course);
    }
}
