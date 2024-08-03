package com.emtechhouse.CustomerService.RetailMember;

import com.emtechhouse.Utils.DataNotFoundException;
import com.emtechhouse.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RetailcustomerService {
    private final RetailcustomerRepo retailcustomerRepo;

    public RetailcustomerService(RetailcustomerRepo retailcustomerRepo) {
        this.retailcustomerRepo = retailcustomerRepo;
    }

    public Retailcustomer addRetailcustomer(Retailcustomer retailcustomer) {
        try {
            return retailcustomerRepo.save(retailcustomer);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Retailcustomer> findAllRetailcustomers() {
        try {
            return retailcustomerRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Retailcustomer findById(Long id){
        try{
            return retailcustomerRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Retailcustomer updateRetailcustomer(Retailcustomer retailcustomer) {
        try {
            return retailcustomerRepo.save(retailcustomer);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteRetailcustomer(Long id) {
        try {
            retailcustomerRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public EntityResponse getCustomerEmail(String customerCode){
        try {
            EntityResponse res =new EntityResponse<>();
            Optional<RetailcustomerRepo.CustomerEmail> mail= retailcustomerRepo.getCustomerEmail(customerCode);
            if(mail.isPresent()){
                RetailcustomerRepo.CustomerEmail m= mail.get();
                res.setStatusCode(HttpStatus.FOUND.value());
                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                res.setEntity(m);
            }else {
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }
            return res;
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getTotalRetailCustomers(){
        try {
            EntityResponse response =new EntityResponse<>();
            Integer totalRetailCustomers = retailcustomerRepo.countAllRetailCustomers();
            if (totalRetailCustomers > 0) {
                response.setMessage("Total Unverified Retail Customers is " + totalRetailCustomers);
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(totalRetailCustomers);
            } else {
                response.setMessage("Total Unverified Retail Customers is " + totalRetailCustomers);
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("");
            }
            return response;
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
