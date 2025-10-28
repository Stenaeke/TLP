package com.stenaeke.TLP.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;

    @ManyToMany(mappedBy = "students")
    private Set<Course> courses = new HashSet<>();

}


