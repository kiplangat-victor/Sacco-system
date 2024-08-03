package com.emtechhouse.System.ExceptionCode;

import com.emtechhouse.System.MisSector.Missector;
import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExceptioncodeRepo extends JpaRepository<Exceptioncode,Long> {

    Optional<Exceptioncode> findByExceptionCode(String exceptionCode);

    @Query(value = "SELECT * FROM exceptioncode WHERE is_deleted ='Y';", nativeQuery = true)
    List<Exceptioncode> findalldeletedCurrencies();

    @Query(value = "SELECT * FROM exceptioncode WHERE is_deleted ='N';", nativeQuery = true)
    List<Exceptioncode> findallundeletedCurrencies();

    List<Exceptioncode> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<Exceptioncode> findByEntityIdAndExceptionCodeAndDeletedFlag(String entityId, String exceptionCode, Character flag);
    Optional<Exceptioncode> findByEntityIdAndExceptionCode(String entityId, String exceptionCode);
    List<Exceptioncode> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}
