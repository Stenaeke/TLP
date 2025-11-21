package com.stenaeke.TLP.dtos.subcategory;

import com.stenaeke.TLP.domain.Subcategory;
import lombok.Data;

@Data
public class UpdateSubcategoryDescription implements UpdateSubcategoryDto {
    private String description;

    @Override
    public void applyToSubcategory(Subcategory subcategory) {
        subcategory.setDescription(description);
    }
}
