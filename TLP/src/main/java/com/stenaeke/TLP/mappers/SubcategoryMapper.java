package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.subcategory.SubcategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {
    @Mapping(target = "courseId", source = "course.id")
    SubcategoryDto subcategoryToSubcategoryDto(Subcategory subcategory);
}
