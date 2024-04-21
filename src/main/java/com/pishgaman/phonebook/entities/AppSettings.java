package com.pishgaman.phonebook.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_settings")
@NoArgsConstructor
@Getter
@Setter
public class AppSettings  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long maxUploadFileSize;

    private String vsphereUrl;
    private String vsphereUsername;
    @Column(length = 2048) // For encrypted password storage if needed
    private String vspherePassword;
}

