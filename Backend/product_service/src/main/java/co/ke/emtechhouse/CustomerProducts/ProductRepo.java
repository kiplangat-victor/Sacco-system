package co.ke.emtechhouse.CustomerProducts;

import co.ke.emtechhouse.CustomerProducts.Dtos.LookupDto;
import co.ke.emtechhouse.CustomerProducts.Dtos.ProductEventDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    Optional<Product> findByEntityIdAndProductCodeAndDeletedFlag(String entityId, String productCode, Character flag);
    List<Product> findByEntityIdAndProductTypeAndDeletedFlag(String entityId,String productType,Character flag);

    List<Product> findByEntityIdAndProductCode(String entityId, String productCode);



    Optional<Product> findByProductCodeAndDeletedFlag(String productCode, Character flag);

    Optional<Product> findByProductTypeAndDeletedFlag(String productType, Character flag);

    @Query(value = "SELECT `product_code` AS productCode, product_type AS productType, `product_code_desc` AS productDescription ,deleted_flag AS deletedFlag , verified_flag As verifiedFlag FROM product WHERE product_code LIKE :productCode AND deleted_flag = 'N'", nativeQuery = true)
    List<LookupDto> findProdDtoByProductCode(String productCode);

    @Query(value = "SELECT `product_code` AS productCode, product_type AS productType, `product_code_desc` AS productDescription ,deleted_flag AS deletedFlag , verified_flag As verifiedFlag FROM product WHERE product_type LIKE :productType AND deleted_flag = 'N'", nativeQuery = true)
    List<LookupDto> findProdDtoByProductType(String productType);

    @Query(value = "SELECT `collect_ledger_fee`,`ledger_fee_event_id_code`,`product_code`,'acid' AS acid FROM product WHERE product_code=:prodCode", nativeQuery = true)
    Optional<ProductEventDto> findProductLedgerDetails(String prodCode);

}
