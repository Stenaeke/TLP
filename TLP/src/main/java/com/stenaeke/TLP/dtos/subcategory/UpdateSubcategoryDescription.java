package com.stenaeke.TLP.dtos.subcategory;

import lombok.Data;

@Data
public final class UpdateSubcategoryDescription implements UpdateSubcategoryDto {
    private String description;
}
