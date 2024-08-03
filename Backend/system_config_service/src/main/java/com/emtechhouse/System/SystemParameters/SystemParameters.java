package com.emtechhouse.System.SystemParameters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class SystemParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 100)
    private String saccoName;
    @Column(nullable = false, length = 50)
    private String passwordPolicy;
    @Lob
    @Column(nullable = false)
    private String organisationLogo;
    @JsonIgnore
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false)
    private Date rcre;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date foundingDate;
    @Column(precision = 10, scale = 2, nullable = false)
    private Double minShareCapital;
    @Column(precision = 10, scale = 2, nullable = false)
    private Double sharePrice;
    @Column(length = 50, nullable = false)
    private String registrationNo;
    private Boolean codeSameWithRegistrationNo;
    private String saccoCode;
    @Column(nullable = false)
    private Character depositTaking;
    private Boolean takesMaturityPeriod;
    private Integer maturityPeriod; //Maturity period in months, if maturity
    @Column(precision = 10, scale = 2, nullable = false)
    private Double registrationFee;

}
