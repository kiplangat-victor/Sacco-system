package com.emtechhouse.accounts.Models.Accounts.Migration;

import com.emtechhouse.accounts.Models.Accounts.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public interface MigrationRepo extends JpaRepository<Account, Long> {
    @Query(value = "select concat(first_name,' ',middle_name,' ',last_name) as accountName ,customer_code as customerCode from retailcustomer", nativeQuery = true)
    List<CustomerInterface> getCustomerInfo();

    @Query(value = "select customer_code AS customerCode,group_name AS accountName from group_member", nativeQuery = true)
    List<CustomerInterface> getGroupCustomerInfo();
    interface CustomerInterface {
        Long getId();
        String getAccountName();
        String getCustomerCode();
    }


    @Query(value = "select lm.* from loan_migration lm ", nativeQuery = true)
    List<LoanInterface> loanAccountsInfo();

    @Query(value = "select lm.* from loan_migration_closed_ac lm ", nativeQuery = true)
    List<LoanInterface> loanAccountsInfo2();

    @Query(value = "select lm.* from loan_migration lm join group_member gm ON  lm.customer_code=gm.customer_code", nativeQuery = true)
    List<LoanInterface> grploanAccountsInfo();


    @Query(value = "select lm.*, concat(rc.first_name,\" \",rc.middle_name,\" \",rc.middle_name) as account_name from loan_migration lm join retailcustomer rc ON  lm.customer_code=rc.customer_code LIMIT 1", nativeQuery = true)
    Optional<LoanInterface> loanAccountsInformation();

    @Query(value = "select a.acid AS acid from accounts a JOIN loan l ON l.account_id_fk=a.id where a.posted_by='MIGRATED' and a.product_code='LA20'", nativeQuery = true)
    List<String> allMigratedLoanAcids();

    @Query(value = "select a.acid AS acid from accounts a JOIN loan l ON l.account_id_fk=a.id where a.posted_by='MIGRATED' and a.product_code='LA20' and maturity_date>:maturityDate", nativeQuery = true)
    List<String> allMigratedLoanAcidsPayOff(LocalDate maturityDate);



    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE loan_migration SET `number_of_installments`=:installmentNumber WHERE acid=:acid", nativeQuery = true)
    void updateLoanInterestFlags(String acid, Integer installmentNumber);

    @Query(value = "SELECT * FROM `guarantor_migration`", nativeQuery = true)
    List<LoanGuarantorInterface> getGuarantors();
}
