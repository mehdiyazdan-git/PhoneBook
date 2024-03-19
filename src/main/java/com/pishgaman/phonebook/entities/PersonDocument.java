package com.pishgaman.phonebook.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class PersonDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String documentName;
    private String documentType;
    private byte [] nationalIdFile;
    private byte [] birthCertificateFile;
    private byte [] cardServiceFile;
    private byte [] academicDegreeFile;
    private String fileExtension;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Person person;
}
