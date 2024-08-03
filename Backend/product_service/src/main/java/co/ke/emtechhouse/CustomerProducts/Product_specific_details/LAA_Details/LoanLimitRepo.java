package co.ke.emtechhouse.CustomerProducts.Product_specific_details.LAA_Details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanLimitRepo extends JpaRepository<LoanLimitCondition, Long> {

    @Query(value = "SELECT * FROM loan_limit_condition WHERE laa_details IN(SELECT id FROM laa_details WHERE id IN (SELECT product_fk1 FROM product WHERE product_code = :productCode))", nativeQuery = true)
    List<LoanLimitCondition> findAllByProductCode(@Param("productCode") String productCode);
}
