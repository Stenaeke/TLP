package com.stenaeke.TLP.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;


@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class Student extends User {

    @ManyToMany(mappedBy = "students")
    private Set<Course> courses = new HashSet<>();

}


