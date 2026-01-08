package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Module;
import com.stenaeke.TLP.dtos.module.ModuleDto;
import com.stenaeke.TLP.dtos.module.UpdateModuleDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    @Mapping(target = "subcategoryId", source = "subcategory.id")
    ModuleDto moduleToModuleDto(Module module);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModuleFromRequest(UpdateModuleDto updateModuleDto, @MappingTarget Module module);
}
