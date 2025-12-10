package com.stenaeke.TLP.services;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;
import com.stenaeke.TLP.dtos.subcategory.*;
import com.stenaeke.TLP.exceptions.ResourceNotFoundException;
import com.stenaeke.TLP.mappers.SubcategoryMapper;
import com.stenaeke.TLP.repositories.SubcategoryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;
    private final SubcategoryMapper subcategoryMapper;
    private final EntityManager entityManager;


    @Transactional
    public SubcategoryDto addSubcategoryToCourse(CreateSubcategoryRequest createSubcategoryRequest) {
        var courseReference = entityManager.getReference(Course.class, createSubcategoryRequest.getCourseId());

        Subcategory subcategory = new Subcategory();
        subcategory.setTitle(createSubcategoryRequest.getTitle());
        subcategory.setDescription(createSubcategoryRequest.getDescription());
        subcategory.setCourse(courseReference);

        subcategoryRepository.save(subcategory);
        return subcategoryMapper.subcategoryToSubcategoryDto(subcategory);
    }

    @Transactional(readOnly = true)
    public List<SubcategoryDto> getAllSubcategoriesForCourse(Long courseId) {
        return subcategoryRepository.findByCourseId(courseId).stream().map(subcategoryMapper::subcategoryToSubcategoryDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubcategoryDto getSubcategory(Long subcategoryId) {

        return subcategoryRepository.findById(subcategoryId).map(subcategoryMapper::subcategoryToSubcategoryDto)
                .orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));
    }

    @Transactional
    public SubcategoryDto updateSubcategory(Long subcategoryId, UpdateSubcategoryRequest updateSubcategoryRequest) {
        try {
            var subcategory = subcategoryRepository.findById(subcategoryId)
                    .orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));

            subcategoryMapper.UpdateSubcategoryFromRequest(updateSubcategoryRequest, subcategory);

            if (updateSubcategoryRequest.getCourseId() != null) {
                Course courseRef = entityManager.getReference(Course.class, updateSubcategoryRequest.getCourseId());
                subcategory.setCourse(courseRef);
            }

            subcategoryRepository.saveAndFlush(subcategory);
            return subcategoryMapper.subcategoryToSubcategoryDto(subcategory);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceNotFoundException("Course not found with id: " + updateSubcategoryRequest.getCourseId());
        }
    }

    @Transactional
    public void deleteSubcategory(Long subcategoryId) {
        var subcategory = subcategoryRepository.findById(subcategoryId)
                .orElseThrow(()-> new ResourceNotFoundException("subcategory not found"));

        subcategoryRepository.delete(subcategory);
    }

}
