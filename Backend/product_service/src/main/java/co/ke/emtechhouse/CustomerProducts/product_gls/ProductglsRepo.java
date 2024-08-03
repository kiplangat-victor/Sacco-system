package co.ke.emtechhouse.CustomerProducts.product_gls;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductglsRepo extends JpaRepository<Productgls,Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM productgls where productgls.gl_subhead =:glsubhead")
    List<Productgls> findByGl__Subhead(String glsubhead);
}
