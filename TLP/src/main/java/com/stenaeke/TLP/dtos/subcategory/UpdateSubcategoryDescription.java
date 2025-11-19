package com.stenaeke.TLP.dtos.subcategory;

import com.stenaeke.TLP.domain.Subcategory;

public class UpdateSubcategoryDescription implements UpdateSubcategoryDto {
    private String Description;

    @Override
    public void applyToSubcategory(Subcategory subcategory) {
        subcategory.setDescription(Description);
    }
}
