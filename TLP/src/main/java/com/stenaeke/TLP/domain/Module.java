package com.stenaeke.TLP.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "modules", schema = "tlp")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    private String title;

    @Lob
    private String content;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    private Boolean published;

}