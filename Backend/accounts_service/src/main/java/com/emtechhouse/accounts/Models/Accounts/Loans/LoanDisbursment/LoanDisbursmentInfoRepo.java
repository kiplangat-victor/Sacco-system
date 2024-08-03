package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDisbursment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import java.util.List;

@Component
public interface LoanDisbursmentInfoRepo extends JpaRepository<LoanDisbursmentInfo , Long> {

    @Query(value = "select ldi.* from loan_disbursment_info ldi JOIN loan l ON ldi.loan_sn=l.sn JOIN accounts a ON l.account_id_fk=a.id WHERE acid=:acid", nativeQuery = true)
    List<LoanDisbursmentInfo> getLoanDisbursmentInfoByAcid(String acid);

}
