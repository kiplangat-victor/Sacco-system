package com.emtechhouse.accounts.Notification;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureService;
import com.emtechhouse.accounts.NotificationComponent.NotificationService;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ServiceNotification {

    @Autowired
    NotifiService notifiService;

    public EntityResponse smsCharges(String acid){
        try {
            System.out.println("In MID service to collect sms charges");
            EntityResponse res= new EntityResponse<>();

            notifiService.feeCharges(acid);


        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
        return null;
    }


}
