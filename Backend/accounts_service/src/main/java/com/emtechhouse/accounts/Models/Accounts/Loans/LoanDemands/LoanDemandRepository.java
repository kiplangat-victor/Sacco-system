package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface LoanDemandRepository extends JpaRepository<LoanDemand, Long> {
    List<LoanDemand> findByAcidAndDemandCarriedFowardFlag(String acid, Character demandCarriedFowardFlag);
    List<LoanDemand> findByLoan(Loan loan);

    @Query(value = "select * from loan_demand l JOIN loan loan ON l.loan_sn=loan.sn JOIN accounts a ON loan.account_id_fk=a.id where l.`adjustment_amount`<l.`demand_amount` and l.demand_carried_foward_flag=:carriedForwardFlag and l.acid=:acid  and loan.loan_status='DISBURSED' and loan.next_repayment_date <= current_timestamp()  AND a.account_status='ACTIVE' AND a.delete_flag='N'  AND l.deleted_flag='N' AND l.demand_carried_foward_flag = 'N' ", nativeQuery = true)
    List<LoanDemand> findDemandsToSatisfy(String acid, Character carriedForwardFlag);
    @Query(value = "SELECT * FROM `loan_demand` ld JOIN loan l ON ld.loan_sn=l.sn JOIN accounts a ON l.account_id_fk=a.id WHERE ld.`adjustment_amount`<ld.`demand_amount` and ld.demand_carried_foward_flag=:carriedForwardFlag and ld.deleted_flag = 'N' and l.next_repayment_date <= current_timestamp()  AND l.loan_status='DISBURSED' AND a.account_status='ACTIVE' AND a.account_balance < -1 AND paused_satisfaction_flag = 'N' AND a.delete_flag='N'  AND ld.deleted_flag='N' AND l.operative_acount_id in (select acid from accounts where account_type = 'sba' and  (account_balance-book_balance-1) > 0 ) limit 1000", nativeQuery = true)
    List<LoanDemand> findUnsatisfiedLoanDemands(Character carriedForwardFlag);

    @Query(value = "SELECT * FROM `loan_demand` ld JOIN loan l ON ld.loan_sn=l.sn and l.sn = :loanSn JOIN accounts a ON l.account_id_fk=a.id WHERE ld.`adjustment_amount`<ld.`demand_amount` and ld.demand_carried_foward_flag=:carriedForwardFlag AND l.loan_status='DISBURSED' AND a.account_status='ACTIVE' AND a.account_balance < -1 AND paused_satisfaction_flag = 'N' AND a.delete_flag='N'  AND ld.deleted_flag='N' order by ld.demand_date", nativeQuery = true)
    List<LoanDemand> findUnsatisfiedLoanDemandsForLoan(Character carriedForwardFlag, Long loanSn);

    @Query(value = "select * from loan_demand where acid=:acid and demand_type=:demandType  AND delete_flag='N' ORDER BY sn DESC LIMIT 1", nativeQuery = true)
    Optional<LoanDemand> getLastLoanDemand(String acid, String demandType);
    @Query(value = "select * from loan_demand where acid=:acid ORDER BY sn DESC LIMIT 1", nativeQuery = true)
    Optional<LoanDemand> getLastLoanDemand1(String acid);

    @Query(value = "select * from loan_demand where acid=:acid and demand_type=:demandType and adjustment_amount<demand_amount and deleted_flag = 'N' order by demand_date asc LIMIT 1", nativeQuery = true)
    Optional<LoanDemand> getFirstUnsatisfiedDemand(String acid, String demandType);
    @Query(value = "SELECT *\n" +
            "FROM loan_demand\n" +
            "JOIN loan l ON l.sn = loan_demand.loan_sn\n" +
            "WHERE\n" +
            "    acid = :acid\n" +
            "    AND adjustment_amount < (demand_amount - 2)\n" +
            "    AND deleted_flag = 'N'\n" +
            "    AND l.loan_status = 'DISBURSED'\n" +
            "    AND (carried_forward_on IS NULL OR carried_forward_on IS NOT NULL)\n" +
            "ORDER BY\n" +
            "    CASE\n" +
            "        WHEN l.frequency_id = 'YEARS' THEN l.maturity_date\n" +
            "        ELSE l.next_repayment_date\n" +
            "    END DESC\n" +
            "LIMIT 1;\n", nativeQuery = true)
    Optional<LoanDemand> getFirstUnsatisfiedDemand(String acid);

//    @Query(value = "SELECT demand_date,\n" +
//            "    CASE\n" +
//            "        WHEN l.next_repayment_date = l.installment_start_date THEN l.maturity_date\n" +
//            "        ELSE l.next_repayment_date\n" +
//            "    END AS demand_date\n" +
//            "FROM\n" +
//            "    loan l\n" +
//            "JOIN\n" +
//            "    loan_demand ld ON l.sn = ld.loan_sn\n" +
//            "WHERE\n" +
//            "    l.loan_status = 'DISBURSED' AND ld.acid = :acid\n " +
//            "ORDER BY\n" +
//            "    demand_date ASC\n" +
//            "LIMIT 1;\n", nativeQuery = true)
//    Optional<LoanDemand> getFirstUnsatisfiedDemand(String acid);

    @Query(value = "select ld.sn As id, ac.account_name AS accountName, ac.acid AS accountId,ld.demand_amount AS demandAmount  from loan_demand ld join accounts ac ON ld.acid=ac.acid where date(ld.created_on)=:createdOnDate and  and ld.deleted_flag = 'N'", nativeQuery = true)
    List<DemandsInfo> getDemandGeneratedByDate(LocalDate createdOnDate);


    @Query(value = "SELECT COALESCE( (SELECT interest_amount AS total_interest_amount FROM loan_demand WHERE demand_date = ( SELECT MAX(demand_date) FROM loan_demand WHERE acid = :loanAcid ) AND acid = :loanAcid ), 0 ) AS total_interest_amount", nativeQuery = true)
    Double getSumUnpaidInterest(@Param("loanAcid") String loanAcid);


    @Query(value = "SELECT ( SELECT COALESCE( ( SELECT MAX(ls.`cumulative_interest`) FROM loan_demand ld JOIN loan l ON l.sn = ld.loan_sn JOIN loan_schedule ls ON ls.loan_sn = l.sn WHERE ld.`acid` = :loanAcid ), 0 ) ) - (SELECT COALESCE(SUM(CASE WHEN `adjustment_amount`>=`interest_amount` AND `demand_type`='INTEREST_AND_PRINCIPAL_DEMAND' OR `demand_type`='INTEREST_DEMAND' THEN `interest_amount` ELSE `adjustment_amount` END),0) FROM loan_demand WHERE `acid`= :loanAcid AND `demand_carried_foward_flag` !='Y')", nativeQuery = true)
    Double getTotalInterestUnPaid(@Param("loanAcid")String loanAcid);


    @Query(value = "select * from loan_demand where transaction_code = :transactionCode AND deleted_flag='N' ORDER BY sn DESC LIMIT 1", nativeQuery = true)
    Optional<LoanDemand> getDemandGeneratedByTransactionCode(@Param("transactionCode") String transactionCode);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_demand` SET `deleted_flag`='N' WHERE `transaction_code`=:transactionCode", nativeQuery = true)
    void deleteByTransactionCode(@Param("transactionCode") String transactionCode);

    interface DemandsInfo {
        public Long getId();
        public String getAccountName();
        public String getAccountId();
        public String getDemandAmount();
    }

    @Query(value = "select count(*) from loan_demand where date(created_on)=:createdOn", nativeQuery = true)
    Integer countDemandGeneratedOnACertainDate(LocalDate createdOn);

    @Query(value = "select a.id,a.acid, l.next_repayment_date from loan l JOIN accounts a ON l.account_id_fk=a.id where l.loan_status='DISBURSED' and l.next_repayment_date<=:dDate", nativeQuery = true)
    List<AccountsIdsInfo> getAccountsWhoseDemandsWereNotGeneratedByDate(LocalDate dDate);

    @Query(value = "SELECT acid FROM accounts WHERE account_type = 'laa' AND acid IN (SELECT acid FROM loan WHERE loan_status = 'DISBURSED')", nativeQuery = true)
    List<String> loansToClassify();

    //get all generated demands but not called by satisfaction function
    @Query(value = "select sn,acid,created_on from loan_demand where satisfaction_caller_flag='N'", nativeQuery = true)
    List<AccountsIds> getAccountsWhoseDemandsWereGeneratedButNotFoundBySatisfactionFunction();
    interface AccountsIdsInfo {
        Long getId();
String getAcid();
        LocalDate getNext_repayment_date();
    }
    interface AccountsIds{
        Long getSn();
        String getAcid();
        LocalDate getCreated_on();
    }

    public interface LoanRepaymentYears{
        String getRepaymentYears();
    }

    public interface LoanRepayment{
        Long getPaidDate();
        Long getPaidAmount();
    }


    public interface LoanRepaymentMonthWise{
        String getPaidDate();
        Long getPaidAmount();
    }

    @Query(nativeQuery = true, value = "select year(adjustment_date) as repaymentYears from loan_demand group by year(adjustment_date)")
    List<LoanRepaymentYears> findRepaymentYears();

    @Query(nativeQuery = true, value = "select year(adjustment_date) as paidDate, sum(adjustment_amount) as paidAmount from loan_demand group by year(adjustment_date)")
    List<LoanRepayment> findRepaymentYearWise();

    @Query(nativeQuery = true, value = "select monthname(adjustment_date) as paidDate, sum(adjustment_amount) as paidAmount from loan_demand where year(adjustment_date)= :paidYear group by paidDate")
    List<LoanRepaymentMonthWise> findRepaymentMonthWise(@Param("paidYear") String paidYear);


    @Query(nativeQuery = true, value = "select day(adjustment_date) as paidDate, sum(adjustment_amount) as paidAmount from loan_demand where year(adjustment_date)= :paidYear and monthname(adjustment_date)= :paidMonth")
    List<LoanRepayment> findRepaymentDayWise(@Param("paidYear") String paidYear, @Param("paidMonth") String paidMonth);


    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_demand` SET `adjustment_amount`=:amt WHERE `sn`=:sn", nativeQuery = true)
    void updateAdjustmentAmount(Double amt, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_demand` SET `adjustment_date`=:date WHERE `sn`=:sn", nativeQuery = true)
    void updateAdjustmentDate(Date date, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_demand` SET `loan_remaining_balance`=:bal WHERE `sn`=:sn", nativeQuery = true)
    void updateLoanRemainingBalance(Double bal, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_demand` SET `satisfaction_caller_flag`=:flag WHERE `sn`=:sn", nativeQuery = true)
    void updateSatisfactionCallerFlag(Character flag, Long sn);


    @Query(value = "SELECT COALESCE (SUM(demand_amount-adjustment_amount),0) AS arrears FROM `loan_demand` ld JOIN loan l ON ld.loan_sn=l.sn JOIN accounts a ON l.account_id_fk=a.id WHERE ld.`adjustment_amount`<ld.`demand_amount` and ld.demand_carried_foward_flag='N' AND l.loan_status='DISBURSED' AND a.account_status='ACTIVE' AND a.delete_flag='N' AND a.acid=:acid", nativeQuery = true)
    Double getSumArrears(String acid);
}



