package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxPaymentSlipRepository extends JpaRepository<TaxPaymentSlip, Long>, JpaSpecificationExecutor<TaxPaymentSlip> {
}