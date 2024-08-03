package com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
@Transactional
public interface LoanGuarantorRepository extends JpaRepository<LoanGuarantor, Long> {
    @Query(value = "select sum(lg.guarantee_amount) AS totalGuaranteeAmount, lg.guarantor_customer_code AS guarantorCustomerCode from loan_guarantor lg join loan l on l.sn=lg.loan_sn where l.loan_status !=:notEqualToLoanStatus AND lg.guarantor_customer_code=:customerCode", nativeQuery = true)
    Optional<TotalGivenAsGuarantee> getTotalAmountGivenToGuarantees(String customerCode, String notEqualToLoanStatus);
    interface TotalGivenAsGuarantee{
        public Double gettotalGuaranteeAmount();
        public String getguarantorCustomerCode();
    }

    //param product code
    //S02
    @Query(value = "select SUM(a.account_balance) AS account_balance from accounts a where (account_type = 'SBA' OR account_type = 'LAA') AND a.customer_code=:customerCode and a.delete_flag=:deleteFlag", nativeQuery = true)
    Optional<GuarantorSBABalance> getGuarantorShareCapitalAccountDetails(String customerCode, Character deleteFlag);
    interface GuarantorSBABalance {
        public Double getaccount_balance();
    }


    Boolean existsByGuarantorCustomerCodeAndLoan(String acid, Loan l);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_guarantor` SET `guarantee_amount`=:amt WHERE `id`=:id", nativeQuery = true)
    void updateGuaranteedAmt(Double amt, Long id);
}
