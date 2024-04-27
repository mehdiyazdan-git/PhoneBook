package com.pishgaman.phonebook.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Year extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long name;

    @ToString.Exclude
    @OneToMany(mappedBy = "year", orphanRemoval = true)
    private Set<Letter> letters = new LinkedHashSet<>();

    @Column(name = "starting_letter_number")
    private Long startingLetterNumber;

    public Year(Long id, Long name, Long startingLetterNumber) {
        this.id = id;
        this.name = name;
        this.startingLetterNumber = startingLetterNumber;
    }
}
