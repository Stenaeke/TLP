package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.dtos.course.CourseDto;
import com.stenaeke.TLP.dtos.course.UpdateCourseDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDto courseToCourseDto(Course course);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCourseFromRequestDto(UpdateCourseDto updateCourseDto, @MappingTarget Course course);
}
