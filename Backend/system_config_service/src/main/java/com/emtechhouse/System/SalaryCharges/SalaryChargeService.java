package com.emtechhouse.System.SalaryCharges;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SalaryChargeService {
    public final SalaryChargeRepo salaryChargeRepo;

    public SalaryChargeService(SalaryChargeRepo salaryChargeRepo) {
        this.salaryChargeRepo = salaryChargeRepo;
    }


    public SalaryCharge addSalaryCharge(SalaryCharge customerType) {
        try {
            return salaryChargeRepo.save(customerType);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<SalaryCharge> findAllSalaryCharges() {
        try {
            return salaryChargeRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public SalaryCharge findById(Long id){
        try{
            return salaryChargeRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public SalaryCharge findsalary_charge(String salary_charge) {
        try{
            return salaryChargeRepo.findBySalaryChargeCode(salary_charge).orElseThrow(()-> new DataNotFoundException("Data " + salary_charge +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public SalaryCharge updateSalaryCharge(SalaryCharge salaryCharge) {
        try {
            return salaryChargeRepo.save(salaryCharge);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteSalaryCharge(Long id) {
        try {
            salaryChargeRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

}