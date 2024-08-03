package com.emtechhouse.accounts.TransactionService.SalaryUploads.EmployeeDetails;

import com.emtechhouse.accounts.TransactionService.SalaryUploads.Salaryupload;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Employeedetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String account;
    private String idNumber;
    private Double amount;
    private String accountName;
    private String memberNumber;
    private String Parttrantype = "Credit";



    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "salaryupload_fk")
    @JsonIgnore
    @ToString.Exclude
    private Salaryupload salaryupload;
}