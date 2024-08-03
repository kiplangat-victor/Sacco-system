package com.emtechhouse.System.Welfare;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WelfareService {
    private final WelfareRepository welfareRepo;

    public WelfareService(WelfareRepository welfareRepo) {
        this.welfareRepo = welfareRepo;
    }

    public Welfare addWelfare(Welfare welfare) {
        try {
            return welfareRepo.save(welfare);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Welfare> findAllWelfares() {
        try {
            return welfareRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Welfare findById(Long id){
        try{
            return welfareRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Welfare updateWelfare(Welfare welfare) {
        try {
            return welfareRepo.save(welfare);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteWelfare(Long id) {
        try {
            welfareRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}
