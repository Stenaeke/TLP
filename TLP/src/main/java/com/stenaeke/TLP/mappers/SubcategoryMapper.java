package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.subcategory.SubcategoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {
    SubcategoryDto subcategoryToSubcategoryDto(Subcategory subcategory);
}
