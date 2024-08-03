package com.emtechhouse.System.ChequeCharge;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChequeChargeService {
    public final ChequeChargeRepo customerTypeRepo;

    public ChequeChargeService(ChequeChargeRepo customerTypeRepo) {
        this.customerTypeRepo = customerTypeRepo;
    }


    public ChequeCharge addChequeCharge(ChequeCharge customerType) {
        try {
            return customerTypeRepo.save(customerType);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<ChequeCharge> findAllChequeCharges() {
        try {
            return customerTypeRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public ChequeCharge findById(Long id){
        try{
            return customerTypeRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public ChequeCharge findsalary_charge(String salary_charge) {
        try{
            return customerTypeRepo.findByChargeCode(salary_charge).orElseThrow(()-> new DataNotFoundException("Data " + salary_charge +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public ChequeCharge updateChequeCharge(ChequeCharge customerType) {
        try {
            return customerTypeRepo.save(customerType);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteChequeCharge(Long id) {
        try {
            customerTypeRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

}