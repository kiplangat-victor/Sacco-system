package emt.sacco.middleware.AgencyBanking.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import emt.sacco.middleware.AgencyBanking.Dtos.CashDepositReq;
import emt.sacco.middleware.AgencyBanking.Dtos.Withdrawal.AgencyRequest;
import emt.sacco.middleware.AgencyBanking.Dtos.Withdrawal.AgencyTranRequest;
import emt.sacco.middleware.AgencyBanking.Service.CashDepositService;
import emt.sacco.middleware.AgencyBanking.Service.WithdrawalService;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/agency")
@Slf4j
public class AgencyController {

    @Autowired
    CashDepositService cashDepositService;

    @Autowired
    private WithdrawalService withdrawalService;


    // TODO AGENCY TRANSACTION SIMULATION

    @PostMapping(
            value = "/deposit/iso",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> cashDeposit(@RequestBody String iso8583Request) throws Exception {
        System.out.println("ISO 8583 Request: " + iso8583Request);
        return ResponseEntity.ok(cashDepositService.cashDeposit(iso8583Request));
    }

    @PostMapping("/deposit/json")
    public EntityResponse<String> deposit(@RequestBody CashDepositReq depositReq) throws JsonProcessingException {
//        String entityId = "123456";
        String entityId= EntityRequestContext.getCurrentEntityId();
        return cashDepositService.cashdeposit(depositReq, entityId);
    }

    @PostMapping(path = "card/validation")
    public ResponseEntity<EntityResponse<String>> cardValidation(@RequestBody AgencyRequest agencyRequest) {
        try {
            String pin = agencyRequest.getPin();
            String cardNumber = agencyRequest.getCardNumber();
            EntityResponse<String> cardValid = withdrawalService.cardValidation(cardNumber, pin);
            return ResponseEntity.ok(cardValid);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception gracefully and return an error response
            EntityResponse<String> errorResponse = new EntityResponse<>();
            errorResponse.setMessage("Error during agency onboarding");
            errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping(path = "/withdrawal/json", produces = "application/json")
    public ResponseEntity<?> agencyWithdrawal (@RequestBody AgencyTranRequest withdrawalAgencyReq, HttpServletRequest request) {
        log.debug("Received ATM withdrawal request: {}", withdrawalAgencyReq);
        if (withdrawalAgencyReq == null) {
            EntityResponse<String> acknowledgeResponse = new EntityResponse<>();
            acknowledgeResponse.setMessage("No data received");
            acknowledgeResponse.setEntity("");
            acknowledgeResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(acknowledgeResponse);
        }
        try {
            String clientIpAddress = request.getRemoteAddr();
            withdrawalAgencyReq.setIp(clientIpAddress);
            String amount = withdrawalAgencyReq.getAmount();
            String acid = withdrawalAgencyReq.getAcid();
            EntityResponse<String> responses = withdrawalService.withdrawalService(acid, amount, request);
            int customerStatusCode = responses.getStatusCode();
            if (customerStatusCode == HttpStatus.NOT_ACCEPTABLE.value()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new EntityResponse<>("You have Insufficient Balance, Kindly Check Balance and Try Again", "", HttpStatus.NOT_ACCEPTABLE.value()));
            } else if (customerStatusCode == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new EntityResponse<>("The amount entered must include the transaction charge", acid, HttpStatus.NOT_FOUND.value()));
            } else if (customerStatusCode == HttpStatus.OK.value()) {
                return ResponseEntity.ok(new EntityResponse<>("Withdrawal was successful! Thank you for using our services.", "", HttpStatus.OK.value()));
            } else if (customerStatusCode == HttpStatus.UNAUTHORIZED.value()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new EntityResponse<>("ACCESS DENIED!! UNAUTHORIZED ACCESS", "", HttpStatus.UNAUTHORIZED.value()));
            } else {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body(new EntityResponse<>("Request Time Out!! Try after sometime", "", HttpStatus.REQUEST_TIMEOUT.value()));
            }
        } catch (Exception e) {
            log.error("Error processing ATM withdrawal request: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EntityResponse<>("Error processing the request", "", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping(
            value = "/withdrawal/iso",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> agencyWithdrawalIso8583 (@RequestBody String iso8583Request) throws Exception {
        System.out.println("ISO 8583 Request: " + iso8583Request);
        return ResponseEntity.ok(withdrawalService.withdrawalServiceiSO(iso8583Request));
    }






}
