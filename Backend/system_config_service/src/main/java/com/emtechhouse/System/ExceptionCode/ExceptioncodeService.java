package com.emtechhouse.System.ExceptionCode;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ExceptioncodeService {
    @Autowired
    private ExceptioncodeRepo exceptioncodeRepo;

    public Exceptioncode addExceptioncode(Exceptioncode exceptioncode) {
        try {
            return exceptioncodeRepo.save(exceptioncode);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Exceptioncode> findAllExceptioncodes() {
        try {
            return exceptioncodeRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Exceptioncode findById(Long id){
        try{
            return exceptioncodeRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Exceptioncode findByExceptionCode(String exceptionCode){
        try{
            return exceptioncodeRepo.findByExceptionCode(exceptionCode).orElseThrow(()-> new DataNotFoundException("Data " + exceptionCode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Exceptioncode updateExceptioncode(Exceptioncode exceptioncode) {
        try {
            return exceptioncodeRepo.save(exceptioncode);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteExceptioncode(Long id) {
        try {
            exceptioncodeRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}

