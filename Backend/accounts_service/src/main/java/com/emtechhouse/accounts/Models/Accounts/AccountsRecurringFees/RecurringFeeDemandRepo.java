package com.emtechhouse.accounts.Models.Accounts.AccountsRecurringFees;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RecurringFeeDemandRepo extends JpaRepository<RecurringFeeDemand, Long> {
    @Query(value = "SELECT distinct prf.end_date AS endDate, prf.event_code as eventCode, prf.next_collection_date AS nextCollectionDate, product.product_code " +
            "FROM product_recurring_fee prf JOIN product ON prf.product = product.id ", nativeQuery = true)
    List<ProductRecuringFee> getProductRecuringFees();
//    List

    interface ProductRecuringFee {
//        String getAccount();
        Date getNextCollectionDate();
        Date getEndDate();
        String getEventCode();
        String getProduct_code();
    }
}