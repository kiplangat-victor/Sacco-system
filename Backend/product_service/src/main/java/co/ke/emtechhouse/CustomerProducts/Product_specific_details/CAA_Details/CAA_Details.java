package co.ke.emtechhouse.CustomerProducts.Product_specific_details.CAA_Details;

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
public class CAA_Details {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 150)
    private String system_generated_no;
    @Column(length = 150)
    private String max_sanction_limit;
    @Column(length = 150)
    private String norm_int_product_method;
    @Column(length = 150)
    private String ac_statement_charged_by;
    @Column(length = 150)
    private String ledger_follio_fee;
    @Column(length = 150)
    private String dormant_ac_abnormal_trans_limit;
    @Column(length = 150)
    private String duration_to_mark_ac_inactive;
    @Column(length = 150)
    private String duration_from_inactive_to_dormant;
    @Column(length = 150)
    private String dormant_fee;
    @Column(length = 150)
    private String calc_freq_dr_week;
    @Column(length = 150)
    private String allow_debit_against_unclear_bal;
}