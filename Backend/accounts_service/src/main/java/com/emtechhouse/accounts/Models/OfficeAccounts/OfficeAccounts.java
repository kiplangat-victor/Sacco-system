package com.emtechhouse.accounts.Models.OfficeAccounts;//package com.emtechhouse.accounts.Models.OfficeAccounts;
//
//import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
//import com.emtechhouse.accounts.Models.OfficeAccounts.OfficeAccountLabel.OfficeAccountLabel;
//import com.emtechhouse.accounts.Models.OfficeAccounts.OfficeAccountSpecificDetails.OfficeAccountSpecificDetails;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.ToString;
//
//import javax.persistence.*;
//import java.util.Date;
//import java.util.List;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
//@Entity
//public class OfficeAccount {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @Column(nullable = false)
//    private String acid;
//    @Column(nullable = false)
//    private String entityId;
//    @Column(nullable = false)
//    private String accountType;
//    @Column(nullable = false)
//    private Double accountBalance;
//    @Column(nullable = false)
//    private String accountManager;
//    @Column(nullable = false)
//    private String accountName;
//    @Column(nullable = false)
//    private String accountOwnership;
//    @Column(nullable = false)
//    private String accountStatus;
//    @Column(nullable = false)
//    private String currency;
//    @Column(nullable = false)
//    private String glCode;
//    @Column(nullable = false)
//    private String glSubhead;
//    @Column(nullable = false)
//    private String lienAmount;
//    @Column(nullable = false)
//    private Date openingDate;
//    @Column(nullable = false)
//    private String productCode;
//    @Column(nullable = false)
//    private String solCode;
//
//    //Operation Flags
//    @Column(nullable = false)
//    private Character postedFlag;
//    @Column(nullable = false)
//    private Date postedTime;
//    @Column(nullable = false, length = 15)
//    private String postedBy;
//    @Column(nullable = false)
//    private Character verifiedFlag;
//    private Date verifiedTime;
//    @Column(length = 15)
//    private String verifiedBy;
//    @Column(length = 15)
//    private String modifiedBy;
//    private Date modifiedTime;
//    @Column(nullable = false)
//    private Character deleteFlag;
//    private Date deleteTime;
//    @Column(length = 15)
//    private String deletedBy;
//
//    @OneToOne(mappedBy = "officeAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private OfficeAccountSpecificDetails officeAccountSpecificDetails;
//
//    @OneToMany(mappedBy = "officeAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<OfficeAccountLabel> officeAccountLabels;
//
//    public void  setOfficeAccountSpecificDetails(OfficeAccountSpecificDetails officeAccountSpecificDetails) {
//        officeAccountSpecificDetails.setOfficeAccount(this);
//        this.officeAccountSpecificDetails = officeAccountSpecificDetails;
//    }
//
//    public void  setOfficeAccountLabels(List<OfficeAccountLabel> officeAccountLabels) {
//        officeAccountLabels.forEach(officeAccountLabel -> officeAccountLabel.setOfficeAccount(this));
//        this.officeAccountLabels=officeAccountLabels;
//    }
//}
