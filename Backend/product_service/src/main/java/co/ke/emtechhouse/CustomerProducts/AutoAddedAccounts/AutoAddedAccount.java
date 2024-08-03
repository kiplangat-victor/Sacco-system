package co.ke.emtechhouse.CustomerProducts.AutoAddedAccounts;

import co.ke.emtechhouse.CustomerProducts.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AutoAddedAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String productCode;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Product product;
}
