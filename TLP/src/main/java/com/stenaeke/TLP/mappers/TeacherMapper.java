package com.stenaeke.TLP.mappers;


import com.stenaeke.TLP.domain.Teacher;
import com.stenaeke.TLP.dtos.teacher.TeacherDto;
import com.stenaeke.TLP.dtos.teacher.UpdateTeacherRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    TeacherDto mapToDTO(Teacher teacher);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTeacherFromRequest(UpdateTeacherRequest request, @MappingTarget Teacher teacher);
}
