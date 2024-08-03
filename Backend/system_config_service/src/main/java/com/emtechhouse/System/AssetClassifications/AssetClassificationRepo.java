package com.emtechhouse.System.AssetClassifications;

import com.emtechhouse.System.SalaryCharges.SalaryCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetClassificationRepo extends JpaRepository<AssetClassification, Long> {
    Optional<AssetClassification> findByAssetClassificationCodeAndDeletedFlag(String assetClassificationCode, Character deleteFlag);
    List<AssetClassification> findByEntityIdAndDeletedFlag(String entityId, Character flag);
    Optional<AssetClassification> findByEntityIdAndAssetClassificationCode(String entityId, String assetClassificationCode);
    Optional<AssetClassification> findByAssetClassificationCode(String assetClassificationCode);
    List<AssetClassification> findAllByEntityIdAndDeletedFlagOrderByIdDesc(String entityId, Character deletedFlag);


}
