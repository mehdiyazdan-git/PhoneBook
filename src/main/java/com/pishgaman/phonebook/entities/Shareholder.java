package com.pishgaman.phonebook.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Shareholder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(nullable = false)
    private Integer numberOfShares;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentageOwnership;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal sharePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareType shareType;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Lob
    @Column(nullable = true)
    private byte[] scannedShareCertificate;

    public enum ShareType {
        REGISTERED, BEARER
    }
}
