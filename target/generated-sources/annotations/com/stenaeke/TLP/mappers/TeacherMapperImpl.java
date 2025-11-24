package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Teacher;
import com.stenaeke.TLP.dtos.teacher.TeacherDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-24T14:38:18+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class TeacherMapperImpl implements TeacherMapper {

    @Override
    public TeacherDto mapToDTO(Teacher teacher) {
        if ( teacher == null ) {
            return null;
        }

        TeacherDto teacherDto = new TeacherDto();

        teacherDto.setId( teacher.getId() );
        teacherDto.setFirstName( teacher.getFirstName() );
        teacherDto.setLastName( teacher.getLastName() );
        teacherDto.setEmail( teacher.getEmail() );

        return teacherDto;
    }
}
