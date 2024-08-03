package co.ke.emtechhouse.RepaymentSchedule;

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
public class RepaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;

    @Column(length = 15, nullable = false)
    private String account;
    private Integer version;
    @Column(precision = 10, scale = 2)
    private Double repaymentAmount;
    @Temporal(TemporalType.DATE)
    private Date repaymentDate;
    @Column(precision = 10, scale = 2)
    private Double cumulativeRepayment;
}
