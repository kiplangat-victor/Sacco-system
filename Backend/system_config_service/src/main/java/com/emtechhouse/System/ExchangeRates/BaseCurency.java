package com.emtechhouse.System.ExchangeRates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BaseCurency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String baseCurrency="KES";
    private Double value;
    private String entityId;
    private String createdBy;
    private Date createdTime;
    private Date modifiedTime;
    private String modifiedBy;



}
