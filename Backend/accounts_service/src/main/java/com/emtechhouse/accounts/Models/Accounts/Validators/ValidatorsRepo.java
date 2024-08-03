package com.emtechhouse.accounts.Models.Accounts.Validators;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorInterfaces.ProductItem;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorInterfaces.RetailCustomerItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface ValidatorsRepo extends JpaRepository<Account, Long> {
    // Query to get customer code details
    @Query(value = "SELECT `id`,`customer_code`,`posted_flag`,`branch_code`,`phone_number`,`deleted_flag`,`verified_flag` FROM `retailcustomer` WHERE `customer_code`=:customerCode limit 1", nativeQuery = true)
    Optional<RetailCustomerItem> getRetailCustomerInfo(String customerCode);

    @Query(value = "SELECT `id`,`customer_code`,`posted_flag`,`branch_code`,`deleted_flag`,`verified_flag`,`primary_phone` as phone_number FROM `group_member` WHERE `customer_code`=:customerCode limit 1", nativeQuery = true)
    Optional<RetailCustomerItem> getGroupMemberInfo(String customerCode);

    @Query(value = "SELECT `id`,`product_type`,`product_code`,`verified_flag`,`deleted_flag`, `effective_from_date`,`effective_to_date` FROM `product` WHERE `product_code`=:productCode", nativeQuery = true)
    Optional<ProductItem> getProductInfo(String productCode);

    @Query(value = "select pg.id, p.product_code, pg.gl_subhead from productgls pg JOIN product p ON pg.product=p.id where p.product_code=:productCode and pg.gl_subhead_deafault='Yes' limit 1", nativeQuery = true)
    Optional<ProductGlInterface> getProductGlInfo(String productCode);
    interface ProductGlInterface{
        Long getId();
        String getProduct_code();
        String getGl_subhead();
    }

//    @Query(value = "SELECT `id`,`product_type`,`product_code`,`verified_flag`,`deleted_flag` FROM `officeproduct` WHERE `product_code`=:productCode", nativeQuery = true)
//    Optional<ProductItem> getOfficeProductInfo(String productCode);
}
