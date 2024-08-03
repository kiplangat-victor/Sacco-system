package co.ke.emtechhouse.CustomerProducts.Product_fee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product_fee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String eventTypeCode;
    private String eventIdCode;
    private String method;
    private String deductable;
    private String multiple;
    private String amortize;
    private String demand_flow;
    private String dr_placeholder;
    private String cr_placeholder;
    private String apr;
    private String eir;
    private String amort_tenor;
    private String max_no_of_assesment;
}
