package co.ke.emtechhouse.CustomerProducts.RecurringFee;

import co.ke.emtechhouse.CustomerProducts.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class ProductRecurringFee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String eventCode;
    @Column(nullable = false)
    private Date nextCollectionDate;
    @Column(nullable = false)
    private Date endDate;
    @Column(nullable = false)
    private String frequency = "MONTH";

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "product")
    @JsonIgnore
    private Product product;
}