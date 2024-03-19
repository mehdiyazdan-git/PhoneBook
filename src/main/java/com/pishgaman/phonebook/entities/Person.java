package com.pishgaman.phonebook.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String fatherName;
    private String nationalId;
    private LocalDate birthDate;
    private String registrationNumber;
    private String postalCode;
    private String address;
    private String phoneNumber;
    @OneToMany(mappedBy = "person",orphanRemoval = true)
    private Set<BoardMember> boardMember;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private PersonDocument personDocument;

    public Person(Long id) {
        this.id = id;
    }
}
