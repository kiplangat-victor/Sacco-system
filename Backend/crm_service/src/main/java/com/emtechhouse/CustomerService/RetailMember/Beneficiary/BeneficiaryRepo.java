package com.emtechhouse.CustomerService.RetailMember.Beneficiary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiaryRepo extends JpaRepository<Beneficiary, Long> {
}
