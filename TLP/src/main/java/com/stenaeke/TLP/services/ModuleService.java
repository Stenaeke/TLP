package com.stenaeke.TLP.services;

import com.stenaeke.TLP.domain.Module;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.module.*;
import com.stenaeke.TLP.exceptions.ResourceNotFoundException;
import com.stenaeke.TLP.mappers.ModuleMapper;
import com.stenaeke.TLP.repositories.ModuleRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;
    private final EntityManager entityManager;


    @Transactional
    public ModuleDto addModuleToSubcategory(CreateModuleRequest createModuleRequest) {
        var subcategoryReference = entityManager.getReference(Subcategory.class, createModuleRequest.getSubcategoryId());

        Module module = new Module();
        module.setTitle(createModuleRequest.getTitle());
        module.setContent(createModuleRequest.getContent());
        module.setPublished(createModuleRequest.getPublished());
        module.setSubcategory(subcategoryReference);
        module.setCreatedAt(OffsetDateTime.now());
        module.setUpdatedAt(OffsetDateTime.now());

        moduleRepository.save(module);
        return moduleMapper.moduleToModuleDto(module);
    }

    @Transactional(readOnly = true)
    public ModuleDto getModule(Long moduleId) {
        var module = moduleRepository.findById(moduleId)
                .orElseThrow(()-> new ResourceNotFoundException("module not found"));

        return moduleMapper.moduleToModuleDto(module);
    }

    @Transactional(readOnly = true)
    public List<ModuleDto> getAllModulesForSubcategory(Long subcategoryId) {
        return moduleRepository.findBySubcategoryId(subcategoryId).stream().map(moduleMapper::moduleToModuleDto).collect(Collectors.toList());
    }

    @Transactional
    public ModuleDto updateModule(Long moduleId, @Valid UpdateModuleRequest updateModuleRequest) {
        try {
            var module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new ResourceNotFoundException("module not found"));

            moduleMapper.updateModuleFromRequest(updateModuleRequest, module);

            module.setUpdatedAt(OffsetDateTime.now());
            moduleRepository.save(module);
            return moduleMapper.moduleToModuleDto(module);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceNotFoundException("Subcategory not found with id: " + updateModuleRequest.getSubcategoryId());
        }
    }

    @Transactional
    public void deleteModule(Long moduleId) {
        var module = moduleRepository.findById(moduleId)
                .orElseThrow(()-> new ResourceNotFoundException("module not found"));

        moduleRepository.delete(module);
    }
}
