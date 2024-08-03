package com.emtechhouse.System.AssetClassifications;


import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AssetClassificationService {
    @Autowired
    private AssetClassificationRepo assetClassificationRepo;


    public AssetClassification addMainClassification(AssetClassification mainClassification) {
        try {
            return assetClassificationRepo.save(mainClassification);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<AssetClassification> findAllMainClassifications() {
        try {
            return assetClassificationRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public AssetClassification findById(Long id){
        try{
            return assetClassificationRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public AssetClassification findByAssetClassificationCode(String assetClassificationCode){
        try{
            return assetClassificationRepo.findByAssetClassificationCodeAndDeletedFlag(assetClassificationCode, 'N').orElseThrow(()-> new DataNotFoundException("Data " + assetClassificationCode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public AssetClassification updateMainClassification(AssetClassification mainClassification) {
        try {
            return assetClassificationRepo.save(mainClassification);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteMainClassification(Long id) {
        try {
            assetClassificationRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}

