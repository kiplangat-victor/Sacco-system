package com.emtechhouse.System.SharecapitalParams;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SharecapitalParamService {
    private final SharecapitalParamRepo sharecapitalParamRepo;
    public SharecapitalParamService(SharecapitalParamRepo sharecapitalParamRepo) {
        this.sharecapitalParamRepo = sharecapitalParamRepo;
    }

    public SharecapitalParam addSharecapitalParam(SharecapitalParam sharecapitalParam) {
        try {
            return sharecapitalParamRepo.save(sharecapitalParam);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<SharecapitalParam> findAllSharecapitalParams() {
        try {
            return sharecapitalParamRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public SharecapitalParam findById(Long id){
        try{
            return sharecapitalParamRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public SharecapitalParam findByMisCode(String shareCapitalCode){
        try{
            return sharecapitalParamRepo.findByShareCapitalCode(shareCapitalCode).orElseThrow(()-> new DataNotFoundException("Data " + shareCapitalCode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public SharecapitalParam updateSharecapitalParam(SharecapitalParam sharecapitalParam) {
        try {
            return sharecapitalParamRepo.save(sharecapitalParam);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteSharecapitalParam(Long id) {
        try {
            sharecapitalParamRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}

