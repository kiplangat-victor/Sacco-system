package co.ke.emtechhouse.CustomerProducts.Product_exception;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Product_exceptionRepo extends JpaRepository<Product_exception, Long> {
}
