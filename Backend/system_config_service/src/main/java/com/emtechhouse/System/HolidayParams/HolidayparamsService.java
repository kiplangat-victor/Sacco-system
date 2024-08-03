package com.emtechhouse.System.HolidayParams;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HolidayparamsService {
    private final HolidayparamsRepo holidayparamsRepo;

    public HolidayparamsService(HolidayparamsRepo holidayparamsRepo) {
        this.holidayparamsRepo = holidayparamsRepo;
    }

    public Holidayparams addHolidayparams(Holidayparams holidayparams) {
        try {
            return holidayparamsRepo.save(holidayparams);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Holidayparams> findAllHolidayparamss() {
        try {
            return holidayparamsRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Holidayparams findById(Long id){
        try{
            return holidayparamsRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Holidayparams updateHolidayparams(Holidayparams holidayparams) {
        try {
            return holidayparamsRepo.save(holidayparams);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteHolidayparams(Long id) {
        try {
            holidayparamsRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}