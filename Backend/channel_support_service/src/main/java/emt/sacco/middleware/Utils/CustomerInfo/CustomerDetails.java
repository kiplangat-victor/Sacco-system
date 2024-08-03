package emt.sacco.middleware.Utils.CustomerInfo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
public class CustomerDetails {
    String phoneNumber;
    String emailAddress;
    String customerName;
    String customerCode;
    String CustomerUniqueId;
    String acid; // New property for "acid" from CRM response
    String accountName; // New property for "accountName" from CRM response

    public void setName(String customerName) {
        this.customerName = customerName;
    }

    public void setEmail(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // New setter methods for "acid" and "accountName"
    public void setAcid(String acid) {
        this.acid = acid;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
