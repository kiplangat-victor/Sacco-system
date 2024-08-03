package com.emtechhouse.System.ChargeParams.ChargeAmtSlab;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChrgamtslapService {
    @Autowired
    private ChrgamtslapRepo chrgamtslapRepo;


    public List<Chrgamtslab> findAllChrgamtslaps(){
        try{
            return chrgamtslapRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Chrgamtslab findChrgamtslapById(Long id){
        try{
            return chrgamtslapRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Chrgamtslab updateChrgamtslap(Chrgamtslab chrgamtslab){
        try{
            return chrgamtslapRepo.save(chrgamtslab);
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public void deleteChrgamtslap(Long id){
        try{
            chrgamtslapRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
        }
    }
}
