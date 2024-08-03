package com.emtechhouse.System.ChargeParams.EventId.TieredCharges;

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
public class Tieredcharges {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String tierOption;
    private Double lowerLimit;
    private Double upperLimit;
    private Double chargeAmount;
    private Character usePercentage = 'N';
    private Double percentage;
}
