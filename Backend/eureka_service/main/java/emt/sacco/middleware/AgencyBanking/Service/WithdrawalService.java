package emt.sacco.middleware.AgencyBanking.Service;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import emt.sacco.middleware.ATM.Dto.AtmDto;
import emt.sacco.middleware.AgencyBanking.Dtos.ResponseDto;
import emt.sacco.middleware.Iso8583Proxy.FundTransfer.FundsTransferRes;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.converter.translator.MessageTranslator;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.org.json.JSONArray;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.org.json.JSONObject;
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
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;

@Service
@Slf4j
public class WithdrawalService {

    @Autowired
    private RestService restService;
    @Autowired
    @Qualifier("getObjectMapper")
    private ObjectMapper objectMapper;
    @JsonProperty("TransId")
    public static String transId;

    public EntityResponse<String> cardValidation(@RequestParam String cardNumber, String pin){
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
                String clientIpAddress = request.getRemoteAddr();
                EntityResponse<AtmDto> machineInfo = restService.validateAtmMachine(clientIpAddress);
                if (machineInfo.getStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    log.error("AGENT MACHINE NOT CONFIGURED FOR TRANSACTION " + machineInfo.getStatusCode());
                    response.setMessage("UNAUTHORIZED ACCESS: ");
                    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                    response.setEntity("Error");
                }
                else if (machineInfo.getStatusCode() != HttpStatus.OK.value()) {
                    log.error("Error validating AGENT machine. StatusCode: " + machineInfo.getStatusCode());
                    response.setMessage("Server error ");
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    response.setEntity("Error");
                }else {
                    AtmDto onboardingRequestDto1 = machineInfo.getEntity();
                    String terminalId2 = onboardingRequestDto1.getTerminalId();
                    String ip2 = onboardingRequestDto1.getIpAddress();
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

                                    RestService.TransactionResponse postTrRes = this.restService.postTransaction(trCode, transId);
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

                                        String reversalPostRe = restService.testPost(trCode2, transId);
                                        System.out.println("Posting of reversal and updating of balances " + reversalPostRe);
                                        JsonNode reversalJsonNode3 = objectMapper.readTree(String.valueOf(reversalPostRe));
                                        String trCode3 = reversalJsonNode3.get("entity").get("transactionCode").asText();
                                        //  String reversalStatusCode = String.valueOf(reversalJsonNode.get("statusCode"));
                                        //  log.info("Response: " + reversalStatusCode.g);


                                        // Transaction failed, handle the failure
                                        log.error("Transaction failed: and reversal done successfully " + postTrRes.getMessage());
                                        response.setMessage("Transaction failed: and reversal done " + postTrRes.getMessage());
                                        response.setStatusCode(HttpStatus.REQUEST_TIMEOUT.value());
                                        response.setEntity(trCode3);
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
    public Object withdrawalServiceiSO(String iso8583Request) throws Exception {
       // String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-20s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"10\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%20s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}, {\"field\":\"52\",\"format\":\"%04d\",\"variable\":\"card_pin\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"}]";
        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-20s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%-20s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}]";

//        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-20s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%-20s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}, {\"field\":\"52\",\"format\":\"%04d\",\"variable\":\"card_pin\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"}]";

        MessageTranslator mt = new MessageTranslator();
        JSONArray config = new JSONArray();
        config = new JSONArray(configStr);

        var decodedJson = mt.parseISO8583(iso8583Request, config);

        System.out.println("HERE IS THE DECODED JSON OBJECT: "+decodedJson);

        String amount = decodedJson.getString("amount");
        System.out.println("AMOUNT: "+amount);
        String Name1 = decodedJson.getString("customer_name");
        System.out.println("Name: "+Name1);
        String DebitAccount = decodedJson.getString("customer_account");
        System.out.println("Debit Account: "+DebitAccount);
        String agentAccount = decodedJson.getString("destination_account");
        System.out.println("Credit Account: "+agentAccount);
        //String customerPin = decodedJson.getString("card_pin");
        //System.out.println("Pin: " + customerPin);
        String entityId = "001";
        String TransactionType = "CASH-WITHDRAWAL";
        String paymentType = "AGENCY";
//        String clientIpAddress = decodedJson.getClient();
//        EntityResponse<AtmDto> machineInfo = restService.validateAtmMachine(clientIpAddress);
//        AtmDto onboardingRequestDto1 = machineInfo.getEntity();
//        String terminalId2 = onboardingRequestDto1.getTerminalId();
//        String ip2 = onboardingRequestDto1.getIpAddress();
//        String atmAccount2 = onboardingRequestDto1.getAtmAccount();


        String checkCustomer = restService.getCustomerByAcid(DebitAccount, entityId);
        JsonNode customerJsonNode = objectMapper.readTree(checkCustomer);

        String sendingCustomerCode = String.valueOf(customerJsonNode.get("statusCode"));
        int customerStatusCode = Integer.parseInt(sendingCustomerCode);
        EntityResponse<String> response = new EntityResponse<>();

        log.info("Continue with transaction");
        // Create a TransactionHeader object
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType("Cash Withdrawal"); // Set the transaction type
        transactionHeader.setChequeType("");
        transactionHeader.setStaffCustomerCode("");
        transactionHeader.setTransactionCode("");
        transactionHeader.setReversalTransactionCode("");
        transactionHeader.setCurrency("KES");
        // transactionHeader.setIpAddress(currentIpAddress);
        transactionHeader.setEodStatus("N");
        transactionHeader.setEntityId("21019");
        transactionHeader.setRcre(new Date());
//        transactionHeader.setTransParticulars("Cash Withdrawal");
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setStatus("status");
        transactionHeader.setMpesacode("");
        transactionHeader.setChargeEventId("chargeEventId");
        transactionHeader.setSalaryuploadCode("");
        transactionHeader.setTellerAccount("AGENTACCOUNT");
        transactionHeader.setBatchCode("batchCode");
        transactionHeader.setMiddleware('Y');
//                            transactionHeader.setConductedBy("ATM "+terminalId2);
        ArrayList<PartTran> partTrans = new ArrayList<>();
        // Create a PartTran object and set its properties
        PartTran partTran = new PartTran();
        partTran.setAccountType("SBA");
        partTran.setAcid(DebitAccount);
        partTran.setPartTranType("Debit");
        partTran.setPartTranIdentity("normal");
        partTran.setIsoFlag('M');
        partTran.setExchangeRate("1.0");
        partTran.setTransactionParticulars("Cash Withdrawal");
        partTran.setTransactionDate(new Date());
        partTran.setChargeFee('Y');
        partTran.setBatchCode("");
        partTran.setTransactionCode(transactionHeader.getTransactionCode());
        partTran.setTransactionAmount(Double.parseDouble(amount));
        partTrans.add(partTran);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setCurrency("KES");
        transactionHeader.setTellerAccount(agentAccount);
        transactionHeader.setPartTrans(partTrans);
        // Convert the object to JSON using the injected Gson instance
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();
        String requestJson = gson.toJson(transactionHeader);

        System.out.println(requestJson);

        if (requestJson == null || requestJson.isEmpty()) {
            ResponseDto withdrawalAgencyRes = new ResponseDto();
            withdrawalAgencyRes.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            withdrawalAgencyRes.setStatusDescription("Request Json Null");
            return response;
        }

        String transactionEnter = restService.enterTransaction1(requestJson, "casc");
        System.out.println("Transfer response: " + transactionEnter);


        if (transactionEnter == null || transactionEnter.isEmpty()) {
            FundsTransferRes respons = new FundsTransferRes();
            respons.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            respons.setStatusDescription("Transaction Header Null");
            return response;
        }

        JsonNode responseJsonNode = objectMapper.readTree(transactionEnter);
        String responseStatusCode = String.valueOf(responseJsonNode.get("statusCode"));
        int statusCode = Integer.parseInt(responseStatusCode);

        String trCode = responseJsonNode.get("entity").get("transactionCode").asText();
        log.info("transactionentityId code: " + trCode);
        String postTrRes = String.valueOf(this.restService.postTransaction(trCode, entityId));
        log.info("Response: " + postTrRes);

        // Check Balance for debit account
        String debitAccountBalance = restService.retrieveAccount(DebitAccount, entityId);
        JsonNode jsonNode = objectMapper.readTree(debitAccountBalance);
        String acid = jsonNode.get("entity").get("acid").asText();
        System.out.println("Acid: "+acid);
        double accountBalanceDebit = jsonNode.get("entity").get("accountBalance").asDouble();
        System.out.println("Debit Account Balance: "+accountBalanceDebit);
        log.info("Debit Account balance after Transfer: " + accountBalanceDebit);


        String balances = "2500";

        String mti_id = "0212";
        JSONObject jsonObject = jsonObject();
        // not picking decimals
        jsonObject.append("amount", balances);
        jsonObject.append("meter_id_id", "test_003");



        MessageTranslator mt1 = new MessageTranslator();
        var isoResp = mt1.buildISO8583(jsonObject, config, mti_id);


        ResponseDto responsePayload = new ResponseDto();
        responsePayload.setIsoCode(new String(isoResp));
        responsePayload.setStatusCode(String.valueOf(customerStatusCode));
        responsePayload.setStatusDescription("Debit Account Balance: "+accountBalanceDebit );

        return responsePayload;
    }

    public JSONObject jsonObject() {
        JSONObject json = new JSONObject();

        json.put("region_code", "1350     ");
        json.put("settlement_date", "0728");
        json.put("switcher_id", "9876   ");
        json.put("ba_reference_number", "23582375872385728357823758235235");
        json.put("processing_code", "361000");
        json.put("transaction_currency_code", "360");
        json.put("id_selector", "1");
        json.put("reference_number", "232442364723");
        json.put("local_date", "0727");
        json.put("pln_reference_number", "24623482422658235825235235472233");
        json.put("local_time", "515151");
        json.put("destination_account", "G01-012850");
        json.put("stan", "346573");
        json.put("acq_institution_code", "555");
        json.put("card_acceptor_terminal", "376832  ");
        json.put("tariff", "  R5");
        json.put("customer_account", "G01-012849         ");
        json.put("customer_name", "OWUOR VICTOR             ");
        json.put("pan", "364235");
        json.put("customer_id", "    001207");
        json.put("merchant_type", "6789");

        return  json;
    }
}
