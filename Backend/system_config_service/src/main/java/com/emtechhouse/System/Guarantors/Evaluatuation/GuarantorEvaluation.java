package com.emtechhouse.System.Guarantors.Evaluatuation;

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
public class GuarantorEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String guarantor_id;
    private String borrower_id;
    private Double loan_amount = 0.00;
    private Double guaranteed_amount = 0.00;
    private String loan_duration;
    private Date application_date;
    private Date approved_date;
    private Date disbursment_date;
    private Date repayment_date;
    private String interest_rate;
    private String loan_id;
    private String loan_status = "-";
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 3, nullable = false)
    private String activeness_status="-";
    @Column(length = 3, nullable = false)
    private String subsequent_guarantee_status="-";
    @Column(length = 3, nullable = false)
    private String guarantor_loan_status="-";
    @Column(length = 3, nullable = false)
    private String shares_qualification_status="-";
    @Column(length = 3, nullable = false)
    private String maximum_active_guaranteed_no="-";
    @Column(length = 3, nullable = false)
    private String max_no_guaranteed_status= "-";
}
