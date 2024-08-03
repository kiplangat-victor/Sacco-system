package com.emtechhouse.System.Coinage;
import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j

public class CoinageService {
    public final CoinageRepo coinageRepo;

    public CoinageService(CoinageRepo coinageRepo) {
        this.coinageRepo = coinageRepo;
    }


    public Coinage addCoinage(Coinage coinage) {
        try {
            return coinageRepo.save(coinage);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Coinage> findAllCoinages() {
        try {
            return coinageRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Coinage findById(Long id){
        try{
            return coinageRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Coinage findCoinageCode(String coinageCode) {
        try{
            return coinageRepo.findCoinageByCoinageCode(coinageCode).orElseThrow(()-> new DataNotFoundException("Data " + coinageCode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Coinage updateCoinage(Coinage coinage) {
        try {
            return coinageRepo.save(coinage);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteCoinage(Long id) {
        try {
            coinageRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }


}
