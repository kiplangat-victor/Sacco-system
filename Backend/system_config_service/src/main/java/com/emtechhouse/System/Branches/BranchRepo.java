package com.emtechhouse.System.Branches;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepo extends JpaRepository<Branch, Long> {
    Optional<Branch> findBranchByBranchCode(String branchCode);
    List<Branch> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    List<Branch> findAllByEntityIdAndDeletedFlagAndVerifiedFlagOrderByIdDesc(String entityId, Character flag, Character verifiedFlag);
    List<Branch> findBranchByDeletedFlag( Character deletedFlag);
    Optional<Branch> findBranchByBranchCodeAndDeletedFlag(String branchCode, Character deletedFlag);
    Optional<Branch> findByEntityIdAndBranchCodeAndDeletedFlag(String entityId, String branchCode, Character flag);
    List<Branch> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
