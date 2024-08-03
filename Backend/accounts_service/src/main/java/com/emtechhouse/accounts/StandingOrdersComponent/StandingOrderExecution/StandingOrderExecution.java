package com.emtechhouse.accounts.StandingOrdersComponent.StandingOrderExecution;

import com.emtechhouse.accounts.StandingOrdersComponent.StandingOrderDestination.Standingorderdestination;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class StandingOrderExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String standingOrderCode;

    @OneToOne
    @JoinColumn(name = "destination_id")
    private Standingorderdestination destination;

    @OneToMany
    private List<PartTran> partTrans;

    @Column(nullable = false)
    private Double totalAmount = 0.0;

    @Column(nullable = false)
    private Date runDate;

    @Column(nullable = false)
    private Boolean closed = true;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @Column(nullable = false)
    private String status = "FAILED";
    private String failureReason = "";
}