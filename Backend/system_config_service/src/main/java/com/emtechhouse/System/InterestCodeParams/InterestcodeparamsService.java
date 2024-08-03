package com.emtechhouse.System.InterestCodeParams;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class InterestcodeparamsService {
    private final InterestcodeparamsRepo interestcodeparamsRepo;

    public InterestcodeparamsService(InterestcodeparamsRepo interestcodeparamsRepo) {
        this.interestcodeparamsRepo = interestcodeparamsRepo;
    }

    public Interestcodeparams addInterestcodeparams(Interestcodeparams interestcodeparams) {
        try {
            return interestcodeparamsRepo.save(interestcodeparams);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Interestcodeparams> findAllInterestcodeparamss() {
        try {
            return interestcodeparamsRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Interestcodeparams findById(Long id){
        try{
            return interestcodeparamsRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Interestcodeparams updateInterestcodeparams(Interestcodeparams interestcodeparams) {
        try {
            return interestcodeparamsRepo.save(interestcodeparams);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteInterestcodeparams(Long id) {
        try {
            interestcodeparamsRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}
