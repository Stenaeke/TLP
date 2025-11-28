package com.stenaeke.TLP.dtos.module;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ModuleDto {
    private Long id;
    private String title;
    private String content;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long subcategoryId;
}
