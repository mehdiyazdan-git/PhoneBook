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
public class TaxPaymentSlip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate issueDate; // تاریخ صدور فیش

    @Column(nullable = false)
    private String slipNumber; // شماره برگه

    @Enumerated(EnumType.STRING)
    private TaxPaymentSlipType type; // نوع مالیات

    @Column(nullable = false)
    private BigDecimal amount; // مبلغ

    @Column(nullable = false)
    private String period; // دوره مالی

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // شخصیت حقوقی

    @Lob
    private byte[] file; // فایل فیش

    private String fileExtension; // پسوند فایل فیش
    private String fileName;

    public enum TaxPaymentSlipType {
        CORPORATE_INCOME_TAX("فیش پرداخت مالیات عملکرد اشخاص حقوقی"),
        PAYROLL_TAX("فیش پرداخت مالیات بر حقوق"),
        VALUE_ADDED_TAX("فیش پرداخت مالیات بر ارزش افزوده"),
        QUARTERLY_TRANSACTIONS("فیش پرداخت معاملات فصلی"),
        PROPERTY_RENT_TAX("فیش پرداخت مالیات اجاره املاک"),
        PROPERTY_TRANSFER_TAX("فیش پرداخت مالیات نقل و انتقال املاک"),
        OTHER_FEES_AND_CHARGES("فیش پرداخت سایر عوارض و وجوه");

        private final String description;

        TaxPaymentSlipType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TaxPaymentSlip that = (TaxPaymentSlip) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

