package com.emtechhouse.CollateralService;

import com.emtechhouse.Utils.DataNotFoundException;
import com.emtechhouse.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CollateralService {
    private final CollateralRepo collateralRepo;

    public CollateralService(CollateralRepo collateralRepo) {
        this.collateralRepo = collateralRepo;
    }

    public Collateral addCollateral(Collateral collateral) {
        try {
            return collateralRepo.save(collateral);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Collateral> findAllCollaterals() {
        try {
            return collateralRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Collateral findById(Long id){
        try{
            return collateralRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Collateral updateCollateral(Collateral collateral) {
        try {
            return collateralRepo.save(collateral);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteCollateral(Long id) {
        try {
            collateralRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }


    public EntityResponse findByCollateralCode(String collateralCode){
        try {
            EntityResponse res = new EntityResponse<>();
            Optional<CollateralDto> collateral=collateralRepo.getCollateralDetails(collateralCode);
            if(collateral.isPresent()){
                CollateralDto c= collateral.get();
                res.setEntity(c);
                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.FOUND.value());
            }else {
                res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//    public EntityResponse findByCollateralCode(String collateralCode){
//        try {
//            EntityResponse res = new EntityResponse<>();
//            Optional<Collateral> collateral=collateralRepo.findByCollateralCodeAndDeletedFlag(collateralCode, 'N');
//            if(collateral.isPresent()){
//                Collateral c= collateral.get();
//                res.setEntity(c);
//                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
//                res.setStatusCode(HttpStatus.FOUND.value());
//            }else {
//                res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//                res.setStatusCode(HttpStatus.NOT_FOUND.value());
//            }
//            return res;
//        }catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
}
