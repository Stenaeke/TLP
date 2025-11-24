package com.stenaeke.TLP.dtos.subcategory;

import lombok.Data;

@Data
public class SubcategoryDto {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
}
