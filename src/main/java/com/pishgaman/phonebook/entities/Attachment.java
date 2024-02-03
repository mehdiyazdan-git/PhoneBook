package com.pishgaman.phonebook.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Lob
    private byte[] fileContent;

    @ManyToOne
    @JoinColumn(name = "letter_id")
    private Letter letter;

    @Column(name = "deletable")
    private boolean deletable = true;

    public Attachment(Long id) {
        this.id = id;
    }
}
