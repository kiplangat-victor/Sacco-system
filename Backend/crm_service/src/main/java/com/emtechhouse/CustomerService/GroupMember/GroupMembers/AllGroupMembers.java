package com.emtechhouse.CustomerService.GroupMember.GroupMembers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class AllGroupMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String serialNo;
    private String customerCode;
    private String customerName;
    private String contact;
    private BigDecimal totalSaving = new BigDecimal(0.00);
    private BigDecimal loanBalance = new BigDecimal(0.00);
}
