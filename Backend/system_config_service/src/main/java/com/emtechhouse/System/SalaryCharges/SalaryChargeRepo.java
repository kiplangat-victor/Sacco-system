package com.emtechhouse.System.SalaryCharges;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryChargeRepo extends JpaRepository<SalaryCharge, Long> {
    Optional<SalaryCharge> findBySalaryChargeCode(String customerType);
    List<SalaryCharge> findByEntityIdAndDeletedFlag(String entityId, Character deletedFlag);
    List<SalaryCharge> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);
}
