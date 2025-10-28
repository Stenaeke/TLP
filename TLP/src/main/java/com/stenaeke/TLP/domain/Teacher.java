package com.stenaeke.TLP.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String encryptedPassword;

    @ManyToMany(mappedBy = "teachers")
    private Set<Course> courses = new HashSet<>();

}
