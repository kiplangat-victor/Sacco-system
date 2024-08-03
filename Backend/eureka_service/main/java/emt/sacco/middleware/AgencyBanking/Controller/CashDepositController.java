package emt.sacco.middleware.AgencyBanking.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import emt.sacco.middleware.AgencyBanking.Dtos.CashDepositReq;
import emt.sacco.middleware.AgencyBanking.Service.CashDepositService;
import emt.sacco.middleware.Utils.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agencybanking")
public class CashDepositController {
    @Autowired
    CashDepositService cashDepositService;

    @PostMapping(
            value = "/CASH_DEPOSIT-ISO",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> cashDeposit(@RequestBody String iso8583Request) throws Exception {
        System.out.println("ISO 8583 Request: " + iso8583Request);
        return ResponseEntity.ok(cashDepositService.cashDeposit(iso8583Request));
    }

    @PutMapping("/deposit")
    public EntityResponse<String> deposit(@RequestBody CashDepositReq depositReq, String entityId) throws JsonProcessingException {
//        String entityId = "123456";
        return cashDepositService.cashdeposit(depositReq, entityId);
    }


}
