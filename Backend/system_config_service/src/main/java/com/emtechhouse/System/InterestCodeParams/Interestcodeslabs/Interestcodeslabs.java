package com.emtechhouse.System.InterestCodeParams.Interestcodeslabs;

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
public class Interestcodeslabs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double chargeAmount = 0.00;
    @Column(nullable = false)
    private Double lowerLimit = 0.00;
    private Integer percentage  = 1;
    private String slabName;
    @Column(nullable = false)
    private Double upperLimit = 0.00;
    private String usePercentage = "N";
    }
