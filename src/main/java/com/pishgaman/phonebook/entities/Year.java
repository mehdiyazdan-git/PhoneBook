package com.pishgaman.phonebook.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
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

    // Map storing counters for each company
    @ElementCollection
    @CollectionTable(name = "year_company_counters", joinColumns = @JoinColumn(name = "year_id"))
    @MapKeyJoinColumn(name = "company_id")
    @Column(name = "letter_counter")
    private Map<Company, Integer> letterCounters = new HashMap<>();

    public Year(Long id, Long name, Long startingLetterNumber) {
        this.id = id;
        this.name = name;
        this.startingLetterNumber = startingLetterNumber;
    }
}
