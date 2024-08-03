package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface LoanDemandSatisfactionRepo extends JpaRepository<LoanDemandSatisfaction, Long> {

    @Query(value = "select ld.sn As id, ac.account_name AS accountName, ac.acid AS accountId,ld.demand_amount AS demandAmount, lds.amount AS amountSatisfied, ld.loan_remaining_balance AS loanRemainingBalance  from loan_demand_satisfaction lds JOIN loan_demand ld ON lds.loan_demand_fk= ld.sn JOIN accounts ac ON ld.acid=ac.acid where date(lds.date)=:createdOnDate", nativeQuery = true)
    List<DemandSatisfactionInfo> getDemandSatisfiedByDate(LocalDate createdOnDate);
    interface DemandSatisfactionInfo{
        public Long getId();
        public String getAccountName();
        public String getAccountId();
        public String getDemandAmount();
        public String getAmountSatisfied();
        public String getLoanRemainingBalance();
    }

    @Query(value = "SELECT COALESCE(SUM(CASE\n" +
            "           WHEN \n" +
            "           `adjustment_amount`>=`interest_amount`\n" +
            "           AND \n" +
            "           `demand_type`='INTEREST_AND_PRINCIPAL_DEMAND'\n" +
            "           OR \n" +
            "           `demand_type`='INTEREST_DEMAND'\n" +
            "           THEN\n" +
            "           `interest_amount` ELSE `adjustment_amount` END),0)AS interestPaid  FROM loan_demand WHERE `acid`=:acid" +
            " AND `demand_carried_foward_flag` !='Y'", nativeQuery = true)
    Double getTotalInterestPaid(String acid);

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>





    @Query(value = "SELECT COALESCE(SUM(CASE\n" +
            "           WHEN \n" +
            "           `adjustment_amount`>=`interest_amount`\n" +
            "           AND\n " +
            "           `demand_type`='INTEREST_AND_PRINCIPAL_DEMAND'\n" +
            "           THEN\n" +
            "           (`adjustment_amount`-`interest_amount`) ELSE 0 END),0)AS principal  FROM loan_demand WHERE `acid`=:acid " +
            " AND `demand_carried_foward_flag` !='Y'", nativeQuery = true)
    Double getTotalPrincipalPaid(String acid);



    @Query(value = "SELECT COALESCE(SUM(CASE\n" +
            "           WHEN \n" +
            "           `adjustment_amount`>=`interest_amount`\n" +
            "           AND\n" +
            "           `demand_type`='PENAL_INTEREST_DEMAND'\n" +
            "           THEN\n" +
            "           (`adjustment_amount`-`interest_amount`) ELSE 0 END),0)AS penalInt  FROM loan_demand WHERE `acid`=:acid " +
            " AND `demand_carried_foward_flag` !='Y'", nativeQuery = true)
    Double getPenalInterestPaid(String acid);



    @Query(value = "SELECT COALESCE (SUM(`demand_amount`-`adjustment_amount`),0) FROM loan_demand WHERE `acid`=:acid AND `demand_carried_foward_flag`='N'", nativeQuery = true)
    Double getSumUnsatisfiedDemands(String acid);
}
