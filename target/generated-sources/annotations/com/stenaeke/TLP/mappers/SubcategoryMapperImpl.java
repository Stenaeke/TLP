package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.subcategory.SubcategoryDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T16:25:08+0100",
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

        subcategoryDto.setId( subcategory.getId() );
        subcategoryDto.setTitle( subcategory.getTitle() );
        subcategoryDto.setDescription( subcategory.getDescription() );
        subcategoryDto.setCourse( subcategory.getCourse() );

        return subcategoryDto;
    }
}
