package com.emtechhouse.accounts.Notification;

import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionProcessing;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotifiService {

    @Autowired
    ServiceNotification serviceNotification;

    @Autowired
    TransactionProcessing transactionProcessing;


    public EntityResponse feeCharges(String acid){
        try {
            System.out.println("In CENTER service to collect sms charges");
            EntityResponse res= new EntityResponse<>();

            transactionProcessing.feeCharges(acid);

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
        return null;
    }
}
