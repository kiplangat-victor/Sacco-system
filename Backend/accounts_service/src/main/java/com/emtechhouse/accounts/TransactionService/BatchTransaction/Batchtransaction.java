package com.emtechhouse.accounts.TransactionService.BatchTransaction;


import com.emtechhouse.accounts.TransactionService.BatchTransaction.CreditAccounts.Creditaccount;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
@ToString
@Entity
//@EqualsAndHashCode(exclude = {""})
public class Batchtransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 200, nullable = false, unique = false)
    private String batchUploadCode;
    @Column(length = 200, nullable = false, unique = false)
    private Double amount;
    @Column(length = 200, nullable = false, unique = false)
    private String debitAccount;
    @Column(length = 200, nullable = false, unique = false)
    private String tranParticulars;
    private String tellerAccount;
    private String transactionCode;
    private String status = CONSTANTS.ENTERED;
    @Column(length = 1)
    private Character collectCharges;// Y or N
    private String eventIdCode;

    private String drTransactionCode;
    private String drTransactionDate;

    @OneToMany(mappedBy = "batchtransaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Creditaccount> creditaccounts;

    public void setCreditaccounts(List<Creditaccount> creditaccounts) {
        creditaccounts.forEach(creditaccount -> creditaccount.setBatchtransaction(this));
        this.creditaccounts = creditaccounts;
    }

    //*****************Operational Audit *********************
    @Column(length = 30)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String postedBy;
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character postedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date postedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String modifiedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character modifiedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date modifiedTime;
    private String rejectedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character rejectedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date rejectedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String verifiedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character verifiedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date verifiedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String verifiedBy_2;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character verifiedFlag_2 = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date verifiedTime_2;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String deletedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character deletedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date deletedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 100, nullable = false)

    private String enteredBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 1, nullable = false)
    private Character enteredFlag;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false)
    private Date enteredTime;
}