package com.emtechhouse.System.MisSector.MisSubsector;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MissubsectorService {
    private final MissubsectorRepo missubsectorRepo;

    public MissubsectorService(MissubsectorRepo missubsectorRepo) {
        this.missubsectorRepo = missubsectorRepo;
    }

    public Missubsector addMissubsector(Missubsector missubsector) {
        try {
            return missubsectorRepo.save(missubsector);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Missubsector> findAllMissubsectors() {
        try {
            return missubsectorRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Missubsector findById(Long id){
        try{
            return missubsectorRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Missubsector findByMissubcode(String missubcode){
        try{
            return missubsectorRepo.findByMisSubcode(missubcode).orElseThrow(()-> new DataNotFoundException("Data " + missubcode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Missubsector updateMissubsector(Missubsector missubsector) {
        try {
            return missubsectorRepo.save(missubsector);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteMissubsector(Long id) {
        try {
            missubsectorRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}