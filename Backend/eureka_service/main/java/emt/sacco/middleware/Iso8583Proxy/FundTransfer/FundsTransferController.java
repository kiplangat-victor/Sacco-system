package emt.sacco.middleware.Iso8583Proxy.FundTransfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class FundsTransferController {

    @Autowired
    FundsTransferService fundsTransferService;


    @PostMapping(path = "/funds-transfer-json", produces = "application/json")
    public ResponseEntity<FundsTransferRes> moneyTransfer(@RequestBody FundsTransferReq fundsTransferReq) throws Exception {
        System.out.println("inside funds-transfer-json Controller");
        return ResponseEntity.ok(fundsTransferService.moneyTransfer(fundsTransferReq));

    }


    @PostMapping(
            value = "/funds-transfer-ISO",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> getBalance(@RequestBody String iso8583Request) throws Exception {
        System.out.println("ISO 8583 Request: " + iso8583Request);
        return ResponseEntity.ok(fundsTransferService.fundsTransfer(iso8583Request));
    }

}