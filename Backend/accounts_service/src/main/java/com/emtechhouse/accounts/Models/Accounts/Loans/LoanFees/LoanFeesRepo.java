package com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
@Repository
@Transactional
public interface LoanFeesRepo extends JpaRepository<LoanFees, Long> {

//    @Query(value = "SELECT * FROM `loan_fees` lf JOIN loan l ON lf.loan_sn=l.sn JOIN accounts a ON l.account_id_fk=a.id WHERE lf.closed_flag = 'N' AND l.sn=:loanSn AND l.loan_status='DISBURSED' AND l.paused_demands_flag='N' AND a.account_status='ACTIVE' AND a.delete_flag='N'", nativeQuery = true)
//    List<LoanFees> findByAcountId(Long loanSn);

    @Query(value = "SELECT * FROM `loan_fees` lf JOIN loan l ON lf.loan_sn=l.sn JOIN accounts a ON l.account_id_fk=a.id WHERE lf.`monthly_amount`>0 AND l.sn=:loanSn AND l.loan_status='DISBURSED' AND l.paused_demands_flag='N' AND a.account_status='ACTIVE' AND a.delete_flag='N'", nativeQuery = true)
    List<LoanFees> findByAcountId(Long loanSn);

    @Query(value = "SELECT loan_fees.* FROM loan_fees JOIN loan ON loan_fees.loan_sn=loan.sn WHERE loan.loan_status=:loanStatus AND loan_fees.closed_flag = 'N'", nativeQuery = true)
    List<LoanFees> getFeesToDemand(String loanStatus);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan_fees SET `next_collection_date`=:nextCollectionDate where `id`=:id", nativeQuery = true)
    void updateNextCollectionDate(Date nextCollectionDate, Long id);
}
