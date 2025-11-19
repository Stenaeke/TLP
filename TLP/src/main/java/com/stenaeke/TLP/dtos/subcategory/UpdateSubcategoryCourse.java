package com.stenaeke.TLP.dtos.subcategory;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.domain.Subcategory;

public class UpdateSubcategoryCourse implements UpdateSubcategoryDto {
    private Course course;


    @Override
    public void applyToSubcategory(Subcategory subcategory) {
        subcategory.getCourse().removeSubcategory(subcategory);
        course.addSubcategory(subcategory);
    }
}
