package com.pishgaman.phonebook.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class InsuranceSlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // شناسه

    @Column(nullable = false)
    private LocalDate issueDate; // تاریخ صدور فیش

    @Column(nullable = false)
    private String slipNumber; // شماره برگه

    @Enumerated(EnumType.STRING)
    private SlipType type; // نوع فیش (حق بیمه یا جریمه)

    @Column(nullable = false)
    private BigDecimal amount; // مبلغ فیش

    @Column(nullable = false)
    private LocalDate startDate; // تاریخ شروع دوره

    @Column(nullable = false)
    private LocalDate endDate; // تاریخ پایان دوره

    @Lob
    private byte[] file; // فایل فیش

    private String fileExtension; // پسوند فایل فیش
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // شرکت

    public enum SlipType {
        PREMIUM, // حق بیمه
        FINE // جریمه
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        InsuranceSlip that = (InsuranceSlip) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

