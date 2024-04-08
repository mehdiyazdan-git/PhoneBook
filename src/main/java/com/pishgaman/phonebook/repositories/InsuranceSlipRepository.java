package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.InsuranceSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceSlipRepository extends JpaRepository<InsuranceSlip, Long>, JpaSpecificationExecutor<InsuranceSlip> {
}