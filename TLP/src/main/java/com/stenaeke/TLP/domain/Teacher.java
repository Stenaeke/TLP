package com.stenaeke.TLP.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;


@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class Teacher extends User {

    @ManyToMany(mappedBy = "teachers")
    private Set<Course> courses = new HashSet<>();

}
