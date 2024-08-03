package emt.sacco.middleware.ATM.Controller;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import emt.sacco.middleware.ATM.Dto.AccountCustomerBalanceResponse;
import emt.sacco.middleware.ATM.Dto.AtmGlResponse;
import emt.sacco.middleware.ATM.Dto.AtmRequest;
import emt.sacco.middleware.ATM.Dto.TranRequest;
import emt.sacco.middleware.ATM.Model.AtmInventory;
import emt.sacco.middleware.ATM.Model.AtmPayload;
import emt.sacco.middleware.ATM.Reports.ReportService;
import emt.sacco.middleware.ATM.ResetPin.CardNotRecognizedException;
import emt.sacco.middleware.ATM.ResetPin.PreviousPinIncorrectException;
import emt.sacco.middleware.ATM.Service.*;
import emt.sacco.middleware.Utils.Config.UserRequestContext;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.EntityResponse;
import emt.sacco.middleware.Utils.Responses.MessageResponse;
import emt.sacco.middleware.Utils.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import emt.sacco.middleware.Utils.RequestChecker;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/atm")
@Slf4j
public class AtmController {

    @Autowired
    private AtmOnboardingService atmOnboardingService;

    @Autowired
    private AtmInventoryService service;

    @Autowired
    private AtmWithdrawalService atmWithdrawalService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ATMCustomerBalanceService atmBalanceService;

    @Autowired
    private emt.sacco.middleware.ATM.ResetPin.ChangePinService ChangePinService;

    @Autowired
    private RestService restService;
    private final RequestChecker requestChecker;

    public AtmController(RequestChecker requestChecker) {
        this.requestChecker = requestChecker;
    }


    // TODO Onboarding ATM Machine

    @PostMapping("/onboard/add")
    public ResponseEntity<?>  addAtm(@RequestBody AtmPayload atmPayload) {
        String entityId = EntityRequestContext.getCurrentEntityId();
        if (entityId != null) {
            try {
                return atmOnboardingService.addAtm(atmPayload, entityId);
            } catch (Exception e) {
                log.error("Error occurred while adding atms: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse<>(null, "Error occurred while adding an atm machine", 500));
            }
        } else {
            log.error("Invalid Payload: {}", entityId);
            return ResponseEntity.badRequest().body(new MessageResponse<>(null, "Invalid addAtmsPayload", 400));
        }
    }





    @GetMapping(path = "/onboard/getAtms", produces = "application/json")
    public ResponseEntity<?> getAtms() {
        EntityResponse res = new EntityResponse();
        try {
            String entityId = EntityRequestContext.getCurrentEntityId();
            return atmOnboardingService.getAtms(entityId);
        } catch (Exception e) {
            res.setMessage("An error occurred while processing the request.");
            res.setEntity(null);
            res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }



    @DeleteMapping("/onboard/delete/{Id}")
    public ResponseEntity<MessageResponse<JsonNode>> deleteAtms(@PathVariable Long Id) {
        try {
            MessageResponse<JsonNode> response = atmOnboardingService.deleteAtmMachineById(Id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @PutMapping(path = "/onboard/modify", produces = "application/json")
    public ResponseEntity<?> modifyAtm(@RequestBody AtmPayload modifyAtmPayload) {
        log.info("Received ModifyAtmPayload: {}", modifyAtmPayload);

        if (modifyAtmPayload != null) {
            try {

                return ResponseEntity.ok(atmOnboardingService.modifyAtm(modifyAtmPayload));
            } catch (Exception e) {
                String errorMessage = "400: " + e.getMessage();
                log.error("Error occurred while modifying ATM:", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"" + errorMessage + "\"}");
            }
        } else {
            // Handle the case where modifyAtmPayload is null
            log.error("Received null ModifyAtmPayload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"ModifyAtmPayload cannot be null\"}");
        }
    }


    @PutMapping(path = "/onboard/verify/{id}", produces = "application/json")
    public ResponseEntity<MessageResponse<JsonNode>> verifyAtm(@PathVariable Long id) {
        String entityId = EntityRequestContext.getCurrentEntityId();
        log.info("Verifying ATM details for ID: {} and Entity ID: {}", id, entityId);
        try {
            MessageResponse<JsonNode> response = atmOnboardingService.verifyAtm(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
                String errorMessage = "400: " + e.getMessage();
                log.error("Error occurred while verifying the ATM machine:", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse<>(null, "Error occurred while retrieving atms", 500));
        }
    }

    // TODO ATM INVENTORY

    @PostMapping( "/inventory/card/accept")
    public String acceptRequest(@RequestBody AtmInventory atmInventory) {
        return service.acceptRequest(atmInventory);
    }

    @PostMapping("/inventory/card/reject")
    public String rejectRequest(@RequestBody AtmInventory atmInventory) {
        String card = service.rejectRequest(atmInventory);
//        atmInventory.setRequestStatus("Cards application Rejected");
        return card;
    }

    @PostMapping("/inventory/card/disburse")
    public String disburseCards(@RequestBody AtmInventory atmInventory) {
        String card = service.disburseCards(atmInventory);
//        atmInventory.setRequestStatus("Cards disbursed successfully");
        return card;
    }


    //TODO ATM TRANSACTIONS SIMULATION

    @PostMapping(path = "/cardValidation")
    public ResponseEntity<EntityResponse<String>> cardValidation(@RequestBody AtmRequest atmRequest) {
        try {
            String pin = atmRequest.getPin();
            String cardNumber = atmRequest.getCardNumber();
            EntityResponse<String> cardValid = atmWithdrawalService.cardValidation(cardNumber, pin);
            return ResponseEntity.ok(cardValid);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception gracefully and return an error response
            EntityResponse<String> errorResponse = new EntityResponse<>();
            errorResponse.setMessage("Error during ATM onboarding");
            errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping(path = "/atmWithdrawal", produces = "application/json")
//    public ResponseEntity<?> atmWithdrawal(@RequestBody TranRequest tranRequest, HttpServletRequest request) {
    public ResponseEntity<Object> processRequest(@RequestBody String requests, HttpServletRequest request) throws Exception {

        ResponseEntity<TranRequest> responseEntity = requestChecker.processRequest(requests);
        TranRequest responseBody = responseEntity.getBody();

        String acid = responseBody.getAcid();
        String amount = responseBody.getAmount();

        // Now you can use the extracted values as needed
        System.out.println("Acid: " + acid);
        System.out.println("Amount: " + amount);
        log.debug("Received ATM withdrawal request: {}", requests);
        if (requests == null) {
            EntityResponse<String> acknowledgeResponse = new EntityResponse<>();
            acknowledgeResponse.setMessage("No data received");
            acknowledgeResponse.setEntity("");
            acknowledgeResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(acknowledgeResponse);
        }
        try {
            String clientIpAddress = request.getRemoteAddr();
//            tranRequest.setIp(clientIpAddress);
//            String amount = tranRequest.getAmount();
//            String acid = tranRequest.getAcid();
            EntityResponse<String> responses = atmWithdrawalService.withdrawalService(acid, amount, request);
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
//                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
//                        .body(new EntityResponse<>("Request Time Out!! Try after sometime", "", HttpStatus.REQUEST_TIMEOUT.value()));
                //                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
////                        .body(new EntityResponse<>("Request Time Out!! Try after sometime", "", HttpStatus.REQUEST_TIMEOUT.value()));
//                        .body(new EntityResponse<>(responses.getEntity()));
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body(responses.getEntity());
                       // .body(new EntityResponse<>("Request Time Out!! Try after sometime", "", HttpStatus.REQUEST_TIMEOUT.value()));
            }
        } catch (Exception e) {
            log.error("Error processing ATM withdrawal request: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EntityResponse<>("Error processing the request", "", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "/AtmGls", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<List<AtmGlResponse>>> getAtmGl()
    {
        String entityId = EntityRequestContext.getCurrentEntityId();
        String userName = UserRequestContext.getCurrentUser();
        EntityResponse<List<AtmGlResponse>> response = new EntityResponse<>();
        try {
            String accountStatementData = atmWithdrawalService.findAtmGl(userName, entityId);
            log.info("Actual JSON Response: {}", accountStatementData);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                JsonNode jsonNode = objectMapper.readTree(accountStatementData);
                String message = jsonNode.path("message").asText();
                JsonNode entityNode = jsonNode.path("entity");
                if ("Records Found".equals(message)) {
                    List<AtmGlResponse> atmGlResponses = convertToAtmGlResponses(entityNode);
                    response.setMessage("Records Found");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(atmGlResponses);
                    String atmAccount = extractAtmAccountInfo(entityNode);
                    // Now you can pass the ATM account information to the atmOnboarding method
                    return ResponseEntity.ok(response);
                } else {
                    // Handle the case when records are not found
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new EntityResponse<>());
                }
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new EntityResponse<>());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new EntityResponse<>());
            }
        } finally {
        }
    }
    private List<AtmGlResponse> convertToAtmGlResponses(JsonNode entityNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<AtmGlResponse> result = new ArrayList<>();
        if (entityNode.isArray()) {
            for (JsonNode accountNode : entityNode) {
                if (accountNode.has("accountName") && accountNode.path("accountName").isTextual()) {
                    String accountName = accountNode.path("accountName").asText();
                    if (accountName.toLowerCase().contains("atm")) {
                        AtmGlResponse atmGlResponse = new AtmGlResponse();
                        atmGlResponse.setAcid(accountNode.path("acid").asText());
                        atmGlResponse.setAccountName(accountName);
                        atmGlResponse.setOpeningDate(accountNode.path("openingDate").asText());
                        atmGlResponse.setSolCode(accountNode.path("solCode").asText());
                        atmGlResponse.setVerifiedFlag(accountNode.path("verifiedFlag").asText());
                        result.add(atmGlResponse);
                    }
                }
            }
        } else {
            if (entityNode.has("accountName") && entityNode.path("accountName").isTextual()) {
                String accountName = entityNode.path("accountName").asText();
                if (accountName.toLowerCase().contains("atm")) {
                    AtmGlResponse atmGlResponse = new AtmGlResponse();
                    atmGlResponse.setAcid(entityNode.path("acid").asText());
                    atmGlResponse.setAccountName(accountName);
                    result.add(atmGlResponse);
                }
            }
        }
        return result;
    }
    private String extractAtmAccountInfo(JsonNode entityNode) {
        // Replace "atm_account" with the actual key in your JSON response
        return entityNode.path("acid").asText();
    }

    @PostMapping(path="/customerbalance", produces = "application/json" )
    public ResponseEntity<AccountCustomerBalanceResponse> getCustomerBalance(@RequestParam String acid) {
        log.info("Received AccountCustomerBalanceRequest: {}", acid);

        if (acid != null && acid != null && !acid.isEmpty()) {
            try {
                // Call the service to retrieve customer account balance
                AccountCustomerBalanceResponse response = atmBalanceService.getAccountBalance(acid);
                log.info("Returned AccountCustomerBalanceResponse: {}", response);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("Error occurred while retrieving ATM balance: {}", e.getMessage());
                // Here, you might want to return a more informative message or handle specific exceptions
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AccountCustomerBalanceResponse());
            }
        } else {
            log.error("Invalid GetAtmBalanceRequest: {}", acid);
            return ResponseEntity.badRequest().body(new AccountCustomerBalanceResponse());
        }
    }



    // TODO LOGS

    @GetMapping("/errors/{format}")
    public String generateReports(@PathVariable String format) {
        return reportService.exportErrorReports(format);

    }


    //TODO ATM PIN RESET
    @PostMapping("/pin/reset")
    public ResponseEntity<String> resetPin(
//            @RequestParam String entityId,
            @RequestParam String cardNumber,
            @RequestParam String previousPin,
            @RequestParam String newPin,
            @RequestParam String confirmNewPin
    ) {
        // Check if new PIN and confirm new PIN match
        if (!newPin.equals(confirmNewPin)) {
            return ResponseEntity.badRequest().body("New PIN and confirm new PIN do not match.");
        }

        // Check if new PIN is different from the previous PIN
        if (newPin.equals(previousPin)) {
            return ResponseEntity.badRequest().body("New PIN should be different from the previous PIN.");
        }

        // Validate new PIN format
        if (!isValidPinFormat(newPin)) {
            return ResponseEntity.badRequest().body("New PIN should be 4-digit numeric.");
        }

        try {
            // Delegate the reset pin logic to the service
            String serviceResponse = ChangePinService.resetPin(cardNumber, previousPin, newPin, confirmNewPin);
            return ResponseEntity.ok(serviceResponse);
        } catch (PreviousPinIncorrectException e) {
            return ResponseEntity.badRequest().body("Previous PIN is incorrect.");
        } catch (CardNotRecognizedException e) {
            return ResponseEntity.badRequest().body("ATM card not recognized.");
        } catch (Exception e) {
            log.error("Error during PIN reset", e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }


    private boolean isValidPinFormat(String pin) {
        return Pattern.matches("\\d{4}", pin);
    }
//    @PostMapping( "/AtmReversals")
//    public String atmReversals() {
//        String userName=UserRequestContext.getCurrentUser();
//        String entity_id=EntityRequestContext.getCurrentEntityId();
//        return atmWithdrawalService.atmReversals(userName,entity_id);
//    }
    @GetMapping("/AtmReversals")
    public ResponseEntity<EntityResponse<List<Map<String, Object>>>> getAtmReversals() {
        String userName = UserRequestContext.getCurrentUser();
        String entity_id = EntityRequestContext.getCurrentEntityId();
        try {
            List<Object[]> data = atmWithdrawalService.findAtmReversal(userName, entity_id);

            List<Map<String, Object>> mappedData = convertToObjectList(data);

            EntityResponse<List<Map<String, Object>>> response = new EntityResponse<>();
            response.setMessage("Success");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(mappedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return null;
            //            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new EntityResponse<>("Error occurred while fetching ATM reversals data", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    // Helper method to convert List<Object[]> to List<Map<String, Object>>
    private List<Map<String, Object>> convertToObjectList(List<Object[]> data) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Object[] array : data) {
            Map<String, Object> map = new HashMap<>();
            map.put("atmName", array[0]);
            map.put("terminalId", array[1]);
            map.put("location", array[2]);
            map.put("postedTime", array[3]);
            map.put("responseStatus", array[4]);
            map.put("acid", array[5]);
            map.put("totalDebitAmount", array[6]);
            resultList.add(map);
        }
        return resultList;
    }

    private List<Map<String, Object>> convertErrorsToList(List<Object[]> data) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Object[] array : data) {
            Map<String, Object> map = new HashMap<>();
            map.put("atmName", array[0]);

            map.put("location", array[1]);
            map.put("postedTime", array[2]);
            map.put("responseStatus", array[3]);

            resultList.add(map);
        }
        return resultList;
    }

    @GetMapping("/ErrorLogs")
    public ResponseEntity<EntityResponse<List<Map<String, Object>>>> getErrorLogs() {
        String userName = UserRequestContext.getCurrentUser();
        String entity_id = EntityRequestContext.getCurrentEntityId();
        try {
            List<Object[]> data = atmWithdrawalService.findErrorLogs(userName, entity_id);

            List<Map<String, Object>> mappedData = convertErrorsToList(data);

            EntityResponse<List<Map<String, Object>>> response = new EntityResponse<>();
            response.setMessage("Success");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(mappedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return null;
            //            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new EntityResponse<>("Error occurred while fetching ATM reversals data", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }



}
