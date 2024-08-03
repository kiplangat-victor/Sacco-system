package emt.sacco.middleware.Iso8583Proxy.FundTransfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import emt.sacco.middleware.ATM.Dto.TranRequest;
import emt.sacco.middleware.Utils.CommonService.Channel;
import emt.sacco.middleware.Utils.CommonService.PartTran;
import emt.sacco.middleware.Utils.CommonService.TransactionHeader;
import emt.sacco.middleware.Utils.CustomerInfo.UserDetailsRequestContext;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.EntityResponse;
import emt.sacco.middleware.Utils.RestService;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.converter.translator.MessageTranslator;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.org.json.JSONArray;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
@Slf4j
public class FundsTransferService {

    @Autowired
    RestService restService;

    @Autowired
    ObjectMapper objectMapper;


    public FundsTransferRes moneyTransfer(FundsTransferReq fundsTransferReq) throws Exception {

        String DebitAccount = fundsTransferReq.getDebitAccount();
        String creditAccount = fundsTransferReq.getCreditAccount();
        Double amount = fundsTransferReq.getAmount();
        String entityId = EntityRequestContext.getCurrentEntityId();
        String TransactionType = "Funds Transfer";
        String paymentType = "Mobile";

        String checkCustomer = restService.getCustomerByAcid(DebitAccount, entityId);
        JsonNode customerJsonNode = objectMapper.readTree(checkCustomer);

        String sendingCustomerCode = String.valueOf(customerJsonNode.get("statusCode"));
        int customerStatusCode = Integer.parseInt(sendingCustomerCode);


        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setSn(12345L);
        transactionHeader.setTransactionType("Transfer");
        transactionHeader.setCurrency("KES");
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setPostedBy(UserDetailsRequestContext.getcurrentUserDetails());
        transactionHeader.setEntityId(entityId);
        transactionHeader.setConductedBy(Channel.POS);

        ArrayList<PartTran> partTrans = new ArrayList<>();

        PartTran debitPartTran = new PartTran();
        debitPartTran.setAccountType("SBA");
        debitPartTran.setAcid(DebitAccount);
        debitPartTran.setPartTranType("Debit");
        debitPartTran.setIsoFlag('M');
        debitPartTran.setExchangeRate("1.0");
        debitPartTran.setTransactionParticulars("Transfer");
        debitPartTran.setTransactionDate(new Date());
        debitPartTran.setChargeFee('Y');
        debitPartTran.setTransactionAmount(Double.parseDouble(String.valueOf(amount)));
        debitPartTran.setConductedBy(Channel.POS);
        partTrans.add(debitPartTran);

        PartTran creditPartTran = new PartTran();
        creditPartTran.setAccountType("SBA");
        creditPartTran.setAcid(creditAccount);
        creditPartTran.setPartTranType("Credit");
        creditPartTran.setIsoFlag('M');
        creditPartTran.setExchangeRate("1.0");
        creditPartTran.setTransactionParticulars(paymentType);
        creditPartTran.setTransactionDate(new Date());
        creditPartTran.setChargeFee('Y');
        creditPartTran.setTransactionAmount(Double.parseDouble(String.valueOf(amount)));
        debitPartTran.setConductedBy(Channel.POS);
        partTrans.add(creditPartTran);

        transactionHeader.setPartTrans(partTrans);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();
        String requestJson = gson.toJson(transactionHeader);

        System.out.println(requestJson);

        if (requestJson == null || requestJson.isEmpty()) {
            FundsTransferRes response = new FundsTransferRes();
            response.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.setStatusDescription("Request Json Null");
            return response;
        }

        String transactionEnter = restService.enterTransaction(requestJson, entityId);
        System.out.println("Transfer response: " + transactionEnter);


        if (transactionEnter == null || transactionEnter.isEmpty()) {
            FundsTransferRes response = new FundsTransferRes();
            response.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.setStatusDescription("Transaction Header Null");
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


        // Check Balance for credit account
        String creditAccountBalance = restService.retrieveAccount(creditAccount, entityId);
        JsonNode jsonNode1 = objectMapper.readTree(creditAccountBalance);
        String acid1 = jsonNode1.get("entity").get("acid").asText();
        System.out.println("Acid: "+acid1);
        double accountBalanceCredit = jsonNode1.get("entity").get("accountBalance").asDouble();
        System.out.println("Credit Account Balance: "+accountBalanceCredit);
        log.info("Credit Account balance after Transfer: " + accountBalanceCredit);


        FundsTransferRes responsePayload = new FundsTransferRes();
        responsePayload.setIsoCode("test_003");
        responsePayload.setStatusCode(String.valueOf(customerStatusCode));
        responsePayload.setStatusDescription("Debit Account Balance: "+accountBalanceDebit + "Credit Account Balance:"+accountBalanceCredit);

        return responsePayload;
    }


    public FundsTransferRes fundsTransfer(String iso8583Request) throws JsonProcessingException {

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
        String entityId = EntityRequestContext.getCurrentEntityId();
        String TransactionType = "Funds Transfer";
        String paymentType = "Mobile";

        String checkCustomer = restService.getCustomerByAcid(DebitAccount, entityId);
        JsonNode customerJsonNode = objectMapper.readTree(checkCustomer);

        String sendingCustomerCode = String.valueOf(customerJsonNode.get("statusCode"));
        int customerStatusCode = Integer.parseInt(sendingCustomerCode);


        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setSn(12345L);
        transactionHeader.setTransactionType("Transfer");
        transactionHeader.setCurrency("KES");
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setMiddleware('Y');
        transactionHeader.setPostedBy(UserDetailsRequestContext.getcurrentUserDetails());
        transactionHeader.setEntityId(entityId);
        transactionHeader.setConductedBy(Channel.POS);

        ArrayList<PartTran> partTrans = new ArrayList<>();

        PartTran debitPartTran = new PartTran();
        debitPartTran.setAccountType("SBA");
        debitPartTran.setAcid(DebitAccount);
        debitPartTran.setPartTranType("Debit");
        debitPartTran.setIsoFlag('M');
        debitPartTran.setExchangeRate("1.0");
        debitPartTran.setTransactionParticulars("Transfer");
        debitPartTran.setTransactionDate(new Date());
        debitPartTran.setChargeFee('Y');
        debitPartTran.setCurrency("KES");
        debitPartTran.setTransactionAmount(Double.parseDouble(String.valueOf(amount)));
        debitPartTran.setConductedBy(Channel.POS);
        partTrans.add(debitPartTran);

        PartTran creditPartTran = new PartTran();
        creditPartTran.setAccountType("SBA");
        creditPartTran.setAcid(creditAccount);
        creditPartTran.setPartTranType("Credit");
        creditPartTran.setIsoFlag('M');
        creditPartTran.setExchangeRate("1.0");
        creditPartTran.setTransactionParticulars(paymentType);
        creditPartTran.setTransactionDate(new Date());
        creditPartTran.setChargeFee('Y');
        creditPartTran.setCurrency("KES");
        creditPartTran.setTransactionAmount(Double.parseDouble(String.valueOf(amount)));
        partTrans.add(creditPartTran);

        transactionHeader.setTotalAmount(Double.parseDouble(String.valueOf(amount)));
        transactionHeader.setPartTrans(partTrans);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();
        String requestJson = gson.toJson(transactionHeader);

        System.out.println(requestJson);

        if (requestJson == null || requestJson.isEmpty()) {
            FundsTransferRes response = new FundsTransferRes();
            response.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.setStatusDescription("Request Json Null");
            return response;
        }

        String transactionEnter = restService.enterTransaction(requestJson, entityId);
        System.out.println("Transfer response1: " + transactionEnter);


        if (transactionEnter == null || transactionEnter.isEmpty()) {
            FundsTransferRes response = new FundsTransferRes();
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



        JsonNode payload = responseJsonNode.get("entity");
        String email = restService.emailNotification(String.valueOf(payload), "21019");
        log.info("The email is " + email);
        System.out.println("Email Sent Successfully");

        // Check Balance for debit account
        String debitAccountBalance = restService.retrieveAccount(DebitAccount, entityId);
        JsonNode jsonNode = objectMapper.readTree(debitAccountBalance);
        String acid = jsonNode.get("entity").get("acid").asText();
        System.out.println("Acid: "+acid);
        JsonNode jsonNodeN = objectMapper.readTree(debitAccountBalance);
//        String name = jsonNode.get("entity").get("accountName").toString();
        String name = jsonNode.get("entity").get("accountName").asText();
        double accountBalanceDebit = jsonNode.get("entity").get("accountBalance").asDouble();
        System.out.println("Debit Account Balance: "+accountBalanceDebit);
        log.info("Debit Account balance after Transfer: " + accountBalanceDebit);
        System.out.println("");


        // Check Balance for credit account
        String creditAccountBalance = restService.retrieveAccount(creditAccount, entityId);
        JsonNode jsonNode1 = objectMapper.readTree(creditAccountBalance);
        String acid1 = jsonNode1.get("entity").get("acid").asText();
        System.out.println("Acid: "+acid1);
        JsonNode jsonNodeN2 = objectMapper.readTree(creditAccountBalance);
        String namec = jsonNode1.get("entity").get("accountName").asText();
        double accountBalanceCredit = jsonNode1.get("entity").get("accountBalance").asDouble();
        System.out.println("Debit Account Balance: "+accountBalanceCredit);
        log.info("Credit Account balance after Transfer: " + accountBalanceCredit);

        String balances = "2500";
        String namee = name.toString();


        String mti_id = "0212";
        JSONObject jsonObject = jsonObject();
        // not picking decimals
        jsonObject.append("amount", balances);
        jsonObject.append("meter_id_id", "test_003");
//        jsonObject.append("customer_name", namee);
//        jsonObject.append("destination_account", creditAccount);
//        jsonObject.append("customer_account", DebitAccount);




        MessageTranslator mt1 = new MessageTranslator();
        var isoResp = mt1.buildISO8583(jsonObject, config, mti_id);


        FundsTransferRes responsePayload = new FundsTransferRes();
        responsePayload.setIsoCode(new String(isoResp));
        responsePayload.setStatusCode(String.valueOf(customerStatusCode));
//        responsePayload.setStatusDescription("Debit Account Balance: "+accountBalanceDebit + "Credit Account Balance:"+accountBalanceCredit);
        responsePayload.setStatusDescription(amount + transactionHeader.getCurrency() + " SUCCESSFULLY PAID TO " + namec + " FROM " + name + " " + transactionHeader.getEnteredTime());
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
        json.put("customer_name", "KENNETH KINYANJUI           ");
        json.put("pan", "364235");
        json.put("customer_id", "    001207");
        json.put("merchant_type", "6789");

        return  json;
    }

    public EntityResponse<TranRequest> fundsTransfers(String iso8583Request) throws Exception {
        EntityResponse<TranRequest> entityResponse = new EntityResponse<>();

        System.out.println("Inside Service");
//        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-19s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"19\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%19s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"19\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}]";
        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-20s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%-20s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}]";


        MessageTranslator mt = new MessageTranslator();
        JSONArray config = new JSONArray();
        config = new JSONArray(configStr);

        var decodedJson = mt.parseISO8583(iso8583Request, config);

        System.out.println("HERE IS THE DECODED JSON OBJECT: " + decodedJson);

        String amount = decodedJson.getString("amount");
        System.out.println("AMOUNT: " + amount);
        String Name1 = decodedJson.getString("customer_name");
        System.out.println("Name: " + Name1);
        String DebitAccount = decodedJson.getString("customer_account");
        System.out.println("Debit Account: " + DebitAccount);
        String creditAccount = decodedJson.getString("destination_account");
        System.out.println("Credit Account: " + creditAccount);
        String entityId = "001";
        String TransactionType = "Funds Transfer";
        String paymentType = "Mobile";
        TranRequest toapesa = new TranRequest();
        toapesa.setAcid(DebitAccount);
        toapesa.setAmount(amount);

        entityResponse.setEntity(toapesa);
        return entityResponse;
    }


}
