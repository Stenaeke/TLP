package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.module.CreateModuleDto;
import com.stenaeke.TLP.dtos.module.ModuleDto;
import com.stenaeke.TLP.dtos.module.UpdateModuleDto;
import com.stenaeke.TLP.services.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleDto> addModule(@Valid @RequestBody CreateModuleDto createModuleDto,
                                               UriComponentsBuilder uriComponentsBuilder) {
        var moduleDto = moduleService.addModuleToSubcategory(createModuleDto);
        var uri = uriComponentsBuilder.path("/modules/{moduleId}").buildAndExpand(moduleDto.getId()).toUri();//TODO:Double check
        return ResponseEntity.created(uri).body(moduleDto);
    }

    @GetMapping("/{moduleId}")
    public ResponseEntity<ModuleDto> getModule(@PathVariable long moduleId) {
        var moduleDto = moduleService.getModule(moduleId);
        return ResponseEntity.ok(moduleDto);
    }

    @GetMapping
    public ResponseEntity<List<ModuleDto>> getModulesBySubcategoryId(@RequestParam long subcategoryId) {
        var moduleDtos = moduleService.getAllModulesForSubcategory(subcategoryId);
        return ResponseEntity.ok(moduleDtos);
    }

    @PatchMapping("/{moduleId}")
    public ResponseEntity<ModuleDto> updateModule(@PathVariable long moduleId,
                                                  @Valid @RequestBody UpdateModuleDto updateModuleDto) {

        var updatedModuleDto = moduleService.updateModule(moduleId, updateModuleDto);
        return ResponseEntity.ok(updatedModuleDto);
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long moduleId) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }

}
