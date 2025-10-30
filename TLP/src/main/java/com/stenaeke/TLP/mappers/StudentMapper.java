package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Student;
import com.stenaeke.TLP.dtos.StudentDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    StudentDTO mapToDTO(Student student);
}
