package com.emtechhouse.accounts.Models.Accounts;

import com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces.AccountLookUpInterface;
import com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces.AccountLookUpInterfaceASIS;
import com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces.LoanVerificationInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AccountRepository extends JpaRepository<Account, Long>, AccountCustomRepo, JpaSpecificationExecutor<Account> {
    List<Account> findByDeleteFlag(Character deleteFlag);

    Optional<Account> findByAcid(String acid);

    Optional<Account> findById(Long id);

    @Query(value = "SELECT * FROM `accounts` WHERE `acid`=:acid", nativeQuery = true)
    Optional<Account> findByAccountId(String acid);

    @Query(value = "SELECT * FROM `accounts` WHERE `acid`=:acid", nativeQuery = true)
    List<Account> fetchAccountBalances(String acid);

    @Query(value = "SELECT id, acid, account_name, sol_code, verified_flag " +
            "FROM accounts " +
            "WHERE gl_subhead IN (SELECT gl_subhead_code FROM glsubhead WHERE gl_code = :glCode) " +
            "AND delete_flag = :deleteFlag", nativeQuery = true)
    List<AccountsByAccountType> findByGlCode(@Param("glCode") String glCode, @Param("deleteFlag") Character deleteFlag);


    @Query(value = "SELECT id, acid, account_name, sol_code, verified_flag " +
            "FROM accounts " +
            "WHERE gl_subhead IN (SELECT gl_subhead_code FROM glsubhead WHERE gl_code = :glCode) " +
            "AND delete_flag = :deleteFlag", nativeQuery = true)
    List<Account> findByGlCode();



    @Query(value = "SELECT a.posted_by FROM accounts a WHERE a.acid = :acid and a.entity_id=:entityId", nativeQuery = true)
    String findIfAccountIsPostedByMM(String acid, String entityId);


    @Query(value = "SELECT * FROM accounts a LEFT JOIN part_tran pt  ON pt.acid = a.acid LEFT JOIN dtd d ON pt.transaction_header_id = d.sn where a.acid =:acid ORDER BY a.acid DESC LIMIT 1", nativeQuery = true)
    Optional<Account> findAccountBalance(String acid);

    @Query(value = "SELECT  COALESCE(SUM(accounts.account_balance),0) as totalAccountBalance FROM accounts WHERE accounts.account_balance < -1 and accounts.account_status = 'ACTIVE' and accounts.account_type = 'LAA' AND accounts.customer_code = :customerCode and product_code = :productCode", nativeQuery = true)
    public Double sumTotalLoansByCustomerCodeAndProductCode( @Param("customerCode") String customerCode, @Param("productCode") String productCode);

    @Query(value = "SELECT  COALESCE(SUM(accounts.account_balance),0) as totalAccountBalance FROM accounts WHERE accounts.account_balance < -1 and accounts.account_status = 'ACTIVE' and accounts.account_type = 'LAA' AND product_code = 'OD' and accounts.customer_code = :customerCode", nativeQuery = true)
    public Double sumTotalLoansByProductCode(@Param("customerCode") String customerCode);

    @Query(value = "SELECT  COALESCE(SUM(accounts.account_balance),0) as totalAccountBalance FROM accounts WHERE accounts.account_balance < -1 and accounts.account_status = 'ACTIVE' and accounts.account_type = 'LAA' AND product_code = 'OD' and accounts.customer_code = :customerCode", nativeQuery = true)
    public Double getMobileLoanBalance(String customerCode);

    @Query(value = "Select coalesce(sum(ls.interest_amount),0) as totalInterestBalance from loan_schedule ls join loan l on l.sn=ls.loan_sn join accounts a on l.account_id_fk=a.id where a.account_balance < -1 and a.account_status = 'ACTIVE' and a.account_type = 'LAA' AND a.product_code = 'OD' and a.customer_code = :customerCode", nativeQuery = true)
    public Double getMobileLoanInterestAmount( String customerCode);

    @Query(value = "select customer_code from accounts where acid =:acid", nativeQuery = true)
    public String getTheCustomerCode( String acid);

    @Query(value = "select acid from accounts where product_code = 'OD' and account_balance < -1 and account_type = 'LAA' and customer_code =:customerCode order by id desc limit 1", nativeQuery = true)
    public String getOdAcid( String customerCode);

    @Query(value = "SELECT  COALESCE(SUM(accounts.account_balance),0) as totalAccountBalance FROM accounts WHERE accounts.account_status = 'ACTIVE' and accounts.account_type = 'SBA' AND accounts.customer_code = :customerCode", nativeQuery = true)
    public Double sumTotalSavingsByCustomerCodeAndProductCode( @Param("customerCode") String customerCode);

    @Query(value = "SELECT * FROM accounts a  LEFT JOIN part_tran pt ON a.acid = pt.acid LEFT JOIN dtd d ON pt.transaction_header_id = d.sn WHERE a.acid = :acid AND DATE(pt.tran_date) >= :sixMonthsTime AND DATE(pt.tran_date) <= :todayDate", nativeQuery = true)
    List<Account> findAccountSum(String acid, String todayDate, String sixMonthsTime);

    @Query(value = "SELECT SUM(CASE WHEN part_tran_type = 'Debit' THEN -1 * tran_amount ELSE tran_amount END) AS Balance\n" +
            "FROM part_tran pt join dtd on pt.transaction_header_id = dtd.sn JOIN accounts a on a.acid = pt.acid\n" +
            "where dtd.deleted_flag = 'N' AND a.acid = :acid AND dtd.posted_flag = 'Y'  AND DATE(pt.tran_date) between :siixMonthsTime AND :newDate", nativeQuery = true)
    Optional<Double> findLoanLimit(String acid, String siixMonthsTime, String newDate);


    @Query(value = "SELECT *" +
            "FROM accounts " +
            "WHERE gl_subhead IN (SELECT gl_subhead_code FROM glsubhead WHERE gl_code = :glCode) " +
            "AND delete_flag = :deleteFlag", nativeQuery = true)
    List<Account> findExpensesByGlCode(@Param("glCode") String glCode, @Param("deleteFlag") Character deleteFlag);

    @Query(value = "SELECT * " +
            "FROM accounts " +
            "WHERE gl_subhead IN (SELECT gl_subhead_code FROM glsubhead WHERE gl_code = :glCode) " +
            "AND delete_flag = :deleteFlag", nativeQuery = true)
    List<Account> findIncomesByGlCode(@Param("glCode") String glCode, @Param("deleteFlag") Character deleteFlag);

    @Query(value = "SELECT account_type FROM `accounts` WHERE `acid`=:acid", nativeQuery = true)
    String findAccountByType(String acid);

    @Query(value = "select account_balance from accounts where acid=:acid", nativeQuery = true)
    Double fetchAccountBalance(String acid);

    Optional<Account> findByAcidAndDeleteFlag(String Acid, Character flag);

    @Query(nativeQuery = true, value = "SELECT * FROM accounts where acid=:acid AND delete_flag = 'N'")
    Optional<Account> checkByAcid(@Param("acid") String acid);


    @Query(value = "SELECT COUNT(*) FROM accounts WHERE delete_flag = 'N' AND verified_flag = 'N'", nativeQuery = true)
    Integer countAllAccountsByAcid();

    @Query(value = "SELECT a.* FROM accounts a JOIN retailcustomer rc ON a.customer_code=rc.customer_code WHERE rc.unique_id=:nationalId AND a.delete_flag=:flag", nativeQuery = true)
    List<Account> findByNationalIdAndDeleteFlag(String nationalId, Character flag);

    @Query(value = "select id,acid,account_name, sol_code,verified_flag from accounts where account_type=:type AND delete_flag=:deleteFlag", nativeQuery = true)
    List<AccountsByAccountType> findByAccountTypeAndDeleteFlag(String type, Character deleteFlag);

    @Query(value = "select id,acid,account_name, sol_code,verified_flag from accounts where gl_subhead IN (select gl_subhead_code from glsubhead where gl_code = :glCode) AND delete_flag=:deleteFlag", nativeQuery = true)
    List<AccountsByAccountType> findByGlCodeAndDeleteFlag(String glCode, Character deleteFlag);

    @Query(value = "select id,acid,account_name, sol_code,verified_flag from accounts where account_type=:type AND delete_flag=:deleteFlag", nativeQuery = true)
    List<AccountsByAccountType> findByAccountTypeAndDeleteFlagLookUp(Character deleteFlag, String type);

    @Query(value = "select id,acid,account_name, sol_code,verified_flag from accounts where product_code=:productCode AND delete_flag=:deleteFlag", nativeQuery = true)
    List<AccountsByAccountType> findByProductCodeAndDeleteFlagLookUp(Character deleteFlag, String productCode);

    @Query(value = "SELECT * FROM `accounts` WHERE `product_code`='SA06' AND account_balance > 0.5", nativeQuery = true)
    List<Account> findOverPaidRepaymentAccounts();

    @Query(value = "SELECT * FROM `accounts` WHERE (select account_id_fk from loan where operative_acount_id = :acid  ) ORDER BY account_balance ASC LIMIT 1", nativeQuery = true)
    Account getLoanAccountByRepaymentAccount(@Param("acid") String acid);

    @Query(value = "SELECT product_code FROM accounts WHERE id IN (SELECT account_id_fk FROM loan WHERE sn = :sn)", nativeQuery = true)
    String productForLoan(@Param("sn") Long sn);

    @Query(value = "SELECT product_code FROM `accounts` WHERE `acid`=:acid", nativeQuery = true)
    String findAccountByProductCode(String acid);

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`posted_time`, a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`,a.product_code, p.product_code_desc, rc.unique_id FROM `accounts` a LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code where a.account_type = :accountType and a.delete_flag='N' and a.verified_flag = 'N' and    (a.rejected_flag  = 'N' OR  a.rejected_flag  is null )  ORDER BY id DESC limit 50", nativeQuery = true)
    List<AccountLookUpInterface> getApprovalList(@Param("accountType") String accountType);

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`,a.product_code, p.product_code_desc, rc.unique_id FROM `accounts` a LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code where a.account_type <> 'laa' and a.delete_flag='N'  and a.verified_flag = 'N' and    (a.rejected_flag  = 'N' OR  a.rejected_flag  is null )  ORDER BY id DESC limit 50", nativeQuery = true)
    List<AccountLookUpInterface> getNonLoanApprovalList();

    @Query(value = "SELECT * FROM accounts WHERE verified_flag  = 'Y' and account_type = 'LAA' and id IN (SELECT account_id_fk FROM loan WHERE disbursement_flag = 'Y' AND disbursement_verified_flag = 'N' AND loan_status = 'APPROVED') limit 50", nativeQuery = true)
    List<AccountLookUpInterface> getAllDisbursementApprovalList();

    @Query(value = "SELECT * FROM accounts WHERE product_code = :productCode and customer_code = :customerCode LIMIT 1", nativeQuery = true)
    Optional<Account> getAccountByCustomerCodeAndProduct(@Param("customerCode") String customerCode, @Param("productCode") String productCode);

    @Query(value = "SELECT * FROM accounts WHERE (product_code = 'G01' OR product_code = 'G02') and customer_code = :customerCode LIMIT 1", nativeQuery = true)
    Optional<Account> getOperativeAccount(@Param("customerCode") String customerCode);

    @Query(nativeQuery = true, value =
            "SELECT DISTINCT a.* " +
                    "FROM accounts a " +
                    "JOIN part_tran pt ON a.acid = pt.acid " +
                    "JOIN (" +
                    "    SELECT acid, MAX(tran_date) AS max_tran_date " +
                    "    FROM part_tran " +
                    "    WHERE tran_particulars NOT LIKE '%Excise duty for%' " +
                    "       AND tran_particulars NOT LIKE '%LEDGER FEES%' " +
                    "       AND tran_particulars NOT LIKE '%REVERSAL%' " +
                    "       AND tran_particulars NOT LIKE '%Welfare - %'" +
                    "    GROUP BY acid " +
                    ") pt2 ON pt.acid = pt2.acid " +
                    "WHERE a.product_code IN ('G01', 'G02') " +
                    "  AND a.verified_flag = 'Y' " +
                    "  AND pt2.max_tran_date < DATE_SUB(CURDATE(), INTERVAL 6 MONTH)")

    List<Account> findDormantAccounts();



    @Query(value = "SELECT * FROM accounts WHERE (`account_status` = 'DORMANT' OR `account_status` = 'CLOSED') AND `account_type` = 'LAA' AND `account_balance` < -2; ", nativeQuery = true)
    List<Account> findAccountsStatus();



    @Query(value = "SELECT a.`acid` FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code WHERE rc.unique_id=:nationalId AND rc.customer_code=:customerCode and a.delete_flag='N' AND a.product_code like 'G0%' AND a.account_type = 'SBA'  order by product_code limit 1", nativeQuery = true)
    Optional<String> getAccountByCustomerCodeAndNationalId(@Param("customerCode") String customerCode, @Param("nationalId") String nationalId);

    @Query(value = "SELECT `id` AS id, `acid` AS accountCode FROM accounts WHERE `account_type`=:accountType AND product_code=:productCode AND acid LIKE :productCodeInit and length(acid) >= :minsize ORDER BY `id` DESC LIMIT 1", nativeQuery = true)
    Optional<AccountCode> findAccountLastEntry(String accountType, String productCode, String productCodeInit, @Param("minsize")  Integer minsize);


    @Query(value = "SELECT  'Application' as verification, a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`,a.product_code, p.product_code_desc, rc.unique_id FROM `accounts` a LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code where a.account_type = 'laa'  and   (a.rejected_flag  = 'N' OR  a.rejected_flag  is null ) and a.delete_flag='N'  and a.verified_flag = 'N' ORDER BY id DESC limit 50", nativeQuery = true)
    List<LoanVerificationInterface> getUnverifiedLoanApplications();

    @Query(value = "SELECT 'Disbursement' as verification, a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`,a.product_code, p.product_code_desc, rc.unique_id FROM `accounts` a LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code JOIN (select * from loan WHERE disbursement_flag = 'Y' AND (disbursement_verified_flag = 'N' or disbursement_verified_flag is null  ) ) loan ON loan.account_id_fk = a.id where a.account_type = 'laa' and a.delete_flag='N' and   (a.rejected_flag  = 'N' OR  a.rejected_flag  is null )  and a.verified_flag = 'Y' ORDER BY id DESC limit 50", nativeQuery = true)
    List<LoanVerificationInterface> getUnverifiedLoanDisbursments();

    @Query(value = "SELECT MAX(acid) FROM Account WHERE gl_subhead = :glSubHead AND SUBSTRING(acid, 1, 3) = SUBSTRING(:glSubHead, 1, 3)")
    Optional<String> findMaxAcidByGlSubHead(@Param("glSubHead") String glSubHead);



//    List<Account> findByGlCode();

    interface AccountsByAccountType {
        public Long getid();

        public String getacid();

        public String getaccount_name();

        public String getsol_code();

        public String getverified_flag();
    }

    @Query(value = "SELECT Id, Acid, account_type AS AccountType, account_status AS AccountStatus," +
            " sol_code AS SolCode, product_code AS ProductCode, 'Product Desc' AS ProductCodeDesc," +
            " customer_code CustomerCode,  'NationalId' AS NationalId, account_name AS AccountName, opening_date AS OpeningDate, " +
            " verified_flag AS VerifiedFlag, posted_time AS PostedTime FROM accounts WHERE account_type = :type  ", nativeQuery = true)
    List<AccountLookUpInterfaceASIS> findByAccountType(@Param("type") String type);
    List<Account> findByAccountTypeAndAccountStatus(String type, String status);
    Optional<Account> findByAccountTypeAndCustomerCodeAndDeleteFlagAndVerifiedFlag(String accountType, String customerCode, Character deleteFlag, Character verifiedFlag);

    List<Account> findByCustomerCodeAndDeleteFlag(String customerCode, Character deleteFlag);

    List<Account> findByCustomerCodeAndProductCodeAndDeleteFlag(String customerCode, String productCode, Character deleteFlag);

    List<Account> findByVerifiedFlag(Character verifiedFlag);

    List<Account> findByCustomerTypeAndDeleteFlag(String customerType, Character deleteFlag);

    List<Account> findByAccountTypeAndCustomerTypeAndSolCodeAndAccountStatusAndDeleteFlag(String accountType, String customerType, String solCode, String accountStatus, Character deleteFlag);

    List<Account> findByAccountTypeAndCustomerTypeAndSolCodeAndAccountStatusAndDeleteFlagAndIsWithdrawalAllowed(String accountType, String customerType, String solCode, String accountStatus, Character deleteFlag, Boolean isWithdrawalAllowed);

    List<Account> findBySolCodeAndAccountTypeAndVerifiedFlagAndDeleteFlag(String solCode, String accountType, Character verifiedFlag, Character deleteFlag);
    @Query(value = "SELECT a.id, a.acid, d.document_title, d.document_image FROM account AS a JOIN account_document as d WHERE a.id:account_id_fk", nativeQuery = true)
    List<accountDocuments> findByAccountDocuments(Long account_id_fk);

    interface accountDocuments{
        String getcid();
        String getdocument_title();
        String getdocument_image();
    }
    @Query(value = ":query", nativeQuery = true)
    List<Account> getAccountsForLookUp1(StringBuilder query);

    //    Query for getting Customers Balance on by date range
    @Query(value = "SELECT  SUM(accounts.account_balance) as AccountBalance from accounts WHERE accounts.sol_code=:sol_code AND accounts.scheme_code=:scheme_code AND accounts.scheme_type =:scheme_type AND accounts.customer_code=:cust_code AND accounts.posted_time BETWEEN :from_date AND :to_date AND accounts.delete_flag='N'", nativeQuery = true)
    Account sumCustomerAccountBalance(String sol_code, String scheme_code, String scheme_type, String cust_code, Date from_date, Date to_date);

    //    Query to get filtered accounts
    @Query(value = "SELECT * FROM `accounts` WHERE accounts.customerType=:customerType AND accounts.account_type=:accountType AND accounts.solCode=:solCode AND accounts.account_status=:accountStatus AND accounts.delete_flag='N'", nativeQuery = true)
    Account filterAccounts(String customerType, String accountType, String solCode, String accountStatus);

    @Query(value = "select account_balance from accounts where acid=:acid", nativeQuery = true)
    Double getAccountBalance(String acid);

    @Query(value = "select (account_balance-book_balance) AS account_balance from accounts where acid=:acid", nativeQuery = true)
    Double getAvailableBalance(String acid);

    @Query(value = " SELECT account_balance FROM accounts WHERE customer_code=:custCode AND product_code='SA02'", nativeQuery = true)
    Double getSavingNonWithAcBal(String custCode);

    @Query(value = "SELECT `id` AS id, `acid` AS accountCode FROM accounts WHERE `account_type`=:accountType AND product_code=:productCode AND acid LIKE :productCodeInit ORDER BY `acid` DESC LIMIT 1", nativeQuery = true)
    Optional<AccountCode> findAccountLastEntry(String accountType, String productCode, String productCodeInit);


    @Query(value = "SELECT `id` AS id, `acid` AS accountCode FROM accounts WHERE `account_type`=:accountType AND `gl_subhead`=:glSubHead  ORDER BY `acid` DESC LIMIT 1", nativeQuery = true)
    Optional<AccountCode> findAccountLastEntryOfficeAccount(String accountType, String glSubHead);


    interface AccountCode {
        public Long getId();

        public String getAccountCode();
    }

    Optional<Account> findByProductCodeAndCustomerCode(String productCode, String customerCode);

    List<Account> findByProductCodeAndDeleteFlag(String productCode, Character deleteFlag);

    @Query(value = "SELECT * FROM accounts WHERE account_balance > 0.01 and product_code = 'DIV'", nativeQuery = true)
    List<Account> findLoadedDividendAccounts();

    @Query(value = "SELECT * FROM accounts WHERE (product_code = 'G01' OR  product_code = 'G02') and  customer_code =:customerCode limit 1", nativeQuery = true)
    Optional<Account> findOperativeAccount(@Param("customerCode") String customerCode);

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag` FROM `accounts` a  WHERE a.customer_code=:customerCode and a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> getAccountLookUpByCustomerCode(String customerCode);

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag` FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code WHERE rc.unique_id=:nationalId and a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> getAccountLookUpByNationalId(String nationalId);

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag` FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code WHERE a.acid=:accountNumber and a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> getAccountLookUpByAccountNumber(String accountNumber);

    @Query(value = "select id, acid, account_balance,account_status from accounts where customer_code=:customerCode and product_code=:productCode and delete_flag='N'", nativeQuery = true)
    Optional<AccountBalanceByAccountType> getAccountBalanceByCustomerType(String customerCode, String productCode);

    @Query(value = "select  *  from accounts where customer_code=:customerCode and account_type = 'laa' and delete_flag='N'", nativeQuery = true)
    List<Account> getLoansByCustomerCode(String customerCode);


    interface AccountBalanceByAccountType {
        public Long getId();

        public String getacid();

        public String getaccount_status();

        Double getaccount_balance();
    }

    // TODO : GENERAL ACCOUNTS LOOKUP
    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.`account_name`, a.posted_time, a.`verified_flag`,a.product_code, p.product_code_desc, rc.national_id FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code WHERE a.entity_id=:entityId AND a.account_type=:accountType AND a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> findAllByEntityIdAndAccountType(String entityId,String accountType);

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.customer_type, a.`account_name`, a.posted_time, a.`verified_flag`,a.product_code, p.product_code_desc, rc.national_id FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code WHERE a.entity_id=:entityId  AND a.customer_type=:customerType AND a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> findAllByEntityIdAndCustomerType(String entityId,String customerType);

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.`account_name`, a.posted_time, a.`verified_flag`,a.product_code, p.product_code_desc, rc.national_id FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code WHERE a.entity_id=:entityId AND a.account_status=:accountStatus AND a.account_type=:accountType AND a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> findAllByEntityIdAndAccountStatus(String entityId,String accountStatus, String accountType);

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.`account_name`, a.posted_time, a.`verified_flag`,a.product_code, p.product_code_desc, rc.national_id FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code WHERE a.entity_id=:entityId AND a.sol_code=:solCode AND a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> findAllByEntityIdAndSolCode(String entityId,String solCode);

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.`account_name`, a.posted_time, a.`verified_flag`,a.product_code, p.product_code_desc, rc.national_id FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code WHERE  a.entity_id=:entityId  AND a.customer_code=:customerCode AND a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> findAllByEntityIdAndCustomerCode(String entityId,String customerCode);

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.`account_name`, a.posted_time, a.`verified_flag`,a.product_code, p.product_code_desc, rc.national_id FROM `accounts` a  LEFT JOIN retailcustomer AS rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code WHERE  a.entity_id=:entityId  AND a.acid=:acid AND a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> findAllByEntityIdAndAcid(String entityId,String acid);

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.`account_name`,a.posted_time, a.`verified_flag`,a.product_code, p.product_code_desc, rc.national_id FROM `accounts` a  LEFT JOIN retailcustomer  rc ON a.customer_code=rc.customer_code LEFT JOIN product  p ON a.product_code=p.product_code WHERE rc.unique_id=:nationalId AND a.entity_id=:entityId AND a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> findAccountByEntityIdAndNationalId(String entityId,String nationalId);

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`, a.`account_name`, a.posted_time, a.`verified_flag`,a.product_code, p.product_code_desc, rc.national_id FROM `accounts` a LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code WHERE a.entity_id=:entityId AND a.posted_time BETWEEN :fromDate AND :toDate AND a.delete_flag='N' ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> findAllByEntityIdAndDateRange(String entityId, String fromDate, String toDate);


    // TODO : END OF GENERAL ACCOUNT LOOKUP

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`,a.product_code, p.product_code_desc FROM `accounts` a  JOIN product AS p ON a.product_code=p.product_code WHERE a.customer_code=:customerCode and a.account_type=:accountType and a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> fetchAccountLookUpByCustomerCodeAndAccountType(String customerCode, String accountType);

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`, a.product_code, p.product_code_desc FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code JOIN product AS p ON a.product_code=p.product_code WHERE rc.unique_id=:nationalId and a.account_type=:accountType and a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> getAccountLookUpByNationalIdAndAccountType(String nationalId, String accountType);

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`, a.`account_name`,a.`verified_flag`, a.product_code, p.product_code_desc  FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code JOIN product AS p ON a.product_code=p.product_code WHERE a.acid=:accountNumber and a.account_type=:accountType and a.delete_flag='N'", nativeQuery = true)
    List<AccountLookUpInterface> getAccountLookUpByAccountNumberAndAccountType(String accountNumber, String accountType);
    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`, a.`account_name`, a.posted_time, a.`verified_flag`,a.product_code, p.product_code_desc, rc.national_id FROM `accounts` a LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code WHERE a.entity_id=:entityId AND  a.account_type=:accountType AND a.posted_time BETWEEN :fromDate AND :toDate AND a.delete_flag='N' ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> getAccountLookUpByAccountTypeAndDateRange(String entityId, String accountType, String fromDate, String toDate);

    @Modifying(clearAutomatically = true)
    @Query(value = "update accounts set lien_amount=:amount where acid=:acid", nativeQuery = true)
    void updateLienBalance(Double amount, String acid);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE accounts SET account_balance=account_balance+:amount where acid=:acid", nativeQuery = true)
    void updateAccountBalance(Double amount, String acid);



    @Modifying(clearAutomatically = true)
    @Query(value = "update loan l join accounts a on a.id = l.account_id_fk set l.loan_status = 'DISBURSED' where " +
            "a.account_type = 'LAA' AND a.account_balance < -1;", nativeQuery = true)
    void updateLoanStatus();


    @Query(value = "SELECT `id`,`customer_code` from accounts where acid=:acid and `delete_flag`='N'", nativeQuery = true)
    Optional<CustomerCode> getCustomerCodeByAcidAndDeletedFlag(String acid);

    interface CustomerCode {
        public Long getId();

        public String getCustomer_code();
    }

    @Query(value = "SELECT `id`,`lien_amount` from accounts where acid=:acid and `delete_flag`='N'", nativeQuery = true)
    Optional<LienAmount> getLienAmountByAcidAndDeletedFlag(String acid);

    interface LienAmount {
        public Long getId();

        public Double getLien_amount();
    }

    @Query(value = "SELECT `id`,`lien_amount` from accounts where acid=:acid and `delete_flag`='N'", nativeQuery = true)
    Optional<WithdrawalStatus> getWithdrawalStatusByAcidAndDeletedFlag(String acid);

    interface WithdrawalStatus {
        public Long getId();

        public Boolean getIs_withdrawal_allowed();
    }

    @Query(value = "SELECT `id`,`receive_sms_notifications` FROM accounts where acid=:acid AND `delete_flag`='N'", nativeQuery = true)
    Optional<ReceiveSmsNotifications> getSmsNotificationStatusByAcidAndDeletedFlag(String acid);

    interface ReceiveSmsNotifications {
        public Long getId();

        public Boolean getReceive_sms_notifications();
    }

    @Query(value = "SELECT `id`,`customer_code`,`receive_email_notifications` FROM accounts where acid=:acid AND `delete_flag`='N'", nativeQuery = true)
    Optional<ReceiveEmailNotifications> getReceiveEmailNotificationsByAcidAndDeletedFlag(String acid);

    interface ReceiveEmailNotifications {
        public Long getId();

        public Boolean getReceive_email_notifications();

        public String getCustomer_code();
    }

    @Query(value = "SELECT `id` AS Id, `account_type` AS AccountType FROM `accounts` WHERE `acid`=:acid", nativeQuery = true)
    Optional<AccountType> getAccountType(String acid);

    @Query(value = "select COALESCE(sum(lien_amount), 0) from lien where   dr_account_id_fk in (select id from accounts where acid = :acid) and status = 'active' and curdate() between date(effective_from) and date(expiry_date)", nativeQuery = true)
    Double getTotalLienAmount(@Param("acid") String acid);

    interface AccountType {
        public Long getId();

        public String getAccountType();
    }

    @Query(value = "SELECT `id` AS Id, `account_balance` AS AccountBalance,`lien_amount` AS LienAmount, book_balance AS BookBalance  FROM `accounts` WHERE `acid`=:acid", nativeQuery = true)
    Optional<AcBalanceAndLien> getAccountBalanceAndLien(String acid);

    interface AcBalanceAndLien {
        public Long getId();

        public Double getAccountBalance();

        public Double getLienAmount();
        public Double getBookBalance();
    }

    @Query(value = "SELECT `id` AS Id, acid AS Acid, account_name as AccountName, `currency` As Currency,teller_account as TellerAccount, agency_account As AgencyAccount, account_type as AccountType, `account_status` AS AccountStatus,`is_withdrawal_allowed` AS IsWithdrawalAllowed,`verified_flag` AS VerifiedFlag, `account_balance` AS AccountBalance, `book_balance` AS BookBalance, `lien_amount` AS LienAmount,customer_code as Customercode,product_code as Productcode   FROM `accounts` WHERE `acid`=:acid", nativeQuery = true)
    Optional<AcTransactionDetails> getAccountTransactionDetails(String acid);

    interface AcTransactionDetails {
        public Long getId();
        public String getAcid();
        public String getAccountName();

        public Double getAccountBalance();
        public Double getBookBalance();

        public String getAccountType();

        public boolean getTellerAccount();
        public boolean getAgencyAccount();

        public Double getLienAmount();

        public String getAccountStatus();

        public Boolean getIsWithdrawalAllowed();

        public Character getVerifiedFlag();

        public String getCurrency();

        public String getCustomercode();

        public String getProductcode();
    }

    @Query(value = "SELECT `id` AS Id,`account_status` AS AccountStatus,`is_withdrawal_allowed` AS IsWithdrawalAllowed,`verified_flag` AS VerifiedFlag FROM `accounts` WHERE `acid`=:acid;", nativeQuery = true)
    Optional<WithdrawalAndAccountStatus> getWithdrawalAndAccountStatus(String acid);

    interface WithdrawalAndAccountStatus {
        public Long getId();

        public String getAccountStatus();

        public Boolean getIsWithdrawalAllowed();

        public Character getVerifiedFlag();
    }

    Optional<String> findAccountNameByAcid(String acid);

    @Query(value = "SELECT `currency` As Currency FROM `accounts` WHERE `acid`=:acid", nativeQuery = true)
    Optional<Ccy> getCurrencyByAcid(String acid);

    interface Ccy {
        public String getCurrency();
    }

    //    Getting Teller LImits
    @Query(value = "select office_account.cash_limit_cr as CashCreditLImit, office_account.cash_limit_dr as CashLimitDr, office_account.transfer_limit_cr as TransferLimitCr, office_account.transfer_limit_dr as TransferLimitDr from office_account \n" +
            "join accounts on office_account.account_id_fk = accounts.id where accounts.acid =:acid", nativeQuery = true)
    Optional<OfficeAccountLimits> getOfficeAccountLimitsByAcid(String acid);

    interface OfficeAccountLimits {
        public Double getCashCreditLImit();

        public Double getCashLimitDr();

        public Double getTransferLimitCr();

        public Double getTransferLimitDr();
    }


    List<Account> findByAccountTypeAndTellerAccountAndDeleteFlag(String accountType, boolean tellerAccount, char deleteFlag);

    @Query(value = "SELECT acid FROM `loan_migration`", nativeQuery = true)
    List<LoanAcid> getLoanAcids();

    @Query(value = "SELECT acid FROM `loan_migration_closed_ac`", nativeQuery = true)
    List<LoanAcid> getLoanAcids2();
    interface LoanAcid{
        public String getacid();
    }

    Boolean existsByAcid(String acid);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `accounts` SET `ledger_fee_next_colection_date`=:date WHERE `id`=:id", nativeQuery = true)
    void updateLedgerFeeNextCollectionDate(Long id, Date date);
    @Query(value = "SELECT * FROM accounts a WHERE a.account_status='ACTIVE' AND a.account_type !='OAB' AND a.delete_flag='N' AND a.verified_flag='Y' AND a.ledger_fee_next_colection_date<date(CURDATE())", nativeQuery = true)
    List<Account> getAccountToDemandLedgerFee();

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`,a.product_code, p.product_code_desc, rc.unique_id FROM `accounts` a LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code where approval_sent_flag = 'Y' AND approval_sent_to LIKE :userName ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> findAllByApprovalSentFlagAndApprovalSentTo(String userName);

    @Query(value = "SELECT a.`id`,a.`acid`, a.account_type, a.account_status, a.`sol_code`,a.`customer_code`, a.`account_name`, a.posted_time, a.`verified_flag`,a.product_code, p.product_code_desc, rc.national_id FROM `accounts` a LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code LEFT JOIN product AS p ON a.product_code=p.product_code WHERE a.entity_id=:entityId AND a.account_type LIKE :accountType AND a.posted_time BETWEEN :fromDate AND :toDate AND a.verified_flag='N' ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> findAllByEntityIdAndAccountTypeAndDateRange(String entityId, String accountType, String fromDate, String toDate);


    // TODO: 5/11/2023  lookup interface
    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`, a.product_code," +
            " p.product_code_desc FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code JOIN product p" +
            " ON a.product_code=p.product_code WHERE rc.unique_id LIKE %:nationalId% and a.account_type LIKE %:account_type% and  a.account_name LIKE %:account_name% " +
            "AND a.customer_code LIKE %:customer_code% AND a.acid LIKE %:acid% AND a.delete_flag='N' AND date(a.posted_time) BETWEEN :fromDate AND :toDate ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> accountLookUp(String nationalId,String account_type,String account_name,String customer_code,String acid, Date fromDate, Date toDate);

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`, a.product_code," +
            " p.product_code_desc FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code JOIN product p" +
            " ON a.product_code=p.product_code WHERE rc.unique_id LIKE %:nationalId% and a.account_type LIKE %:account_type% and  a.account_name LIKE %:account_name% " +
            "AND a.customer_code LIKE %:customer_code% AND a.acid LIKE %:acid% AND a.delete_flag='N' AND a.customer_type LIKE %:customerType% AND a.sol_code LIKE %:solCode% AND a.account_status LIKE %:accountStatus% AND date(a.posted_time) BETWEEN :fromDate AND :toDate ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> accountLookUp(String nationalId,String account_type,String account_name,String customer_code,String acid, Date fromDate, Date toDate, String customerType, String solCode, String accountStatus);


    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`, a.product_code, p.product_code_desc FROM " +
            "`accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code JOIN product p ON a.product_code=p.product_code WHERE" +
            " rc.customer_code=:customer_code ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> accountLookUpByCustomerCode(String customer_code);
    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`, a.product_code, p.product_code_desc FROM " +
            "`accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code JOIN product p ON a.product_code=p.product_code WHERE" +
            " rc.customer_code=:customer_code AND a.account_type=:account_type ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> accountLookUpByCustomerCodeAndAccountType(String customer_code, String account_type);

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`, a.product_code, p.product_code_desc" +
            " FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code JOIN product p ON a.product_code=p.product_code " +
            "WHERE a.acid=:acid ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> accountLookUpByAcid(String acid);

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`, a.product_code, p.product_code_desc" +
            " FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code JOIN product p ON a.product_code=p.product_code" +
            " WHERE rc.unique_id=:nationalId ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> accountLookUpByNationalId(String nationalId);

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`, a.product_code, p.product_code_desc" +
            " FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code JOIN product p ON a.product_code=p.product_code" +
            " WHERE rc.unique_id=:nationalId and AND a.account_type=:account_type ORDER BY id DESC", nativeQuery = true)
    List<AccountLookUpInterface> accountLookUpByNationalIdAndAccountType(String nationalId,  String account_type);

    @Query(value = "SELECT a.`id`,a.`acid`,a.`sol_code`,a.`customer_code`,a.`account_name`,a.`verified_flag`, a.product_code, p.product_code_desc" +
            " FROM `accounts` a  LEFT JOIN retailcustomer rc ON a.customer_code=rc.customer_code JOIN product p ON a.product_code=p.product_code" +
            " WHERE date(a.posted_time) BETWEEN :fromDate AND :toDate", nativeQuery = true)
    List<AccountLookUpInterface> findByFromDateAndFromDate(Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM accounts WHERE account_status = 'ACTIVE' and account_type = :accountType and product_code NOT IN ('DIV') AND customer_code IN (SELECT customer_code from retailcustomer WHERE phone_number = :phoneNumber) LIMIT 1", nativeQuery = true)
    Optional<Account> getAccountByAccountTypeAndPhone( @Param("accountType") String accountType, @Param("phoneNumber") String phoneNumber);


}