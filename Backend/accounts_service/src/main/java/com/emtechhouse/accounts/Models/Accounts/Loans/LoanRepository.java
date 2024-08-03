package com.emtechhouse.accounts.Models.Accounts.Loans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query(value = "select * from loan where total_loan_balance > 0", nativeQuery = true)
    List<Loan> findAllForSchedules();
    List<Loan> findByNextRepaymentDate(Date date);
    @Query(value = "SELECT l.* FROM `loan` l JOIN accounts a ON l.account_id_fk=a.id WHERE a.acid=:acid AND a.delete_flag='N' AND a.verified_flag='Y' and a.account_status='ACTIVE'", nativeQuery = true)
    Optional<Loan> findByAcid(String acid);

    @Query(value = "SELECT l.number_of_installments FROM loan l join accounts a on a.id = l.account_id_fk WHERE a.acid =:acid AND a.account_balance < -1", nativeQuery = true)
    String getNumberOfInstallments(String acid);

    @Query(value = "SELECT l.maturity_date FROM loan l join accounts a on a.id = l.account_id_fk WHERE a.acid =:acid", nativeQuery = true)
    Date getMaturityDate(String acid);
    @Query(value = "SELECT l.* FROM `loan` l JOIN accounts a ON l.account_id_fk=a.id WHERE a.acid=:acid AND a.delete_flag='N' AND  l.asset_classification = 'PERFORMING' AND a.verified_flag='Y' and a.account_status='ACTIVE'", nativeQuery = true)
    Optional<Loan> findByAcidToDemand(String acid);
    @Query(value = "SELECT l.* FROM `loan` l JOIN accounts a ON l.account_id_fk=a.id WHERE a.acid=:acid and a.account_status=\"ACTIVE\" and a.delete_flag=\"N\"", nativeQuery = true)
    Loan findByAcountId(String acid);

//    @Query(value = "SELECT distinct loan.* FROM loan JOIN accounts ON loan.account_id_fk=accounts.id WHERE loan.next_repayment_date<= current_timestamp AND loan.loan_status=\"DISBURSED\" AND loan.paused_demands_flag='N'  AND accounts.account_status='ACTIVE' AND accounts.account_balance < -1 AND accounts.delete_flag='N' and loan.sn NOT IN(select loan_sn from loan_demand where satisfaction_caller_flag = 'N') order by accounts.opening_date", nativeQuery = true)
    @Query(value = "SELECT distinct loan.* FROM loan JOIN accounts ON loan.account_id_fk=accounts.id WHERE loan.next_repayment_date<= current_timestamp AND loan.loan_status=\"DISBURSED\" AND loan.paused_demands_flag='N' AND loan.asset_classification  = 'PERFORMING' AND loan.frequency_id = 'MONTHS' AND accounts.account_status='ACTIVE' AND accounts.account_balance < -1 AND accounts.delete_flag='N' order by accounts.opening_date", nativeQuery = true)
    List<Loan> getLoansToDemand(Date demandDate, String loanStatus);

    @Query(value = "SELECT distinct loan.* FROM loan JOIN accounts ON loan.account_id_fk=accounts.id WHERE loan.disbursement_date<= current_timestamp AND loan.loan_status=\"DISBURSED\" AND loan.paused_demands_flag='N' AND loan.asset_classification  = 'PERFORMING' AND loan.frequency_id = 'YEARS' AND accounts.account_status='ACTIVE' AND accounts.account_balance < -1 AND accounts.delete_flag='N' order by accounts.opening_date", nativeQuery = true)
    List<Loan> getYearLoansToDemand(Date demandDate, String loanStatus);

    @Query(value = "SELECT distinct loan.* FROM loan JOIN accounts ON loan.account_id_fk=accounts.id WHERE loan.disbursement_date<= current_timestamp AND loan.loan_status=\"DISBURSED\" AND loan.paused_demands_flag='N' AND loan.asset_classification = 'PERFORMING' AND loan.frequency_id = 'MONTHS' AND loan.loan_period_months = 1 AND accounts.account_status='ACTIVE' AND accounts.account_balance < -1 AND accounts.delete_flag='N' AND accounts.product_code = 'OD' order by accounts.opening_date", nativeQuery = true)
    List<Loan> getOdLoansToDemand(Date demandDate, String loanStatus);

    @Query(value = "select COUNT(*) as count from loan where loan_status=:loanStatus", nativeQuery = true)
    Integer countByLoanStatus(String loanStatus);

    List<Loan> findByLoanStatus(String status);
    @Query(value = "SELECT * FROM loan l JOIN accounts a ON l.account_id_fk=a.id WHERE l.loan_status='DISBURSED' AND l.interest_calculation_method='FLAT_RATE' AND l.pause_accrual_flag='N' AND date(l.`accrual_last_date`) < date(NOW()) AND a.delete_flag='N' AND a.verified_flag='Y' and a.account_status='ACTIVE'", nativeQuery = true)
    List<Loan> getLoansToAccrue();

    @Query(value = "SELECT * FROM loan l JOIN accounts a ON l.account_id_fk=a.id WHERE l.loan_status='DISBURSED' AND l.interest_calculation_method='FLAT_RATE' AND l.pause_booking_flag='N' AND date(l.`booking_last_date`) < date(NOW()) AND a.delete_flag='N' AND a.verified_flag='Y' and a.account_status='ACTIVE'", nativeQuery = true)
    List<Loan> getLoansToBook();

    @Query(value = "SELECT * FROM loan WHERE sn IN (SELECT l.sn FROM `loan` l JOIN loan_demand ld ON ld.loan_sn=l.sn JOIN accounts a ON l.account_id_fk=a.id WHERE ld.`adjustment_amount`<ld.`demand_amount` and ld.demand_carried_foward_flag=:carriedForwardFlag AND l.loan_status='DISBURSED' AND a.account_status='ACTIVE' AND a.account_balance < -1 AND paused_satisfaction_flag = 'N' AND a.delete_flag='N')", nativeQuery = true)
    List<Loan> findLoansWithUnsatisfiedDemands(Character carriedForwardFlag);

    @Query(value = "select over_flow_amount from loan where sn=:sn", nativeQuery = true)
    Double getOverFlowAmount(Long sn);
    @Query(value = "SELECT `total_loan_balance` FROM `loan` WHERE `sn`=:sn", nativeQuery = true)
    Double getLoanTotalBalance(Long sn);
    @Query(value = "SELECT `sum_monthly_fee_demand` FROM `loan` WHERE `sn`=:sn", nativeQuery = true)
    Double getSumMonthlyFeeDemand(Long sn);

    @Query(value = "SELECT `sum_recurring_fee_demand` FROM `loan` WHERE `sn`=:sn", nativeQuery = true)
    Double getSumRecurringFeeDemand(Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "update loan set asset_classification=:assetClassification where sn=:sn", nativeQuery = true)
    void updateLoanAssetClassification(String assetClassification, Long sn);

    @Query(value = "select asset_classification from loan where sn = :sn", nativeQuery = true)
    String getAssetClassification(@Param("sn") Long sn);


    //update loan balance
    @Modifying(clearAutomatically = true)
    @Query(value = "update loan set total_loan_balance=:totalBalance where sn=:sn", nativeQuery = true)
    void updateLoanBalance(Double totalBalance, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "update loan set over_flow_amount=:overFlowAmt where sn=:sn", nativeQuery = true)
    void updateLoanOverFlowAmount(Double overFlowAmt, Long sn);


    @Query(value = " select adjustment_amount from loan_demand where sn=:sn", nativeQuery = true)
    Double getAdjustmentAmount(Long sn);

    @Query(value = "SELECT `over_flow_amount` FROM `loan` WHERE `sn`=:sn", nativeQuery = true)
    Double getOverFlowAmt(Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "update accounts set account_status=:status where id=:id", nativeQuery = true)
    void updateAccountStatus(String status, Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "update loan set loan_status=:status where sn=:sn", nativeQuery = true)
    void updateLoanStatus(String status, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan set `sum_monthly_fee_demand`=:amount where sn =:sn", nativeQuery = true)
    void updateLoanSumMonthlyFeeDemand(Double amount, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan set `sum_recurring_fee_demand`=:amount where sn =:sn", nativeQuery = true)
    void updateLoanRecurringFeeDemand(Double amount, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "update loan SET `next_repayment_date`=:date WHERE `sn`=:sn", nativeQuery = true)
    void updateLoanNextRepaymentDate(Date date, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "update loan SET `total_loan_balance`=:totalBalance WHERE `sn`=:sn", nativeQuery = true)
    void updateLoanTotalBalance(Double totalBalance, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "update loan SET `sum_principal_demand`=:amount WHERE `sn`=:sn", nativeQuery = true)
    void updateLoanSumPrincipalDemand(Double amount, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `sum_accrued_amount`=`sum_accrued_amount`+:amount WHERE `sn`=:sn", nativeQuery = true)
    void updateLoanSumAccruedAmount(Double amount, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `accrual_last_date`=:lastAccrualDate  WHERE `sn`=:sn", nativeQuery = true)
    void updateAccrualLastDate(Date lastAccrualDate, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `sum_booked_amount`=`sum_booked_amount`+:amount WHERE `sn`=:sn", nativeQuery = true)
    void updateLoanSumBookedAmount(Double amount, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET  `booking_last_date`=:bookingLastDate WHERE `sn`=:sn", nativeQuery = true)
    void updateBookingLastDate(Date bookingLastDate, Long sn);

    @Query(value = "SELECT `accrual_last_date` FROM loan WHERE `sn`=:sn", nativeQuery = true)
    Date getAccrualLastDate(Long sn);

    @Query(value = "SELECT `booking_last_date` FROM `loan` WHERE `sn`=:sn", nativeQuery = true)
    Date getBookingLastDate(Long sn);

    //disbursement
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `disbursement_amount`=:amount where `sn`=:sn", nativeQuery = true)
    void updateDisbursementAmount(Double amount, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `fees_amount`=:amount where `sn`=:sn", nativeQuery = true)
    void updateFeesAmount(Double amount, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `disbursement_flag`=:flag where `sn`=:sn", nativeQuery = true)
    void updateDisbursementFlag(Character flag, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `disbursement_date`=:date where `sn`=:sn", nativeQuery = true)
    void updateDisbursementDate(Date date, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `disbursed_by`=:user where `sn`=:sn", nativeQuery = true)
    void updateDisbursementBy(String user, Long sn);

//    @Query(value = "SELECT DISTINCT frequency_id AS LoanPeriods FROM loan JOIN accounts ON loan.account_id_fk = accounts.id WHERE accounts.account_status = 'ACTIVE' AND accounts.account_balance < -1 AND accounts.delete_flag = 'N' ORDER BY accounts.opening_date;", nativeQuery = true)
//    List<getLoanFrequency> getLoanFrequency(Date demandDate, String loanStatus);


    interface getLoanFrequency {
        String getLoanPeriods();

        String getLoanLength();
    }

    @Query(value = "SELECT DISTINCT frequency_id  FROM loan JOIN accounts ON loan.account_id_fk = accounts.id WHERE accounts.account_status = 'ACTIVE' AND accounts.acid =:acid AND accounts.account_balance < -1 AND accounts.delete_flag = 'N' ORDER BY accounts.opening_date;", nativeQuery = true)
    String getLoanFrequency(String acid);


    @Query(value = "SELECT product_code FROM `accounts` WHERE `acid`=:acid", nativeQuery = true)
    String findAccountByProduct(String acid);

    //disbursement verification
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `disbursement_verified_flag`=:flag where `sn`=:sn", nativeQuery = true)
    void updateDisbursementVerifiedFlag(Character flag, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `disbursment_verified_on`=:date where `sn`=:sn", nativeQuery = true)
    void updateDisbursmentVerifiedOn(Date date, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `disbursment_verified_by`=:user where `sn`=:sn", nativeQuery = true)
    void updateDisbursmentVerifiedBy(String user, Long sn);


    //payoff update queries
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `pay_off_flag`=:flag WHERE `sn`=:sn", nativeQuery = true)
    void updatePayoffFlag(Character flag, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `pay_off_date`=:date WHERE `sn`=:sn", nativeQuery = true)
    void updatePayOffDate(Date date, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `pay_off_initiated_by`=:user WHERE `sn`=:sn", nativeQuery = true)
    void updatePayOffInitiatedBy(String user, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `pay_off_verified_by`=:user WHERE `sn`=:sn", nativeQuery = true)
    void updatePayOffVerifiedBy(String user, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `pay_off_verification_flag`=:flag WHERE `sn`=:sn", nativeQuery = true)
    void updatePayOffVerificationFlag(Character flag, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET `pay_off_verification_date`=:date WHERE `sn`=:sn", nativeQuery = true)
    void updatePayOffVerificationDate(Date date, Long sn);

    @Query(value = "SELECT COALESCE(SUM(lg.initial_gee_amount),0) FROM `loan_guarantor` lg JOIN loan l ON lg.loan_sn=l.sn JOIN accounts a ON l.account_id_fk=a.id WHERE a.acid=:acid", nativeQuery = true)
    Double getSumInitialGuaranteed(String acid);

    @Query(value = "SELECT l.sn AS Sn, l.loan_type AS loanType, l.principal_amount as principalAmount,l.interest_amount AS interestAmount, l.repayment_period AS repaymentPeriodfrom,l.maturity_date AS maturityDateloan, l.disbursement_date AS disbursementDate, l.disbursement_flag AS disbursementFlag,l.disbursement_verified_flag AS disbursementVerifiedFlag, a.acid AS acid FROM loan AS l JOIN accounts a ON l.account_id_fk=a.id WHERE l.disbursement_flag='Y' AND l.disbursement_verified_flag ='N' LIMIT 50", nativeQuery = true)
    List<DisbursementDetails> findAllUnVerifiedDisbursement();
    @Query(value = "SELECT COUNT(*) FROM loan WHERE disbursement_flag='Y' AND disbursement_verified_flag='N'", nativeQuery = true)
    Integer countAllLoanDisbursement();

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan SET next_repayment_date = DATE_SUB(next_repayment_date, INTERVAL 1 MONTH) WHERE sn = :sn ", nativeQuery = true)
    void backDategByMonth(@Param("sn") Long sn);

    @Query(value = "select lds.*, ac.acid from (select l2.loan_sn, min(l2.demand_date) date from loan_demand l1 \n" +
            "join loan_demand l2 where l1.demand_date < l2.demand_date and l1.loan_sn = l2.loan_sn " +
            "and l1.deleted_flag = 'N' and l1.interest_amount > 0 and l2.deleted_flag = 'N' and l2.interest_amount > 0 " +
            " and l1.demand_amount > l1.adjustment_amount+10 \n" +
            "and   l2.demand_amount > l2.adjustment_amount+10 group by l2.loan_sn) lds  JOIN loan " +
            "ON lds.loan_sn = loan.sn JOIN accounts ac ON loan.account_id_fk = ac.id", nativeQuery = true)
    List<NonPerformanceLoanReversals> loansForNonPerformingLoans();



    interface NonPerformanceLoanReversals {
        String getAcid();
        String getLoan_sn();
        Date getDate();
    }

    interface DisbursementDetails {
        Long getSn();
        String getAcid();
         String getLoanType();
         Double getPrincipalAmount();
         Double getInterestAmount();
         Integer getRepaymentPeriod();
         Date getMaturityDate();
         Date getDisbursementDate();
         Character getDisbursementFlag();
         Character getDisbursementVerifiedFlag();
    }
}