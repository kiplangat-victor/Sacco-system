package com.emtechhouse.accounts.Models.OfficeAccounts.OfficeAccountSpecificDetails;//package com.emtechhouse.accounts.Models.OfficeAccounts.OfficeAccountSpecificDetails;
//
//import com.emtechhouse.accounts.Models.Accounts.Account;
//import com.emtechhouse.accounts.Models.OfficeAccounts.OfficeAccount;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.ToString;
//
//import javax.persistence.*;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
//@Entity
//public class OfficeAccountSpecificDetails {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String accountHeadName;
//    private String accountSupervisorName;
//    private String accountSupervisorId;
//
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_id_fk")
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private OfficeAccount officeAccount;
//}
