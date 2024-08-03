package com.emtechhouse.System.ChargeParams.ChargePreferentials;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargepreferentialRepo extends JpaRepository<Chargepreferential, Long> {
    @Query(value = "SELECT * FROM `chargepreferential` WHERE `chargepreferential`.`event_id`:event_id LIMIT 1", nativeQuery = true)
    Chargepreferential findByEventId(String event_id);
    @Query(value = "SELECT * FROM `chargepreferential` WHERE `chargepreferential`.`chrg_preferential`=:chrg_preferential AND `chargepreferential`.`event_type`=:event_type AND `chargepreferential`.`event_id`=:event_id AND `chargepreferential`.`account_id`=:account_id AND `chargepreferential`.`cif`=:cif AND `chargepreferential`.`organization_id`=:organization_id LIMIT 1", nativeQuery = true)
    Chargepreferential findActualChargePreferential(String chrg_preferential, String  event_type, String event_id, String account_id, String cif, String organization_id);
    //    check by Customer Level
    @Query(value = "SELECT * FROM `chargepreferential` WHERE `chargepreferential`.`chrg_preferential`=:chrg_preferential AND `chargepreferential`.`cif_id`=:cif_id LIMIT 1", nativeQuery = true)
    Optional<Chargepreferential> checkCustomerActualChargePreferential(String chrg_preferential,String cif_id);
    //    check by Account Level
    @Query(value = "SELECT * FROM `chargepreferential` WHERE `chargepreferential`.`chrg_preferential`=:chrg_preferential AND `chargepreferential`.`account_id`=:account_id LIMIT 1", nativeQuery = true)
    Optional<Chargepreferential> checkAccountActualChargePreferential(String chrg_preferential, String account_id);
    //    check by Charge Level
    @Query(value = "SELECT * FROM `chargepreferential` WHERE `chargepreferential`.`chrg_preferential`=:chrg_preferential AND `chargepreferential`.`event_type`=:event_type AND `chargepreferential`.`event_id`=:event_id LIMIT 1", nativeQuery = true)
    Optional<Chargepreferential> checkChargeActualChargePreferential(String chrg_preferential, String  event_type, String event_id);
    //    check by Contract Level
    @Query(value = "SELECT * FROM `chargepreferential` WHERE `chargepreferential`.`chrg_preferential`=:chrg_preferential AND `chargepreferential`.`organization_id`=:organization_id LIMIT 1", nativeQuery = true)
    Optional<Chargepreferential> checkContractActualChargePreferential(String chrg_preferential,String organization_id);

    @Query(nativeQuery = true, value = "select * from chargepreferential where is_deleted = 'Y'")
    List<Chargepreferential> findalldeletedChargepreferential();

    @Query(nativeQuery = true, value = "select * from chargepreferential where is_deleted = 'N'")
    List<Chargepreferential> findallundeletedChargepreferential();
    List<Chargepreferential> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);

}


