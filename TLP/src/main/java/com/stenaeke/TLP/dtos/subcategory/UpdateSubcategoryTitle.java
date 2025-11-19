package com.stenaeke.TLP.dtos.subcategory;

import com.stenaeke.TLP.domain.Subcategory;

public class UpdateSubcategoryTitle implements UpdateSubcategoryDto {
    private String title;

    @Override
    public void applyToSubcategory(Subcategory subcategory) {
        subcategory.setTitle(title);
    }
}
