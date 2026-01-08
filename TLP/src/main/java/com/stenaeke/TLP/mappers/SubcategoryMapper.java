package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.subcategory.SubcategoryDto;
import com.stenaeke.TLP.dtos.subcategory.UpdateSubcategoryDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {
    @Mapping(target = "courseId", source = "course.id")
    SubcategoryDto subcategoryToSubcategoryDto(Subcategory subcategory);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void UpdateSubcategoryFromRequest(UpdateSubcategoryDto request, @MappingTarget Subcategory subcategory);
}
