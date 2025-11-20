package com.stenaeke.TLP.dtos.subcategory;

import lombok.Data;

@Data
public class SubcategoryDto {
    private int id;
    private String title;
    private String description;
    private int courseId;
}
