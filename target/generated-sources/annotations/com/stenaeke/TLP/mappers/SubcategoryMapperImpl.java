package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.subcategory.SubcategoryDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-28T11:06:53+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class SubcategoryMapperImpl implements SubcategoryMapper {

    @Override
    public SubcategoryDto subcategoryToSubcategoryDto(Subcategory subcategory) {
        if ( subcategory == null ) {
            return null;
        }

        SubcategoryDto subcategoryDto = new SubcategoryDto();

        subcategoryDto.setCourseId( subcategoryCourseId( subcategory ) );
        subcategoryDto.setId( subcategory.getId() );
        subcategoryDto.setTitle( subcategory.getTitle() );
        subcategoryDto.setDescription( subcategory.getDescription() );

        return subcategoryDto;
    }

    private Long subcategoryCourseId(Subcategory subcategory) {
        Course course = subcategory.getCourse();
        if ( course == null ) {
            return null;
        }
        return course.getId();
    }
}
