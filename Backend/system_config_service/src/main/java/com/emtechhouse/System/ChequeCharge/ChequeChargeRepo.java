package com.emtechhouse.System.ChequeCharge;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChequeChargeRepo extends JpaRepository<ChequeCharge, Long> {
    Optional<ChequeCharge> findByChargeCode(String customerType);
    List<ChequeCharge> findByEntityIdAndDeletedFlag(String entityId, Character deletedFlag);
}
