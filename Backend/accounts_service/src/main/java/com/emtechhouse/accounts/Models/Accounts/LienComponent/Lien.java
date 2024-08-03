package com.emtechhouse.accounts.Models.Accounts.LienComponent;
import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.LienTransactions.LienTransactions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Lien {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    public Double lienAmount;
    @Column(nullable = false)
    public Double lienAdjustedAmount=0.0;
    @Column(nullable = false)
    public String lienType; //loan, fees
    @Column(nullable = false)
    public String priority="1";
    @Column(nullable = false)
    public Boolean impactImmediately = true;
    @Column(nullable = false)
    public Date effectiveFrom;
    @Column(nullable = false)
    public Date expiryDate = new Date(2024,1,1);
    @Column(nullable = false)
    public String status="ACTIVE"; //ACTIVE, SATISFIED, EXPIRED, REMOVED
    @Column(nullable = false, columnDefinition = "TEXT")
    public String lienDescription;
    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    public String lienCode;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String sourceAcid;
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String destinationAcid;

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String sourceAcc;

//    @Transient


//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    public String destinationAcc;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "dr_account_id_fk")
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnore
    private Account sourceAccount;

    private String sourcePartTranType;
    private String destinationPartTranType;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "cr_account_id_fk")
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnore
    private Account destinationAccount;

    @OneToMany(mappedBy = "lien", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    private List<LienTransactions> lienTransactions;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character postedFlag = 'N';

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date postedTime = new Date();

    @Column(nullable = false, length = 15)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String postedBy = "SYSTEM";

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character verifiedFlag = 'N';

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date verifiedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 15)
    private String verifiedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 15)
    private String modifiedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date modifiedTime;
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character deletedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date deletedTime;
    @Column(length = 15)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String deletedBy;

    private String getSourceAcc(){
       String acid= this.getSourceAccount().getAcid();
//        System.out.println("acid"+acid);
        return acid;
    }

    private String getDestinationAcc(){
        return this.getDestinationAccount().getAcid();
    }
}
