package co.ke.emtechhouse.CustomerProducts.AssetsClassification;

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
public class Assetsclassification {
     @Id
     @GeneratedValue(strategy = GenerationType.AUTO)
     private Long id;
     private Integer dpd_from;
     private Integer dpd_to;
     private String asset_classification;
     private String class_sub;
     private String int_accrue;
     private String int_book;
     private String int_apply;
     private String past_due;
     private String manual;
     private String ac_int_suspense;
     private String ac_penal_int_suspense;
     private String prov_dr;
     private String prov_cr;
}
