package co.ke.emtechhouse.CustomerProducts.Product_specific_details.LAA_Details;

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
public class LoanLimitCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String productCode;
    @Column(nullable = false)
    private String conditionType;
    private Double accMultiplier = 1.0;
    private Integer privTranPeriodMonth = 1;
    private Double historyMultiplier = 1.0;
    private Integer activeMonths = 1;
    private String limitStatementInWords;
}