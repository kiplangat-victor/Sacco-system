package emt.sacco.middleware.ATM.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import emt.sacco.middleware.Atm.DTO.OnboardingRequestDto;
import emt.sacco.middleware.ATM.Dto.AtmDto;
import emt.sacco.middleware.Utils.CommonService.Channel;
import emt.sacco.middleware.Utils.CommonService.PartTran;
import emt.sacco.middleware.Utils.CommonService.TransactionHeader;
import emt.sacco.middleware.Utils.CustomerInfo.Account;
import emt.sacco.middleware.Utils.EntityResponse;
import emt.sacco.middleware.Utils.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import static emt.sacco.middleware.Utils.StkPushAsyncResponse.transID;
@Service
@Slf4j
public class AtmWithdrawalService {
    @Autowired
    private RestService restService;
    @Autowired
    @Qualifier("getObjectMapper")
    private ObjectMapper objectMapper;
    @JsonProperty("TransID")
    public static String transID;
    public EntityResponse<String>cardValidation(@RequestParam String cardNumber, String pin){
        EntityResponse<String> response =new EntityResponse<>();
        try{
            EntityResponse<Account> cardInfo = restService.cardValidation(cardNumber,pin);
            int cardStatusCode = cardInfo.getStatusCode();
            if (cardStatusCode == HttpStatus.OK.value()) {
                String acid = String.valueOf(cardInfo.getEntity());

                log.info("Acid value: " + acid);


                response.setMessage("Successful");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(acid);
            } else if (cardStatusCode == HttpStatus.NOT_FOUND.value()) {
                response.setMessage("ATM card is expired");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("notfound");
            } else if (cardStatusCode == HttpStatus.FORBIDDEN.value()) {
                response.setMessage("Card not verified");
                response.setStatusCode(HttpStatus.FORBIDDEN.value());
                response.setEntity("forbidden");
            } else if (cardStatusCode == HttpStatus.GONE.value()) {
                response.setMessage("ATM card is deleted");
                response.setStatusCode(HttpStatus.GONE.value());
                response.setEntity("gone");
            } else if (cardStatusCode==HttpStatus.UNAUTHORIZED.value()) {
                response.setMessage("PIN INCORRECT!!");
                response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                response.setEntity("");
            } else {
                // Handle other status codes if necessary
                response.setMessage("Invalid card");
               // response.setStatusCode(cardStatusCode);
                response.setEntity("invalid");
            }
            return response;
        }catch (Exception e) {
            log.error("Error WHILE VALIDATING CARD: " + e.getMessage(), e);
            response.setMessage("ERROR OCCUR WHILE PROCESSING YOU REQUEST" + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setEntity("ERROR");
        }
        return response;
    }
    public EntityResponse<String> withdrawalService(String acid, String amount, HttpServletRequest request) {
        EntityResponse<String> response = new EntityResponse<>();
        try {
            EntityResponse<Account> customerInfoResponse = restService.retrieveAccount(acid);
            int customerStatusCode = customerInfoResponse.getStatusCode();
            if (customerStatusCode == HttpStatus.NOT_FOUND.value()) {
                log.info("Customer account Number " + acid + " has NOT been Registered with us. Thank You for using our services.");
                response.setMessage("Customer account Number " + acid + " has NOT been Registered with us.");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("Not Registered");
            } else if (customerStatusCode != HttpStatus.OK.value()) {
                log.error("Error fetching customer info. StatusCode: " + customerStatusCode);
                response.setMessage("Error fetching customer info. StatusCode: " + customerStatusCode);
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setEntity("Error");
            } else {
                String clientIpAddress = request.getLocalAddr();
                log.info("this ip"+clientIpAddress);
                EntityResponse<AtmDto> machineInfo = restService.validateAtmMachine(clientIpAddress);
                if (machineInfo.getStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    log.error("ATM MACHINE NOT CONFIGURED FOR TRANSACTION " + machineInfo.getStatusCode());
                    response.setMessage("UNAUTHORIZED ACCESS: ");
                    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                    response.setEntity("Error");
                }
                else if (machineInfo.getStatusCode() != HttpStatus.OK.value()) {
                    log.error("Error validating ATM machine. StatusCode: " + machineInfo.getStatusCode());
                    response.setMessage("Server error ");
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    response.setEntity("Error");
                }else {
                    AtmDto onboardingRequestDto1 = machineInfo.getEntity();
                    String terminalId2 = onboardingRequestDto1.getTerminalId();
                    String ip2 = onboardingRequestDto1.getIpAddress();
                    String p=request.getRemoteAddr();
                    log.info("this is the remote ip address"+p);
                    String atmAccount2 = onboardingRequestDto1.getAtmAccount();

                    try {
                        if (clientIpAddress.equalsIgnoreCase(ip2)) {
                            log.info("Continue with transaction");
                            // Create a TransactionHeader object
                            TransactionHeader transactionHeader = new TransactionHeader();
                            transactionHeader.setTransactionType("Cash Withdrawal"); // Set the transaction type
                            transactionHeader.setChequeType("");
                            transactionHeader.setStaffCustomerCode("");
                            transactionHeader.setTransactionCode("");
                            transactionHeader.setReversalTransactionCode("");
                            transactionHeader.setCurrency("currency");
                            // transactionHeader.setIpAddress(currentIpAddress);
                            transactionHeader.setEodStatus("N");
                            transactionHeader.setEntityId("entityId");
                            transactionHeader.setRcre(new Date());
                            transactionHeader.setTransactionDate(new Date());
                            transactionHeader.setStatus("status");
                            transactionHeader.setMpesacode("");
                            transactionHeader.setChargeEventId("chargeEventId");
                            transactionHeader.setSalaryuploadCode("");
                            transactionHeader.setTellerAccount("ATMACCOUNT");
                            transactionHeader.setBatchCode("batchCode");
                            transactionHeader.setConductedBy(Channel.ATM);
                            transactionHeader.setMiddleware('Y');
//                            transactionHeader.setConductedBy(terminalId2);
                            ArrayList<PartTran> partTrans = new ArrayList<>();
                            // Create a PartTran object and set its properties
                            PartTran partTran = new PartTran();
                            partTran.setAccountType("SBA");
                            partTran.setAcid(acid);
                            partTran.setPartTranType("Debit");
                            partTran.setPartTranIdentity("normal");
                            partTran.setIsoFlag('M');
                            partTran.setExchangeRate("1.0");
                            partTran.setTransactionParticulars(terminalId2);
                            partTran.setTransactionDate(new Date());
                            partTran.setChargeFee('Y');
                            partTran.setBatchCode("");
                            partTran.setTransactionCode(transactionHeader.getTransactionCode());
                            partTran.setTransactionAmount(Double.parseDouble(amount));
                            partTrans.add(partTran);
                            transactionHeader.setTransactionDate(new Date());
                            transactionHeader.setCurrency("KES");
                            transactionHeader.setTellerAccount(atmAccount2);
                            log.info(terminalId2);
                                transactionHeader.setPartTrans(partTrans);
                            // Convert the object to JSON using the injected Gson instance
                            Gson gson = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                                    .create();
                            String requestJson = gson.toJson(transactionHeader);
                            System.out.println(requestJson);
                            // Check if the requestJson variable is null or empty
                            if (requestJson == null || requestJson.isEmpty()) {
                                // Handle the case where the requestJson is null or empty
                                log.error("Error: Request JSON is null or empty");
                                response.setMessage("Error: Request JSON is null or empty");
                                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                                response.setEntity("Error");
                                return response;
                            }
                            // Call the rest service using the autowired instance
                            String restServiceResponse = restService.enterTransaction1(requestJson, "casc");
                            System.out.println("Completing withdrawal process: " + restServiceResponse);
                            JsonNode customerJsonNode = objectMapper.readTree(restServiceResponse);
                            String sendingCustomerCode = String.valueOf(customerJsonNode.get("statusCode"));

                            int customerStatusCod = Integer.parseInt(sendingCustomerCode);
                            if (customerStatusCod == HttpStatus.NOT_ACCEPTABLE.value()) {
                                response.setMessage(restServiceResponse);
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("Not Acceptable");
                                return response;
                            } else if (customerStatusCod == HttpStatus.NOT_FOUND.value()) {
                                response.setMessage(restServiceResponse);
                                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                                response.setEntity("Not Acceptable");
                                return response;
                            }
                            else {
                                // Check if the response is null or empty
                                if (restServiceResponse == null || restServiceResponse.isEmpty()) {
                                    log.error("Transaction failed: Response is null or empty");
                                    response.setMessage("Transaction failed: Response is null or empty");
                                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                                    response.setEntity("Error");
                                    return response;
                                }
                                // Parse the response as JSON
                                try {
                                    JsonNode responseJsonNode = objectMapper.readTree(restServiceResponse);
                                    String responseStatusCode = String.valueOf(responseJsonNode.get("statusCode"));
                                    int statusCode = Integer.parseInt(responseStatusCode);
                                    if (statusCode != 201) {
                                        log.error("Error: Status code is not 201");
                                        response.setMessage("Error: Status code is not 201");
                                        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                                        response.setEntity("Error");
                                        return response;
                                    }
                                    String trCode = responseJsonNode.get("entity").get("transactionCode").asText();
                                    log.info("Transaction entity code: " + trCode);
//                                    RestService.TransactionResponse postTrRes = this.restService.postTransaction(trCode, transID);
//                                    log.info("Response: " + postTrRes);
//                                    response.setMessage("Success");
//                                    response.setStatusCode(HttpStatus.OK.value());
//                                    response.setEntity("Success");
//                                    return response; // Replace with your success response

                                    RestService.TransactionResponse postTrRes = this.restService.postTransaction(trCode, transID);
                                    log.info("Response: " + postTrRes.getMessage()); // Log the message from the response
                                    if (postTrRes.isSuccess()) {
                                        response.setMessage("Success");
                                        response.setStatusCode(HttpStatus.OK.value());
                                        response.setEntity("Success");
                                    } else {


                                        String reversalResponse = restService.initiateReversal(trCode);
                                        System.out.println("Reversal Initiated successfully " + reversalResponse);
//                                        JsonNode responseJsonNode = objectMapper.readTree(restServiceResponse);
                                         JsonNode reversalJsonNode1 = objectMapper.readTree(reversalResponse);
                                        String trCode1 = reversalJsonNode1.get("entity").get("transactionCode").asText();

                                        String verifyRe = this.restService.verifyReversal(trCode1);
                                       // JsonNode reversalJsonNode = objectMapper.readTree(reversalResponse);
                                        JsonNode reversalJsonNode2 = objectMapper.readTree(verifyRe);
                                        System.out.println("Verification of reversal after initiation " + verifyRe);
                                        String trCode2 = reversalJsonNode2.get("entity").get("transactionCode").asText();

//                                        String reversalPostRe = restService.testPost(trCode2, transID);
//                                        System.out.println("Posting of reversal and updating of balances " + reversalPostRe);
//                                        JsonNode reversalJsonNode3 = objectMapper.readTree(String.valueOf(reversalPostRe));
//                                        String trCode3 = reversalJsonNode3.get("entity").get("transactionCode").asText();

//                                        String reversalPostRe = restService.testPost(trCode2, transID);
//                                        System.out.println("Posting of reversal and updating of balances " + reversalPostRe);
//                                        JsonNode reversalJsonNode3 = objectMapper.readTree(String.valueOf(reversalPostRe));
//                                        String trCode3 = reversalJsonNode3.get("entity").get("transactionCode").asText();
                                      //  String reversalStatusCode = String.valueOf(reversalJsonNode.get("statusCode"));
                                      //  log.info("Response: " + reversalStatusCode.g);


                                        // Transaction failed, handle the failure
                                        log.error("Transaction failed: and reversal done successfully " + postTrRes.getMessage());
                                        response.setMessage("Transaction failed: and reversal done " + postTrRes.getMessage());
                                        response.setStatusCode(HttpStatus.REQUEST_TIMEOUT.value());
                                        response.setEntity(trCode2);
//                                        response.setEntity(trCode3);
                                    }

                                    return response;
                                } catch (Exception e) {
                                    log.error("Error parsing JSON response: " + e.getMessage(), e);
                                    response.setMessage("Error parsing JSON response: " + e.getMessage());
                                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                                    response.setEntity("Error");
                                    return response;
                                }
                            }
                        } else {
                            log.info("The system network is down");
                            // Handle the case where the system network is down
                            response.setMessage("The system network is down");
                            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                            response.setEntity("Error");
                            return response;
                        }
                    } catch (Exception e) {
                        log.error("Error during withdrawal service: " + e.getMessage(), e);
                        response.setMessage("Error during withdrawal service: " + e.getMessage());
                        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        response.setEntity("Error");
                        return response;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error during withdrawal service: " + e.getMessage(), e);
            response.setMessage("Error during withdrawal service: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setEntity("Error");
        }
        return response;
    }
    public String findAtmGl(@RequestParam String userName, @RequestParam String entityId) {
        try {
            log.info("Fetching a list of accounts");
            String accountStatementResponse = restService.findAtmGl(userName, entityId);
            log.info("Receiving list of accounts from the remote server");
            return accountStatementResponse;
        } catch (Exception e) {
            log.error("Failed to retrieve accounts", e);
            throw new RuntimeException(e);
        }
    }
    public String atmReversals(String userName, String entity_id) {
        String response = restService.findAtmReversals(userName,entity_id);
        return response;
    }
    @Transactional
    public List<Object[]> findAtmReversal(String userName, String entity_id) {
        try {
            String response = restService.findAtmReversals(userName, entity_id);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});

            // Extract the list of ATM reversal objects from the response
            List<Map<String, Object>> atmReversalsMap = (List<Map<String, Object>>) responseMap.get("entity");

            // Transform the list of maps into a list of arrays
            List<Object[]> atmReversals = new ArrayList<>();
            for (Map<String, Object> reversalMap : atmReversalsMap) {
                Object[] reversalArray = new Object[7]; // Assuming there are 7 fields in each reversal
                reversalArray[0] = reversalMap.get("atmName");
                reversalArray[1] = reversalMap.get("terminalId");
                reversalArray[2] = reversalMap.get("location");
                reversalArray[3] = reversalMap.get("postedTime");
                reversalArray[4] = reversalMap.get("responseStatus");
                reversalArray[5] = reversalMap.get("acid");
                reversalArray[6] = reversalMap.get("totalDebitAmount");
                atmReversals.add(reversalArray);
            }

            return atmReversals;
        } catch (IOException e) {
            // Handle JSON parsing exception
            log.error("Error occurred while parsing ATM reversals data", e);
            throw new RuntimeException("Error occurred while parsing ATM reversals data", e);
        }
    }


    @Transactional
    public List<Object[]> findErrorLogs(String userName, String entity_id) {
        try {
            String response = restService.findErrorLogs(userName, entity_id);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});

            // Extract the list of ATM reversal objects from the response
            List<Map<String, Object>> atmReversalsMap = (List<Map<String, Object>>) responseMap.get("entity");

            // Transform the list of maps into a list of arrays
            List<Object[]> atmReversals = new ArrayList<>();
            for (Map<String, Object> reversalMap : atmReversalsMap) {
                Object[] reversalArray = new Object[7]; // Assuming there are 7 fields in each reversal
                reversalArray[0] = reversalMap.get("atmName");

                reversalArray[1] = reversalMap.get("location");
                reversalArray[2] = reversalMap.get("postedTime");
                reversalArray[3] = reversalMap.get("responseStatus");

                atmReversals.add(reversalArray);
            }

            return atmReversals;
        } catch (IOException e) {
            // Handle JSON parsing exception
            log.error("Error occurred while parsing ATM reversals data", e);
            throw new RuntimeException("Error occurred while parsing ATM reversals data", e);
        }
    }


}

