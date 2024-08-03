package emt.sacco.middleware.Utils;

import okhttp3.MediaType;

public class Constants {
    public static final String BASIC_AUTH_STRING = "Basic";

    public static final String BEARER_AUTH_STRING = "Bearer";
    public static final String AUTHORIZATION_HEADER_STRING = "Authorization";
    public static final String CACHE_CONTROL_HEADER = "cache-control";
    public static final String CACHE_CONTROL_HEADER_VALUE = "no-cache";
    public static MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    public static final String TRANSACTION_STATUS_QUERY_COMMAND = "TransactionStatusQuery";
    public static final String CUSTOMER_PAYBILL_ONLINE = "CustomerPayBillOnline";
    public static final String ACCOUNT_BALANCE_COMMAND = "AccountBalance";
    public static final String TRANSACTION_STATUS_VALUE = "Transaction Status";
    public static final String MSISDN_IDENTIFIER = "1";
    public static final String TILL_NUMBER_IDENTIFIER = "2";
    public static final String SHORT_CODE_IDENTIFIER = "4";

    public static final Character NO = 'N';
    public static final Character YES = 'Y';


    public static final String NO_STRING = "N";
    public static final String YES_STRING = "Y";

    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String TRANSFER = "Transfer";
    public static final String CASH_DEPOSIT = "Cash Deposit";
    public static final String CASH_WITHDRAWAL = "Cash Withdrawal";

    public static final String AGENCY_NET_DEPOSIT = "AGENCY NET DEPOSIT";
    public static final String AGENCY_NET_WITHDRAWAL = "AGENCY NET WITHDRAWAL";
    public static final String PAYBILL_WITHDRAWAL = "Paybill Withdrawal";
    public static final String FUND_TELLER = "Fund Teller";
    public static final String COLLECT_TELLER_FUND = "Collect Teller Fund";
    public static final String POST_EXPENSE = "Post Expense";
    public static final String POST_OFFICE_JOURNALS = "Post Office Journals";
    public static final String RECONCILE_ACCOUNTS = "Reconcile Accounts";
    public static final String REVERSE_TRANSACTIONS = "Reverse Transactions";
    public static final String PETTY_CASH = "Petty Cash";
    public static final String CHEQUE_CLEARENCE = "Cheque Clearence";
    public static final String CHEQUE_BOUNCE = "Cheque Bounce";
    public static final String SALARY_UPLOAD = "Salary upload";
    public static final String BATCH_UPLOAD = "Batch upload";

    //Status
    public static final String ENTERED = "Entered";
    public static final String VERIFIED = "Verified";
    public static final String REVERSED = "Reversed";
    public static final String MODIFIED = "Modified";
    public static final String POSTED = "Posted";
    public static final String DELETED = "Deleted";
    public static final String CLEARED = "Cleared";
    public static final String BOUNCED = "Bounced";


    public static final String YESCHAR = "Y";
    public static final String OFFICE_ACCOUNT = "OAB";
    public static final String SAVINGS_ACCOUNT = "SBA";
    public static final String LOAN_ACCOUNT = "LAA";
    public static final String CURRENT_ACCOUNT = "CAA";
    public static final String TELLER_ACCOUNT = "TLA";

    //----Goodways sacco-----//

    public static final String ORDINARY_SAVINGS_ACCOUNT_PRODUCT_PERSONAL="G01";
    public static final String ORDINARY_SAVINGS_ACCOUNT_PRODUCT_BUSINESS="G02";
    public static final String SHARE_CAPITAL_PRODUCT="S02";
    public static final String DEPOSIT_CONTRIBUTION_PRODUCT="S03";

    //----Mwamba sacco-----//
    public static final String SHARE_CAPITAL_PROD = "SA01";
    public static final String SAVINGS_NON_WITHDRAWABLE_PROD = "SA02";
    public static final String REPAYMENT_ACCOUNT_PROD = "SA06";

    //svaings account flag
    public static final String ORDINARY_SAVINGS_ACCOUNT_FLAG="OSA";
    public static final String SHARE_CAPITAL_ACCOUNT_FLAG="SCA";
    public static final String DEPOSIT_CONTRIBUTION_ACCOUNT_FLAG="DCA";

    public static final String Normal = "Normal";
    public static final String Fee = "Fee";
    public static final String Tax = "Tax";


    public static final Character FULL = 'F';
    public static final Character DIFFERENTIAL = 'D';
    public static final Character DEBIT = 'D';
    public static final Character CREDIT = 'C';

    public static final String Debit = "Debit";
    public static final String Credit = "Credit";




    public static final Character DAILY = 'D';
    public static final Character ANNUAL = 'A';
    public static final Character WEEKLY = 'Y';
    public static final Character MONTHLY = 'M';
    public static final String SOL_ACCOUNTS = "SLA";
    public static final String TERM_DEPOSIT = "TDA";
    public static final String OVERDRAFT_ACCOUNT = "ODA";

    public static final String RETAIL_CUSTOMER="RETAIL";
    public static final String CORPORATE_CUSTOMER="CORPORATE";

    //account status
    public static final String ACTIVE="ACTIVE";
    public static final String NOT_ACTIVE="NOT_ACTIVE";
    public static final String CLOSED="CLOSED";
    public static final String SUSPENDED="SUSPENDED";
    public static final String DORMANT="DORMANT";
    public static final String FROZEN="FROZEN";

    //transcation types
    public static final String DEBITSTRING="Debit";
    public static final String CREDITSTRING="Credit";
    public static final String INCOMINGDEBITSTRING="Debit";
    public static final String INCOMINGCREDITSTRING="Credit";
    public static final String FAILED="FAILED";

    public static final String SUCCUSSEFUL="SUCCUSSEFUL";

    //loan interest method
    public static final String REDUCING_BALANCE="REDUCING_BALANCE";
    public static final String FLAT_RATE="FLAT_RATE";

    //loan status
    public static final String NEW="NEW";
    public static final String APPROVED="APPROVED";
    public static final String DISBURSED="DISBURSED";
    public static final String FULLY_PAID="FULLY_PAID";

    //term deposits
    public static final String MATURED="MATURED";
    public static final String MATURED_PAID="MATURED_PAID";

    //DEMANDS TYPES
    public static final String INTEREST_AND_PRINCIPAL_DEMAND="INTEREST_AND_PRINCIPAL_DEMAND";
    public static final String INTEREST_DEMAND="INTEREST_DEMAND";
    public static final String PRINCIPAL_DEMAND="PRINCIPAL_DEMAND";
    public static final String FEE_DEMAND="FEE_DEMAND";
    public static final String PENAL_INTEREST_DEMAND="PENAL_INTEREST_DEMAND";

    //interest type
    public static final String PENAL_INTEREST="PENAL_INTEREST";
    public static final String NORMAL_INTEREST="NORMAL_INTEREST";

    //system username and entity id
    public static final String SYSTEM_USERNAME="SYSTEM";
    public static final String SYSTEM="SYSTEM";
    public static final String SYSTEM_ENTITY= "001";

    //asset classification
    public static final String NOT_CLASSIFIED ="NOT_CLASSIFIED";
    public static final String PERFORMING="PERFORMING";
    public static final String WATCH = "WATCH";
    public static final String SUB_STANDARD="SUB_STANDARD";
    public static final String DOUBTFUL= "DOUBTFUL";
    public static final String LOSS= "LOSS";

    //lien status
    public static final String L_ACTIVE= "ACTIVE";
    public static final String L_SATISFIED= "SATISFIED";
    public static final String L_EXPIRED= "EXPIRED";
    public static final String L_REMOVED= "REMOVED";


    //loan limits validatios
    public static final String accMultiplier ="accMultiplier";
    public static final String TRAN_HISTORY="TRAN_HISTORY";

    //acid generation constants
    public static final String ACID_BRANCH="BRANCH";
    public static final String ACID_PRODUCT="PRODUCT";
    public static final String ACID_RUNNO="RUNNO";
    public static final String ACID_SUBGL="SUBGL";
    public static final String ACID_MCODE="MCODE";

    //loan guarantor type
    public static final String SALARY="SALARY";
    public static final String DEPOSITS="DEPOSITS";

    public static final String REJECTED = "REJECTED";
    public static final String SYSTEM_REVERSAL = "SYSTEM_REVERSAL";
    public static final String INTEREST = "INTEREST";


}
