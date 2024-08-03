package co.ke.emtechhouse.OfficeProducts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfficeproductRepo extends JpaRepository<Officeproduct, Long> {
    Optional<Officeproduct> findByEntityIdAndProductCodeAndDeletedFlag(String entityId, String productCode, Character flag);
    List<Officeproduct> findByEntityIdAndProductTypeAndDeletedFlagOrderByIdDesc(String entityId, String productType, Character flag);
}
