package com.emtechhouse.System.CustomerType;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerTypeService {
    public final CustomerTypeRepo customerTypeRepo;

    public CustomerTypeService(CustomerTypeRepo customerTypeRepo) {
        this.customerTypeRepo = customerTypeRepo;
    }


    public CustomerType addCustomerType(CustomerType customerType) {
        try {
            return customerTypeRepo.save(customerType);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<CustomerType> findAllCustomerTypes() {
        try {
            return customerTypeRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public CustomerType findById(Long id){
        try{
            return customerTypeRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public CustomerType findcustomer_type(String customer_type) {
        try{
            return customerTypeRepo.findByCustomerType(customer_type).orElseThrow(()-> new DataNotFoundException("Data " + customer_type +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public CustomerType updateCustomerType(CustomerType customerType) {
        try {
            return customerTypeRepo.save(customerType);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteCustomerType(Long id) {
        try {
            customerTypeRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }


}
