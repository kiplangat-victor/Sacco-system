package com.emtechhouse.System.CustomerType;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerTypeRepo extends JpaRepository<CustomerType, Long> {
    Optional<CustomerType> findByCustomerType(String customerType);
    List<CustomerType> findByEntityIdAndDeletedFlag(String entityId, Character deletedFlag);
    @Query(value = "SELECT * FROM customer_type where db_table = :table AND deleted_flag <> 'Y'", nativeQuery = true)
    List<CustomerType> findByDb_table(@Param("table") String table);
    List<CustomerType> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}