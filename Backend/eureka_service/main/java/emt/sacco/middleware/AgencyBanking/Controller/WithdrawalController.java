//package emt.sacco.middleware.AgencyBanking.Controller;
//
//import emt.sacco.middleware.AgencyBanking.Dtos.Withdrawal.AgencyRequest;
//import emt.sacco.middleware.AgencyBanking.Dtos.Withdrawal.WithdrawalAgencyReq;
//import emt.sacco.middleware.AgencyBanking.Service.WithdrawalService;
//import emt.sacco.middleware.Utils.EntityResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//
//@RestController
//@RequestMapping("agency/banking")
//@Slf4j
//
//public class WithdrawalController {
//    private final WithdrawalService withdrawalService;
//
//    public WithdrawalController (WithdrawalService withdrawalService) {
//        this.withdrawalService = withdrawalService;
//    }
//    @PostMapping(path = "cardValidation")
//    public ResponseEntity<EntityResponse<String>> cardValidation(@RequestBody AgencyRequest agencyRequest) {
//        try {
//            String pin = agencyRequest.getPin();
//            String cardNumber = agencyRequest.getCardNumber();
//            EntityResponse<String> cardValid = withdrawalService.cardValidation(cardNumber, pin);
//            return ResponseEntity.ok(cardValid);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Handle the exception gracefully and return an error response
//            EntityResponse<String> errorResponse = new EntityResponse<>();
//            errorResponse.setMessage("Error during agency onboarding");
//            errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//    @PostMapping(path = "/agency-Withdrawal", produces = "application/json")
//    public ResponseEntity<?> agencyWithdrawal (@RequestBody WithdrawalAgencyReq withdrawalAgencyReq, HttpServletRequest request) {
//        log.debug("Received ATM withdrawal request: {}", withdrawalAgencyReq);
//        if (withdrawalAgencyReq == null) {
//            EntityResponse<String> acknowledgeResponse = new EntityResponse<>();
//            acknowledgeResponse.setMessage("No data received");
//            acknowledgeResponse.setEntity("");
//            acknowledgeResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
//            return ResponseEntity.badRequest().body(acknowledgeResponse);
//        }
//        try {
//            String clientIpAddress = request.getRemoteAddr();
//            withdrawalAgencyReq.setIp(clientIpAddress);
//            String amount = withdrawalAgencyReq.getAmount();
//            String acid = withdrawalAgencyReq.getAcid();
//            EntityResponse<String> responses = withdrawalService.withdrawalService(acid, amount, request);
//            int customerStatusCode = responses.getStatusCode();
//            if (customerStatusCode == HttpStatus.NOT_ACCEPTABLE.value()) {
//                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
//                        .body(new EntityResponse<>("You have Insufficient Balance, Kindly Check Balance and Try Again", "", HttpStatus.NOT_ACCEPTABLE.value()));
//            } else if (customerStatusCode == HttpStatus.NOT_FOUND.value()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new EntityResponse<>("The amount entered must include the transaction charge", acid, HttpStatus.NOT_FOUND.value()));
//            } else if (customerStatusCode == HttpStatus.OK.value()) {

//                return ResponseEntity.ok(new EntityResponse<>("Withdrawal was successful! Thank you for using our services.", "", HttpStatus.OK.value()));
//            } else if (customerStatusCode == HttpStatus.UNAUTHORIZED.value()) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(new EntityResponse<>("ACCESS DENIED!! UNAUTHORIZED ACCESS", "", HttpStatus.UNAUTHORIZED.value()));
//            } else {
//                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
//                        .body(new EntityResponse<>("Request Time Out!! Try after sometime", "", HttpStatus.REQUEST_TIMEOUT.value()));
//            }
//        } catch (Exception e) {
//            log.error("Error processing ATM withdrawal request: " + e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new EntityResponse<>("Error processing the request", "", HttpStatus.INTERNAL_SERVER_ERROR.value()));
//        }
//    }
//
//    @PostMapping(
//            value = "/agency-withdrawal-ISO",
//            consumes = {MediaType.APPLICATION_JSON_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE}
//    )
//    public ResponseEntity<?> agencyWithdrawalIso8583 (@RequestBody String iso8583Request) throws Exception {
//        System.out.println("ISO 8583 Request: " + iso8583Request);
//        return ResponseEntity.ok(withdrawalService.withdrawalServiceiSO(iso8583Request));
//    }
//}
