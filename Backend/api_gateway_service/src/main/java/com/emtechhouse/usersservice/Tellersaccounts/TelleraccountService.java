package com.emtechhouse.usersservice.Tellersaccounts;

import com.emtechhouse.usersservice.utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TelleraccountService {
    private final TelleraccountRepo telleraccountRepo;

    public TelleraccountService(TelleraccountRepo telleraccountRepo) {
        this.telleraccountRepo = telleraccountRepo;
    }

    public Telleraccount addTelleraccount(Telleraccount telleraccount) {
        try {
            return telleraccountRepo.save(telleraccount);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Telleraccount> findAllTelleraccounts() {
        try {
            return telleraccountRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Telleraccount findById(Long id){
        try{
            return telleraccountRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Telleraccount updateTelleraccount(Telleraccount telleraccount) {
        try {
            return telleraccountRepo.save(telleraccount);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteTelleraccount(Long id) {
        try {
            telleraccountRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}