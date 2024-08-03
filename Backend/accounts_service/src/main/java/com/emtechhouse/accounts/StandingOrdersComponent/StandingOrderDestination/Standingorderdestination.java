package com.emtechhouse.accounts.StandingOrdersComponent.StandingOrderDestination;

import com.emtechhouse.accounts.StandingOrdersComponent.Standingorderheader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Standingorderdestination {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String standingOrderCode;
    @Column(nullable = false)
    private String destinationAccountNo;
    private String description;
    @Column(nullable = false)
    private Double amount = 0.00;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "standingorderheader_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Standingorderheader standingorderheader;

}
