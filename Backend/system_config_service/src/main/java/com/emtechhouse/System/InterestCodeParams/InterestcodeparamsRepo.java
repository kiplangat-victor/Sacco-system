package com.emtechhouse.System.InterestCodeParams;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestcodeparamsRepo extends JpaRepository<Interestcodeparams, Long> {
    Optional<Interestcodeparams> findByEntityIdAndInterestCode(String entityId, String interestCode);
    List<Interestcodeparams> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<Interestcodeparams> findByEntityIdAndInterestCodeAndDeletedFlag(String entityId, String interestCode, Character flag);
    List<Interestcodeparams> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
