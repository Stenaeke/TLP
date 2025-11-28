package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Module;
import com.stenaeke.TLP.dtos.module.ModuleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    @Mapping(target = "subcategoryId", source = "subcategory.id")
    ModuleDto moduleToModuleDto(Module module);
}
