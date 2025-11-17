package com.stenaeke.TLP.dtos.subcategory;

import com.stenaeke.TLP.domain.Course;
import lombok.Data;

@Data
public class SubcategoryDto {
    private int id;
    private String title;
    private String description;
    private Course course;
}
