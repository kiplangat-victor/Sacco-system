package co.ke.emtechhouse.CustomerProducts.Product_specific_details.ODA_Details;

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
public class ODA_Details {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 150)
    private String max_sanction_limit;
    @Column(length = 150)
    private String dr_bal_limit;
    @Column(length = 150)
    private String max_penal_int;
    @Column(length = 150)
    private String ac_statement_charged_by;
    @Column(length = 150)
    private String ac_stmt_chrg_per_page;
    @Column(length = 150)
    private String ac_stmt_chrg_fixed_amt;
    @Column(length = 150)
    private String inactive_ac_abnormal_trans_limit;
    @Column(length = 150)
    private String dormant_ac_abnormal_trans_limit;
    @Column(length = 150)
    private String duration_to_mark_ac_inactive;
    @Column(length = 150)
    private String duration_from_inactive_to_dormant;
    @Column(length = 150)
    private String dormant_fee;
    @Column(length = 150)
    private String norm_int_product_method;
    @Column(length = 150)
    private String penal_int_rate_method;
}