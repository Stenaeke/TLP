package com.stenaeke.TLP.services;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.course.*;
import com.stenaeke.TLP.dtos.subcategory.CreateSubcategoryRequest;
import com.stenaeke.TLP.exceptions.ResourceNotFoundException;
import com.stenaeke.TLP.mappers.CourseMapper;
import com.stenaeke.TLP.repositories.CourseRepository;
import com.stenaeke.TLP.repositories.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final CourseMapper courseMapper;

    public List<Course> getAllCourses(){
        return courseRepository.findAll();
    }

    public CourseDto createCourse(CreateCourseDto createCourseDto) {
        Course course = new Course();
        course.setTitle(createCourseDto.getTitle());
        course.setDescription(createCourseDto.getDescription());
        courseRepository.save(course);

        return (courseMapper.courseToCourseDto(course));
    }

    public CourseDto updateCourse(UpdateDto updateCourseDto, @PathVariable int id) {
        var course = courseRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("course not found"));

        updateCourseDto.applyToCourse(course);
        courseRepository.save(course);

        return (courseMapper.courseToCourseDto(course));
    }

    public void deleteCourse(int id) {
        var course =  courseRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));
        courseRepository.delete(course);

    }

    @Transactional
    public Subcategory addSubcategoryToCourse(int courseId, CreateSubcategoryRequest createSubcategoryRequest) {
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("course not found"));

        Subcategory subcategory = new Subcategory();
        subcategory.setTitle(createSubcategoryRequest.getTitle());
        subcategory.setDescription(createSubcategoryRequest.getDescription());
        course.addSubcategory(subcategory);

        courseRepository.save(course);
        return subcategory;
    }
}
