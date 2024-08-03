package emt.sacco.middleware.Iso8583Proxy.BalanceEnquiry;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/account")
public class BalanceEnquiryController {

    @Autowired
    BalanceEnquiryService balanceEnquiryService;



    @PostMapping(path = "/check-balance-json", produces = "application/json")
    public ResponseEntity<AccountBalanceRes> checkBalance(@RequestBody AccountBalanceReq accountBalanceReq) throws JsonProcessingException {
       log.info("inside Controller");
        return ResponseEntity.ok(balanceEnquiryService.checkBalance(accountBalanceReq));
    }


    @PostMapping(
            value = "/check-balance-ISO",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> getBalance(@RequestBody String iso8583Request) throws Exception {
        System.out.println("ISO 8583 Request: " + iso8583Request);
        return ResponseEntity.ok(balanceEnquiryService.getBalance(iso8583Request));
    }

}

