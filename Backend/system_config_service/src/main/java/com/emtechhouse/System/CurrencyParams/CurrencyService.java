package com.emtechhouse.System.CurrencyParams;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CurrencyService {
    @Autowired
    private CurrencyRepo currencyRepo;

    public Currency addCurrency(Currency currency) {
        try {
            return currencyRepo.save(currency);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Currency> findAllCurrencys(String entityId, Character flag) {
        try {
            return currencyRepo.findAllByEntityIdAndDeletedFlagOrderByIdDesc(entityId, flag);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Currency findById(Long id){
        try{
            return currencyRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Currency updateCurrency(Currency currency) {
        try {
            return currencyRepo.save(currency);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteCurrency(Long id) {
        try {
            currencyRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public Currency findByCurrencyCode(String currencyCode) {
        try{
            return currencyRepo.findByCurrencyCode(currencyCode).orElseThrow(()-> new DataNotFoundException("Data " + currencyCode +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
}

