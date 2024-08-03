package com.emtechhouse.accounts.Models.Accounts.Loans.InterestReversal;

import com.emtechhouse.accounts.Models.Accounts.Loans.InterestReversal.IntReversalItem.IntReversalItem;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@EqualsAndHashCode(exclude = {"intReversalItems"})
public class InterestReversal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 200, nullable = false, unique = false)
    private String interestReversalCode;
    @Column(length = 200, nullable = false, unique = false)
    private String tranParticulars;

    private String status = CONSTANTS.ENTERED;

    private String transactionDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @ToString.Exclude
    private List<IntReversalItem> intReversalItems;

    public void setIntReversalItems(List<IntReversalItem> intReversalItems) {
        intReversalItems.forEach(employeeDetail -> employeeDetail.setInterestReversal(this));
        this.intReversalItems = intReversalItems;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String verifiedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character verifiedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date verifiedTime;
    private String rejectedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character rejectedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date rejectedTime;
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