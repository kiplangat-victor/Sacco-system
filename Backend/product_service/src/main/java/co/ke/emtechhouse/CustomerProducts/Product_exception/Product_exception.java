package co.ke.emtechhouse.CustomerProducts.Product_exception;


//import com.netflix.discovery.converters.Auto;
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
public class Product_exception {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String exception_code;
    private String exception_description;
}
