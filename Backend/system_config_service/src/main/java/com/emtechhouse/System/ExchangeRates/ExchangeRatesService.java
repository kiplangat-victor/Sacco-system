package com.emtechhouse.System.ExchangeRates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeRatesService {

    @Autowired
    private BaseCurrencyRepo baseRepo;
    @Autowired
    private ExchangeRatesRepo exchangeRatesRepo;

    public void setbaseCurrency(BaseCurency baseCurency){
        baseRepo.save(baseCurency);
    }
    public List<BaseCurency>  getCurrency(){
      return  baseRepo.findAll();
    }
    public void  updateCurrency(BaseCurency baseCurency){
        baseRepo.save(baseCurency);
    }

    public void addexchangerate(ExchangeRate exchangeRate){
        exchangeRatesRepo.save(exchangeRate);
    }
    public List<ExchangeRate> getexchangerate(){
       return exchangeRatesRepo.findAll();
    }
    public void updateExchangeRate(ExchangeRate exchangeRate){
        exchangeRatesRepo.save(exchangeRate);
    }

}
