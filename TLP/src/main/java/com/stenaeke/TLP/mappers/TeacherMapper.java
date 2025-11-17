package com.stenaeke.TLP.mappers;


import com.stenaeke.TLP.domain.Teacher;
import com.stenaeke.TLP.dtos.teacher.TeacherDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    TeacherDto mapToDTO(Teacher teacher);
}
