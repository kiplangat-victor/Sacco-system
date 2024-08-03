package co.ke.emtechhouse.CustomerProducts.product_gls;

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
public class Productgls {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String gl_code;
    private String gl_subhead;
    private String gl_subhead_description;
    private String gl_subhead_deafault;
    private String is_gl_subhead_deleted;
}
