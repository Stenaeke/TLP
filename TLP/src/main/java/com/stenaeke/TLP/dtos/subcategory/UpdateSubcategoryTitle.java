package com.stenaeke.TLP.dtos.subcategory;

import com.stenaeke.TLP.domain.Subcategory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateSubcategoryTitle implements UpdateSubcategoryDto {
    @NotEmpty
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Override
    public void applyToSubcategory(Subcategory subcategory) {
        subcategory.setTitle(title);
    }
}
