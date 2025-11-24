package com.stenaeke.TLP.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "subcategories", schema = "tlp")
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subcategory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Size(max = 255)
    private String title;

    private String description;

    @OneToMany(mappedBy = "subcategory", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},  orphanRemoval = true)
    private Set<Module> modules = new LinkedHashSet<>();

}