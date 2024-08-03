package com.emtechhouse.accounts.TransactionService.SalaryUploads;

import com.emtechhouse.accounts.TransactionService.SalaryUploads.Attachments.Attachment;
import com.emtechhouse.accounts.TransactionService.SalaryUploads.EmployeeDetails.Employeedetails;
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
@EqualsAndHashCode(exclude = {"employeeDetails"})
public class Salaryupload {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 200, nullable = false, unique = false)
    private String salaryUploadCode;
    @Column(length = 200, nullable = false, unique = false)
    private Double amount;
    @Column(length = 200, nullable = false, unique = false)
    private String debitAccount;
    @Column(length = 200, nullable = false, unique = false)
    private String tranParticulars;
    @Column(length = 200, nullable = false, unique = false)
    private String employerCode;
    //    @Column(length = 200, nullable = false, unique = false)
//    private String transactionType;
//    @Column(length = 200, nullable = false, unique = false)
//    private LocalDate transactionDate;
    @Column(length = 200)
    private String chequeNo;
    @Column(length = 200)
    private String chequeType;
    @Column(length = 200)
    private String chequeDate;
    @Column(length = 200)
    private String chequeStatus;

    @Column(length = 200, nullable = false)
    private String salaryChargeCode = "EMPTY";
    @Column(length = 1)
    private Character collectCharges;// Y or N
    private String eventIdCode;
    private String chargeFrom = CONSTANTS.Credit; // credit or debit
    private String tellerAccount;
    private String transactionCode;
    private String recipientType;
    private String status = CONSTANTS.ENTERED;

    //salaryupload
    private String drTransactionCode;
    private Double drTchargeAmount;
    private String drTransactionDate;

    @OneToMany(mappedBy = "salaryupload", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @ToString.Exclude
    private List<Employeedetails> employeeDetails;

    public void setEmployeeDetails(List<Employeedetails> employeeDetails) {
        employeeDetails.forEach(employeeDetail -> employeeDetail.setSalaryupload(this));
        this.employeeDetails = employeeDetails;
    }

    @OneToMany(targetEntity = Attachment.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "salaryupload_fk", referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Attachment> files;

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