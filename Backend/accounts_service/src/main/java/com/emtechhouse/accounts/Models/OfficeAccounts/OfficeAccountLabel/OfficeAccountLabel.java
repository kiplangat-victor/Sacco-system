package com.emtechhouse.accounts.Models.OfficeAccounts.OfficeAccountLabel;//package com.emtechhouse.accounts.Models.OfficeAccounts.OfficeAccountLabel;
//
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
//public class OfficeAccountLabel {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String accountLabel;
//    private Double labelValue;
//
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_id_fk")
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private OfficeAccount officeAccount;
//
//}
