package com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavingContributionInstructionsRepo extends JpaRepository<SavingContributionInstructions, Long> {
    @Query(value = "SELECT * FROM saving_contribution_instructions WHERE status = 'ACTIVE' AND next_collection_date > '2023-02-20' AND DATE(next_collection_date) <= DATE_ADD(CURDATE(), INTERVAL  5 DAY) AND current_timestamp <  end_date ", nativeQuery = true)
    List<SavingContributionInstructions> findAllDue();
    @Query(value = "SELECT * FROM saving_contribution_instructions WHERE customer_code = :customerCode  AND status = 'ACTIVE' AND next_collection_date > '2023-02-20' AND DATE(next_collection_date) <= DATE_ADD(CURDATE(), INTERVAL  :daysAhead DAY) AND current_timestamp <  end_date LIMIT 1", nativeQuery = true)
    Optional<SavingContributionInstructions> findDueByCustomerCode(@Param("customerCode") String customerCode, @Param("daysAhead") int daysAhead);

    @Query(value = "SELECT * FROM saving_contribution_instructions WHERE id = :id  AND status = 'ACTIVE' AND DATE(next_collection_date) <= DATE_ADD(CURDATE(), INTERVAL  :daysAhead DAY) AND current_timestamp <  end_date LIMIT 1", nativeQuery = true)
    Optional<SavingContributionInstructions> findByIdDue(@Param("id") Long id, @Param("daysAhead") int daysAhead);

    Optional<SavingContributionInstructions> findBySavingCode(String customerType);


    @Query(value = "SELECT * FROM saving_contribution_instructions WHERE  saving_code IN(SELECT max(saving_code) FROM saving_contribution_instructions WHERE customer_code = :customerCode)", nativeQuery = true)
    Optional<SavingContributionInstructions> findMaxByCustomerCode(@Param("customerCode") String customerCode);

    @Query(value = "SELECT * FROM saving_contribution_instructions WHERE deleted_flag = :deletedFlag", nativeQuery = true)
    List<SavingContributionInstructions> findByDeletedFlag(Character deletedFlag);
    @Query(value = "SELECT * FROM saving_contribution_instructions WHERE deleted_flag = :deletedFlag and verified_flag = :verifiedFlag", nativeQuery = true)
    List<SavingContributionInstructions> findByDeletedFlagAndVerifiedFlag(Character deletedFlag, Character verifiedFlag);
    @Query(value = "SELECT * FROM saving_contribution_instructions WHERE deleted_flag = 'N' AND verified_flag='N' LIMIT 50", nativeQuery = true)
    List<SavingContributionInstructions> findAllUnVerifiedInstructions();
    @Query(value = "SELECT COUNT(*) FROM saving_contribution_instructions WHERE deleted_flag = 'N' AND verified_flag = 'N'", nativeQuery = true)
    Integer countAllSavingContributionInstructions();
}