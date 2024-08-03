package com.emtechhouse.System.MisSector;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MissectorService {
    private final MissectorRepo missectorRepo;

    public MissectorService(MissectorRepo missectorRepo) {
        this.missectorRepo = missectorRepo;
    }

    public Missector addMissector(Missector missector) {
        try {
            return missectorRepo.save(missector);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Missector> findAllMissectors() {
        try {
            return missectorRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Missector findById(Long id){
        try{
            return missectorRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Missector findByMisCode(String miscode){
        try{
            return missectorRepo.findByMisCode(miscode).orElseThrow(()-> new DataNotFoundException("Data " + miscode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Missector updateMissector(Missector missector) {
        try {
            return missectorRepo.save(missector);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteMissector(Long id) {
        try {
            missectorRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}

