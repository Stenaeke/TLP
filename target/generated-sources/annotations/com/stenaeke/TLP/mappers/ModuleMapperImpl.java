package com.stenaeke.TLP.mappers;

import com.stenaeke.TLP.domain.Module;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.module.ModuleDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-28T11:06:53+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class ModuleMapperImpl implements ModuleMapper {

    @Override
    public ModuleDto moduleToModuleDto(Module module) {
        if ( module == null ) {
            return null;
        }

        ModuleDto moduleDto = new ModuleDto();

        moduleDto.setSubcategoryId( moduleSubcategoryId( module ) );
        moduleDto.setId( module.getId() );
        moduleDto.setTitle( module.getTitle() );
        moduleDto.setContent( module.getContent() );
        moduleDto.setCreatedAt( module.getCreatedAt() );
        moduleDto.setUpdatedAt( module.getUpdatedAt() );

        return moduleDto;
    }

    private Long moduleSubcategoryId(Module module) {
        Subcategory subcategory = module.getSubcategory();
        if ( subcategory == null ) {
            return null;
        }
        return subcategory.getId();
    }
}
