package emt.sacco.middleware.AgencyBanking.Service;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import emt.sacco.middleware.AgencyBanking.Dtos.CashDepositReq;

import emt.sacco.middleware.AgencyBanking.Dtos.ResponseDto;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.converter.translator.MessageTranslator;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.org.json.JSONArray;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.org.json.JSONObject;
import emt.sacco.middleware.Utils.CommonService.Channel;
import emt.sacco.middleware.Utils.CommonService.PartTran;
import emt.sacco.middleware.Utils.CommonService.TransactionHeader;
import emt.sacco.middleware.Utils.EntityResponse;
import emt.sacco.middleware.Utils.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
@Service
@Slf4j
public class CashDepositService {
    @Autowired
    RestService restService;

    @Autowired
    ObjectMapper objectMapper;
    @JsonProperty("TransID")
    public static String transID;

    public ResponseDto cashDeposit(String iso8583Request) throws JsonProcessingException {
        ResponseDto response = new ResponseDto();

        System.out.println("Inside Service");
//        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-19s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"10\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%15s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"15\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}]";
        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-20s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%-20s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}]";


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
        String creditAccount = decodedJson.getString("destination_account");
        System.out.println("Credit Account: "+creditAccount);
        String entityId = "001";
        String TransactionType = "Cash Deposit";
        String paymentType = "Agent Deposit";

        String checkAgentAccount = restService.getCustomerByAcid(DebitAccount, entityId);
        JsonNode customerJsonNode = objectMapper.readTree(checkAgentAccount);

        String sendingCustomerCode = String.valueOf(customerJsonNode.get("statusCode"));
        int customerStatusCode = Integer.parseInt(sendingCustomerCode);


        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType("Cash Deposit"); // Set the transaction type
        transactionHeader.setChequeType("");
        transactionHeader.setStaffCustomerCode("");
        transactionHeader.setTransactionCode("");
        transactionHeader.setReversalTransactionCode("");
        transactionHeader.setCurrency("KES");
        transactionHeader.setEodStatus("N");
        transactionHeader.setEntityId("21019");
        transactionHeader.setRcre(new Date());
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setStatus("status");
        transactionHeader.setMpesacode("");
        transactionHeader.setChargeEventId("chargeEventId");
        transactionHeader.setSalaryuploadCode("");
        transactionHeader.setTellerAccount("AGENTACCOUNT");
        transactionHeader.setBatchCode("batchCode");
        transactionHeader.setConductedBy(Channel.AGENCY);
//        transactionHeader.setMiddleWare('Y');
        ArrayList<PartTran> partTrans = new ArrayList<>();
        // Create a PartTran object and set its properties
        PartTran partTran = new PartTran();
        partTran.setAccountType("SBA");
        partTran.setAcid(creditAccount);
        partTran.setPartTranType("Credit");
        partTran.setPartTranIdentity("normal");
        partTran.setIsoFlag('M');
        partTran.setExchangeRate("1.0");
        partTran.setTransactionParticulars("Cash Deposit");
        partTran.setTransactionDate(new Date());
        partTran.setChargeFee('N');
        partTran.setBatchCode("");
        partTran.setTransactionCode(transactionHeader.getTransactionCode());
        partTran.setTransactionAmount(Double.parseDouble(amount));
        partTrans.add(partTran);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setCurrency("KES");
        transactionHeader.setTellerAccount(DebitAccount);
        transactionHeader.setPartTrans(partTrans);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();
        String requestJson = gson.toJson(transactionHeader);

        System.out.println(requestJson);

        if (requestJson == null || requestJson.isEmpty()) {
            response.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.setStatusDescription("Request Json Null");
            return response;
        }

        String transactionEnter = restService.enterTransaction(requestJson, "21019");
        System.out.println("Transfer response1: " + transactionEnter);


        if (transactionEnter == null || transactionEnter.isEmpty()) {
            response.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.setStatusDescription("Transaction Header Null");
            return response;
        }

        JsonNode responseJsonNode = objectMapper.readTree(transactionEnter);
        String responseStatusCode = String.valueOf(responseJsonNode.get("statusCode"));
        int statusCode = Integer.parseInt(responseStatusCode);

        String trCode = responseJsonNode.get("entity").get("transactionCode").asText();
        log.info("transactionentityId code: " + trCode);
        String postTrRes = this.restService.postTransaction1(trCode, entityId);
        log.info("Response: " + postTrRes);

        // Check Balance for debit account
        String debitAccountBalance = restService.retrieveAccount(DebitAccount, entityId);
        JsonNode jsonNode = objectMapper.readTree(debitAccountBalance);
        String acid = jsonNode.get("entity").get("acid").asText();
        System.out.println("Acid: "+acid);
        double accountBalanceDebit = jsonNode.get("entity").get("accountBalance").asDouble();
        System.out.println("Debit Account Balance: "+accountBalanceDebit);
        log.info("Debit Account balance after Transfer: " + accountBalanceDebit);


        // Check Balance for credit account
        String creditAccountBalance = restService.retrieveAccount(creditAccount, entityId);
        JsonNode jsonNode1 = objectMapper.readTree(creditAccountBalance);
        String acid1 = jsonNode1.get("entity").get("acid").asText();
        System.out.println("Acid: "+acid1);
        double accountBalanceCredit = jsonNode1.get("entity").get("accountBalance").asDouble();
        System.out.println("Debit Account Balance: "+accountBalanceCredit);
        log.info("Credit Account balance after Transfer: " + accountBalanceCredit);

        String balances = "2500";

        String mti_id = "0212";
        JSONObject jsonObject = jsonObject();
        // not picking decimals
        jsonObject.append("amount", balances);
        jsonObject.append("meter_id_id", "test_003");



        MessageTranslator mt1 = new MessageTranslator();
        var isoResp = mt1.buildISO8583(jsonObject, config, mti_id);


        response.setIsoCode(new String(isoResp));
        response.setStatusCode(String.valueOf(customerStatusCode));
        response.setStatusDescription("Debit Account Balance: "+accountBalanceDebit + "Credit Account Balance:"+accountBalanceCredit);

        return response;
    }

    public JSONObject jsonObject() {
        JSONObject json = new JSONObject();

        json.put("region_code", "1350     ");
        json.put("settlement_date", "0728 e");
        json.put("switcher_id", "9876   ");
        json.put("ba_reference_number", "e 23582375872385728357823758235235 e");
        json.put("processing_code", "361000 e");
        json.put("transaction_currency_code", "360 e");
        json.put("id_selector", "1");
        json.put("reference_number", "232442364723 e");
        json.put("local_date", "0727 e");
        json.put("pln_reference_number", "24623482422658235825235235472233");
        json.put("local_time", "515151");
        json.put("destination_account", "G01-012850");
        json.put("stan", "346573");
        json.put("acq_institution_code", "555");
        json.put("card_acceptor_terminal", "376832  ");
        json.put("tariff", "  R5");
        json.put("customer_account", "G01-012849         ");
        json.put("customer_name", "REGINA SHIKANDA             ");
        json.put("pan", "364235");
        json.put("customer_id", "    001207");
        json.put("merchant_type", "6789");

        return  json;
    }

    public EntityResponse<String> cashdeposit(CashDepositReq depositReq, String entityId) {
        EntityResponse<String> response = new EntityResponse<>();
        try {
            double amount = depositReq.getAmount();
            String acid = depositReq.getCreditAccount();

            // Account validation
            String customerInfoResponse = restService.retrieveAccount(acid, entityId);
            JsonNode customerJsonNode = objectMapper.readTree(customerInfoResponse);
            int customerStatusCode = customerJsonNode.get("statusCode").asInt();

            if (customerStatusCode == HttpStatus.NOT_FOUND.value()) {
                log.info("Customer account Number " + acid + " has NOT been Registered with us. Thank You for using our services.");
                response.setMessage("Customer account Number " + acid + " has NOT been Registered with us.");
                response.setEntity("");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }
//            EntityResponse<Agent> agentInfo = validateAgent();
//            Agent newAgent = agentInfo.getEntity();
//            String agentId = newAgent.getAgentId();
//            String agentaccount = newAgent.getAgentAccount();



            // Prepare transaction header and part transactions
            TransactionHeader transactionHeader = new TransactionHeader();
            transactionHeader.setTransactionType("Cash Deposit");
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
            transactionHeader.setTellerAccount("Agent Account" );
            transactionHeader.setBatchCode("batchCode");
            transactionHeader.setMiddleware('Y');
            transactionHeader.setConductedBy(Channel.AGENCY);
            ArrayList<PartTran> partTrans = new ArrayList<>();

            // Create a PartTran object and set its properties
            PartTran partTran = new PartTran();
            partTran.setAccountType("SBA");
            partTran.setAcid(acid);
            partTran.setPartTranType("Credit");
            partTran.setPartTranIdentity("normal");
            partTran.setIsoFlag('M');
            partTran.setExchangeRate("1.0");
            partTran.setTransactionParticulars("");
            partTran.setTransactionDate(new Date());
            partTran.setChargeFee('Y');
            partTran.setBatchCode("");
            partTran.setTransactionCode(transactionHeader.getTransactionCode());
            partTran.setTransactionAmount(amount);
            partTrans.add(partTran);
            transactionHeader.setTransactionDate(new Date());
            transactionHeader.setCurrency("KES");
            transactionHeader.setTellerAccount("GLS705001");
            transactionHeader.setPartTrans(partTrans);
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                    .create();
            String requestJson = gson.toJson(transactionHeader);

            if (requestJson == null || requestJson.isEmpty()) {
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setStatusDescription("Request Json Null");
                return response;
            }

            // Enter transaction
            String restServiceResponse = restService.enterTransaction(requestJson, "casc");
            System.out.println("Completing Deposit process: " + restServiceResponse);
            JsonNode customerJsonNodes = objectMapper.readTree(restServiceResponse);
            String sendingCustomerCode = String.valueOf(customerJsonNodes.get("statusCode"));

            int customerStatusCod = Integer.parseInt(sendingCustomerCode);
            if (customerStatusCod == HttpStatus.NOT_ACCEPTABLE.value()) {
                response.setMessage("Account Not Verified");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("Not Acceptable");
                return response;
            } else if (customerStatusCod == HttpStatus.NOT_FOUND.value()) {
                response.setMessage(restServiceResponse);
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("Not Acceptable");
                return response;
            } else {
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
                    RestService.TransactionResponse postTrRes = this.restService.postTransaction(trCode, transID);
                    log.info("Response: " + postTrRes);
                    response.setMessage("Deposit successful. Transaction ID:"  + trCode);

                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity("Customer Account: " + acid);
                    return response;


                    //                                        JsonNode responseJsonNode = objectMapper.readTree(restServiceResponse);
                    // JsonNode reversalJsonNode = objectMapper.readTree(reversalResponse);
                    //  String reversalStatusCode = String.valueOf(reversalJsonNode.get("statusCode"));
                    //  log.info("Response: " + reversalStatusCode.g);
                    // Transaction failed, handle the failure

                } catch (Exception e) {
                    log.error("Error parsing JSON response: " + e.getMessage(), e);
                    response.setMessage("Error parsing JSON response: " + e.getMessage());
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    response.setEntity("Error");
                    return response;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

//    public EntityResponse<Agent> validateAgent() {
//        EntityResponse<Agent> entityResponse = new EntityResponse<>();
//        try {
//            String userName = UserRequestContext.getCurrentUser();
//            String entityId = EntityRequestContext.getCurrentEntityId();
//            if (userName == null) {
//                userName = Constants.SYSTEM_USERNAME;
//                entityId = Constants.SYSTEM_ENTITY;
//            }
//
//            String url = " http://localhost:9004/api/v1/agent/get/agents{id}";
//
//            OkHttpClient client = new OkHttpClient().newBuilder()
//                    .connectTimeout(100, TimeUnit.SECONDS)
//                    .readTimeout(300, TimeUnit.SECONDS)
//                    .build();
//
//            Request request = new Request.Builder()
//                    .get()
//                    .url(url)
//                    .addHeader("userName", userName)
//                    .addHeader("entityId", entityId)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//            String res = response.body().string();
//            log.info("CRM API Response: {}", res);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(res);
//            String message = jsonNode.path("message").asText();
//            JsonNode entityNode = jsonNode.path("entity");
//
//            if ("The requested record could not be found".equals(message)) {
//                entityResponse.setMessage("No record found");
//                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
//                log.info("Agent not Registered");
//            } else if ("Success".equals(message)) {
//                log.info("Agent details fetch successful");
//                if (entityNode != null && entityNode.size() > 0) {
//                    JsonNode firstElement = entityNode.get(0);
//                    Agent agentOnboarding = new Agent()
//                    String agentId = firstElement.path("agentId").asText();
//                    String agentAccount = firstElement.path("agentAccount").asText();
//                    agentOnboarding.setAgentAccount(agentAccount);
//                    agentOnboarding.setAgentId(agentId);
//                    entityResponse.setStatusCode(HttpStatus.OK.value());
//                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
//                    entityResponse.setEntity(agentOnboarding);
//                } else {
//                    entityResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
//                    entityResponse.setMessage("Invalid JSON structure in CRM response");
//                    log.error("Invalid JSON structure in CRM response: {}", entityNode.toString());
//                }
//            } else {
//                entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//                entityResponse.setMessage("Unexpected response from CRM");
//                log.error("Unexpected response from CRM. Status code: {}", res);
//            }
//        } catch (Exception e) {
//            log.error("Error occurred while fetching agent info: {}", e.getMessage(), e);
//            entityResponse.setMessage("Error occurred while fetching agent info: " + e.getMessage());
//            entityResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
//        }
//        return entityResponse;
//    }
//}

}
