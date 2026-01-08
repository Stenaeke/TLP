package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.subcategory.*;
import com.stenaeke.TLP.services.SubcategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/subcategories")
@RequiredArgsConstructor
public class SubcategoryController {

    private final SubcategoryService subcategoryService;

    @PostMapping
    public ResponseEntity<SubcategoryDto> addSubcategory(@Valid @RequestBody CreateSubcategoryDto createSubcategoryDto,
                                                         UriComponentsBuilder uriComponentsBuilder
    ) {
        var subcategoryDto = subcategoryService.addSubcategoryToCourse(createSubcategoryDto);
        var uri = uriComponentsBuilder.path("/subcategories/{subcategoryId}").buildAndExpand(subcategoryDto.getId()).toUri(); //TODO:Double check
        return ResponseEntity.created(uri).body(subcategoryDto);
    }

    @GetMapping("/{subcategoryId}")
    public ResponseEntity<SubcategoryDto> getSubcategory(@PathVariable Long subcategoryId){
        var subcategoryDto = subcategoryService.getSubcategory(subcategoryId);
        return ResponseEntity.ok(subcategoryDto);
    }

    @GetMapping
    public ResponseEntity<List<SubcategoryDto>> getSubcategoriesByCourseId(@RequestParam Long courseId){
        var subcategoryDtos = subcategoryService.getAllSubcategoriesForCourse(courseId);
        return ResponseEntity.ok(subcategoryDtos);
    }

    @PatchMapping("/{subcategoryId}")
    public ResponseEntity<SubcategoryDto> updateSubcategory(@PathVariable long subcategoryId,
                                                            @Valid @RequestBody UpdateSubcategoryDto updateSubcategoryDto){
        var updatedSubcategoryDto = subcategoryService.updateSubcategory(subcategoryId, updateSubcategoryDto);
        return ResponseEntity.ok(updatedSubcategoryDto);
    }

    @DeleteMapping("/{subcategoryId}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable Long subcategoryId){
        subcategoryService.deleteSubcategory(subcategoryId);
        return ResponseEntity.noContent().build();
    }

}
