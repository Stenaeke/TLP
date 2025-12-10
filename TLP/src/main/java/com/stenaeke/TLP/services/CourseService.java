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
    private final CourseMapper courseMapper;


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
    public CourseDto updateCourse(Long courseId, UpdateCourseRequest updateCourseRequest) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));

        courseMapper.updateCourseFromRequestDto(updateCourseRequest, course);

        courseRepository.save(course);
        return courseMapper.courseToCourseDto(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        var course =  courseRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("course not found"));
        courseRepository.delete(course);

    }

}
