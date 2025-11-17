package com.stenaeke.TLP.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "courses", schema = "tlp")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private int id;

    private String title;

    private String description;

    @OneToMany(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},  orphanRemoval = true)
    private Set<Subcategory> subcategories = new LinkedHashSet<>();

    public void addSubcategory(Subcategory subcategory){
        subcategory.setCourse(this);
        this.subcategories.add(subcategory);
    }

    public void removeSubcategory(Subcategory subcategory){
        subcategory.setCourse(null);
        this.subcategories.remove(subcategory);
    }

}