package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PartTranRepository extends JpaRepository<PartTran, Long> {

    interface ShareCapitalYears{
        String getContrYear();
    }

    interface ShareCapitalYearWise{
        String getContrYear();
        Double getContrAmount();

    }

    interface ShareCapitalMonthWise{
        String getContrMonthName();
        Double getContrAmount();

    }


    interface ShareCapitalDayWise{
        String getContrMonthName();
        Double getContrAmount();

    }

    @Query(nativeQuery = true, value = "select year(tran_date) as contrYear from part_tran group by year(tran_date)")
    List<ShareCapitalYears> findhareCapitalYears();

    @Query(nativeQuery = true, value = "select year(pt.tran_date) as contrYear, sum(pt.tran_amount) as contrAmount from part_tran pt   join accounts ac on ac.acid=pt.acid where ac.product_code= :productCode group by year(pt.tran_date)")
    List<ShareCapitalYearWise> findShareCapitalYearWise(@Param("productCode") String productCode);

    @Query(nativeQuery = true, value = "select monthname(tran_date) as contrMonthName, sum(tran_amount) as contrAmount from part_tran pt join accounts ac on ac.acid=pt.acid where ac.product_code= :productCode and year(tran_date)= :contrYear group by monthname")
    List<ShareCapitalMonthWise> findShareCapitalMonthWise(@Param("productCode") String productCode, @Param("contrYear") String contrYear);

  @Query(nativeQuery = true, value = "select day(tran_date) as contrDay, sum(tran_amount) as contrAmount from part_tran pt join accounts ac on ac.acid=pt.acid where ac.product_code= :productCode and year(tran_date)= :contrYear and monthname(tran_date)= :monthName group by contrDay")
    List<ShareCapitalDayWise> findShareCapitalDayWise(@Param("productCode") String productCode, @Param("contrYear") String contrYear, @Param("monthName") String monthName);


    @Query(value = "SELECT * FROM part_tran pt WHERE pt.acid = :acid and part_tran_type = 'credit' and transaction_header_id IN (SELECT sn FROM dtd WHERE posted_flag = 'Y')  ", nativeQuery = true)
    List<PartTran>  getDeposits(@Param("acid") String acid);

    @Query(value = "SELECT * FROM part_tran pt LEFT JOIN accounts a  ON a.acid = pt.acid LEFT JOIN dtd d ON pt.transaction_header_id = d.sn where a.acid =:acid ORDER BY pt.sn DESC LIMIT 1", nativeQuery = true)
    Optional<PartTran> findLastPartTranDate(String acid);


    @Query(value = "SELECT * FROM accounts a \n" +
            "LEFT JOIN part_tran pt ON a.acid = pt.acid \n" +
            "LEFT JOIN dtd d ON pt.transaction_header_id = d.sn \n" +
            " WHERE DATE(pt.tran_date) = DATE(:date) AND d.eod_status != 'Y'", nativeQuery = true)
    List<PartTran> getTransactionDate(@Param("date") String date);
}
