package emt.sacco.middleware.Mobile.Controller;
import emt.sacco.middleware.Mobile.Models.AccountReq;
import emt.sacco.middleware.Mobile.Models.MobileTransfer;
import emt.sacco.middleware.Mobile.Models.ViewAccountsPayload;
import emt.sacco.middleware.Mobile.Service.MobileService;
import emt.sacco.middleware.Utils.EntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/mobile")
@Slf4j
@RequiredArgsConstructor
public class MobileController {

    @Autowired
    private final MobileService mobileService;

    //private final AccountStatementService accountStatementService;

    @PostMapping("/transfer")
    public String mobileTransfer(@RequestBody MobileTransfer mobileTransfer) {

        EntityResponse response = new EntityResponse();
        //Gson gson = new Gson();
        try {
            String transfer = mobileService.processMobileTransfer(mobileTransfer);
            return transfer;

        } catch (Exception e) {
            return "Successful";
        }
    }

    @GetMapping(value = "/accountStatement", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getAccountStatement(
            @RequestParam String acid,
            @RequestParam String fromdate,
            @RequestParam String todate) {
        try {


            byte[] accountStatementData = mobileService.getAccountStatement(acid, fromdate, todate).getBody();
            assert accountStatementData != null;
            log.info("Received PDF document from CONTROLLER. Size: {}", accountStatementData.length);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "account-statement.pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(accountStatementData);
        } catch (Exception e) {
            log.error("Error occurred while getting account statement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while getting account statement.".getBytes());
        }

    }

    @GetMapping("/ministatement")
    public ResponseEntity<?> getAccountMinistatement(@RequestParam String acid,
                                                     @RequestParam String maxCount,
                                                     @RequestParam String entityId,
                                                     @RequestParam String phoneNumber
    ) {
        try {
            Object accountMinistatementData = mobileService.getAccountMinistatements(acid, maxCount, entityId, phoneNumber);
            return ResponseEntity.ok(accountMinistatementData);

            // return ResponseEntity.ok(accountStatementService.getAccountStatement(request));
        } catch (Exception e) {
            String res = ("Error occurred while retrieving account Ministatement: ");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
    }
//AccountCreation controller for restService connection
//    @PostMapping("create/account")
//    public ResponseEntity<?> createAccount(@RequestBody AccountReq accountReq) {
//        try {
//            return ResponseEntity.ok().body(mobileService.AccountCreationService.createAccount(accountReq));
//        } catch (Exception e) {
//            String res = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>\n" +
//                    "    <soapenv:Header/>\n" +
//                    "    <soapenv:Body>\n" +
//                    "        <api:Response> 400" + ":" + e.getMessage() + "  </api:Response>\n" +
//                    "    </soapenv:Body>\n" +
//                    "</soapenv:Envelope>";
//            return ResponseEntity.badRequest().body(res);
//        }
//    }


    @PostMapping(path = "/viewAccounts", produces = "application/json")
    public ResponseEntity<?> getAccounts(@RequestBody ViewAccountsPayload viewAccountsPayload) {
        log.info("Received ViewAccountPayload: {}", viewAccountsPayload);
        if (viewAccountsPayload != null) {
            try {
                String customerCode = viewAccountsPayload.getCustomerCode();
                return ResponseEntity.ok(mobileService.getAccounts(customerCode));
            } catch (Exception e) {
                log.error("Error occurred while retrieving accounts: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while retrieving accounts");
            }
        } else {
            log.error("Invalid ViewAccountsPayload: {}", viewAccountsPayload);
            return ResponseEntity.badRequest().body("Invalid ViewAccountsPayload");
        }
    }


    @PostMapping("create-account")
    public ResponseEntity<?> createAccount(@RequestBody AccountReq accountReq) {
        try {
            // Call the serviceCaller to handle the account creation request
            return ResponseEntity.ok().body(mobileService.createAccount(accountReq));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to create account.");
        }
    }

}


