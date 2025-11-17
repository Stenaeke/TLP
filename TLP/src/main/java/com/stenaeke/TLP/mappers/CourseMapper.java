package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.dtos.course.CourseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDto courseToCourseDto(Course course);
}
