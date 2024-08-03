package com.emtechhouse.System.PayablesAndReceivables;

import com.emtechhouse.System.Utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class PayablesandreceivablesService {
//    private final
    private final PayablesandreceivablesRepo payablesandreceivablesRepo;
    public PayablesandreceivablesService(PayablesandreceivablesRepo payablesandreceivablesRepo) {
        this.payablesandreceivablesRepo = payablesandreceivablesRepo;
    }
    public Payablesandreceivables addPayablesandreceivables(Payablesandreceivables payablesandreceivables) {
        try {
            return payablesandreceivablesRepo.save(payablesandreceivables);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public List<Payablesandreceivables> findAllPayablesandreceivabless() {
        try {
            return payablesandreceivablesRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Payablesandreceivables findById(Long id){
        try{
            return payablesandreceivablesRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Payablesandreceivables updatePayablesandreceivables(Payablesandreceivables payablesandreceivables) {
        try {
            return payablesandreceivablesRepo.save(payablesandreceivables);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deletePayablesandreceivables(Long id) {
        try {
            payablesandreceivablesRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}
