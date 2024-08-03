package co.ke.emtechhouse.CustomerProducts.Product_specific_details.LAA_Details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class LAA_Details {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 150)
    private Double loan_amt_min = 0.00;
    @Column(length = 150)
    private Double loan_max_amt = 0.00;
    private String individualLimits;
    private String individualLimitsProduct;
    @Column(length = 150)
    private String loan_period_min_mm = "1";
    @Column(length = 150)
    private String loan_period_max_mm = "12";
    @Column(length = 150)
    private String loan_repayment_method;
    @Column(length = 1500)
    private String loan_repayment_hold_op_ac = "N";
    @Column(length = 150)
    private String loan_repayment_upront_installment_collection = "N";
    @Column(length = 1500)
    private String flow_offset_based_on;
    //    chronological_order[]
//interest repayment details
    @Column(length = 150)
    private String int_base;
    @Column(length = 150)
    private String int_prod;
    @Column(length = 150)
    private String int_route_through;
    @Column(length = 150)
    private String fee_routed_through;
    @Column(length = 150)
    private String loan_int_ac;
    @Column(length = 150)
    private String penal_int_recognition_method;
    //INTEREST DETAILS
    @Column(length = 150)
    private String int_on_principal = "N";
    @Column(length = 150)
    private String principal_demand_overdue_at_endmonth;
    @Column(length = 150)
    private Integer principal_overdue_after_mm = 1;
    @Column(length = 150)
    private Integer int_overdue_after_mm = 1;
    @Column(length = 150)
    private Integer charge_overdue_after_mm = 1;
    @Column(length = 150)
    private String int_on_demand;
    @Column(length = 150)
    private String overdue_int_on_principal = "N";
    @Column(length = 150)
    private String int_demand_overdue_at_end_month = "N";
    @Column(length = 150)
    private String apply_preferential_int_for_overdue_int = "N";
    @Column(length = 150)
    private String charge_demand_overdue_end_month = "N";
    @Column(length = 150)
    private String int_rate_based_on_loan_amt = "N";
    @Column(length = 150)
    private String grace_period_for_late_fee_mm = "1";
    @Column(length = 150)
    private String apply_late_fee_for_delayed_payment;
    //PENAL INTEREST DETAILS
    @Column(length = 150)
    private String penal_int_on_principal_overdue = "N";
    @Column(length = 30)
    private String penal_int_on_int_demand_overdue = "N";
    @Column(length = 150)
    private String penal_int_based_on;
    @Column(length = 150)
    private String penal_int_product_method;
    @Column(length = 150)
    private String normal_int_product_method;
    @Column(length = 150)
    private String penal_int_rate_method;
    @Column(length = 150)
    private String grace_period_for_penal_int_mm;
    //EQUATED INSTALLMENT
    @Column(length = 150)
    private String equated_installment;
    @Column(length = 150)
    private String int_compound_freq;
    @Column(length = 150)
    private String outstanding_amt_after_last_inst;
    @Column(length = 150)
    private String ei_formula;
    @Column(length = 150)
    private String shift_installment_for_holiday;
    @Column(length = 150)
    private String includ_maturity_date_incase_for_holiday = "N";
    //ADVANCE INT
    @Column(length = 150)
    private String upfront_int_collection;
    @Column(length = 150)
    private String discounted_int;

    private Integer minGuarantorCount = 0;
    private Integer maxGuarantorCount = 100;
    private Double penal_int = 0.0;

    @OneToMany(targetEntity = LoanLimitCondition.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "laa_details", referencedColumnName = "id")
    private List<LoanLimitCondition> loanLimitsByProduct;
}