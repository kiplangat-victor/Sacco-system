package com.emtechhouse.accounts.Models.Accounts.Loans.LoanPenalties;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface LoanPenaltyRepo extends JpaRepository<LoanPenalty, Long> {
    Optional<LoanPenalty> findByIdAndDeletedFlag(Long id, Character deletedFlag);

    List<LoanPenalty> findByLoanAndDeletedFlag(Loan loan, Character deletedFlag);
    List<LoanPenalty> findAllByDeletedFlagAndVerifiedFlag(Character deletedFlag, Character verifiedFlag);
//    List<LoanPenalty> findAllByDeletedFlagAndVerifiedFlag(Character deletedFlag, Character VerifiedFlag);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_penalties` SET `verified_flag`=:flag WHERE `id`=:id", nativeQuery = true)
    void updateVerifiedFlag(Character flag, Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_penalties` SET `verified_by`=:user WHERE `id`=:id", nativeQuery = true)
    void updateVerifiedBy(String user, Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_penalties` SET `verified_time`=:verifiedTime WHERE `id`=:id", nativeQuery = true)
    void updateVerifiedTime(Date verifiedTime, Long id);
}
