package com.emtechhouse.System.ChargeParams.ChargePreferentials;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChargepreferentialService {
    @Autowired
    private ChargepreferentialRepo chargepreferentialRepo;

    public Chargepreferential addChargepreferential(Chargepreferential chargepreferential){
        try{
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
        return chargepreferentialRepo.save(chargepreferential);
    }
    public List<Chargepreferential> findAllChargepreferentials(){
        try{
            return chargepreferentialRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Chargepreferential findChargepreferentialById(Long id){
        try{
            return chargepreferentialRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Chargepreferential updateChargepreferential(Chargepreferential chargepreferential){
        try{
            return chargepreferentialRepo.save(chargepreferential);
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public void deleteChargepreferential(Long id){
        try{
            chargepreferentialRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
        }
    }
}
