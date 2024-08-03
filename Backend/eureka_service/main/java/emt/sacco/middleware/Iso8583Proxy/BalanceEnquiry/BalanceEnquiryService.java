package emt.sacco.middleware.Iso8583Proxy.BalanceEnquiry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import emt.sacco.middleware.Utils.RestService;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.converter.translator.MessageTranslator;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.org.json.JSONArray;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class BalanceEnquiryService {

    @Autowired
    RestService restService;


    @Autowired
    ObjectMapper objectMapper;


    //JSON METHOD
    public AccountBalanceRes checkBalance(AccountBalanceReq accountBalanceReq) throws JsonProcessingException {
        log.info("inside Controller");

        String Name = accountBalanceReq.getName();
        String  Accounti = accountBalanceReq.getAccount();


        String accountJsonResponse = restService.retrieveAccount(Accounti, Name);
        JsonNode jsonNode = objectMapper.readTree(accountJsonResponse);
        String sendingCustomerCode = String.valueOf(jsonNode.get("statusCode"));
        int customerStatusCode = Integer.parseInt(sendingCustomerCode);
        String acid = jsonNode.get("entity").get("acid").asText();
        System.out.println("Acid: "+acid);
        double accountBalance = jsonNode.get("entity").get("accountBalance").asDouble();
        System.out.println("Account Balance: "+accountBalance);


        AccountBalanceRes resultPayload = new AccountBalanceRes();
        resultPayload.setIsoCode("test_003");

        return resultPayload;
    }

    //ISO FOMART GET BAL
    public AccountBalanceRes getBalance(String iso8583Request) throws JsonProcessingException {
        System.out.println("Inside Service");

//        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-22s\",\"variable\":\"transmission_date_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"22\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%22s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"22\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}]";
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
        String Acid = decodedJson.getString("customer_account");
        System.out.println("Account Number: "+Acid);

        String entityId = "20993";





        String accountJsonResponse = restService.retrieveAccount(Acid, entityId);
        JsonNode jsonNode = objectMapper.readTree(accountJsonResponse);
        String sendingCustomerCode = String.valueOf(jsonNode.get("statusCode"));
        int customerStatusCode = Integer.parseInt(sendingCustomerCode);
        String acid = jsonNode.get("entity").get("acid").asText();
        System.out.println("Acid: "+acid);
        double accountBalance = jsonNode.get("entity").get("accountBalance").asDouble();
        System.out.println("Account Balance: "+accountBalance);

        String balances = "2500";

        String mti_id = "0212";
        JSONObject jsonObject = jsonObject();

        jsonObject.append("amount", balances);
        jsonObject.append("meter_id_id", "test_003");



        MessageTranslator mt1 = new MessageTranslator();
        var isoResp = mt1.buildISO8583(jsonObject, config, mti_id);

        AccountBalanceRes resultPayload = new AccountBalanceRes();
        resultPayload.setIsoCode(new String(isoResp));

        return resultPayload;
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
        json.put("transmission_date_time", "0727515151");
        json.put("stan", "346573");
        json.put("acq_institution_code", "555");
        json.put("card_acceptor_terminal", "376832  ");
        json.put("tariff", "  R5");
        json.put("customer_account", "SBA607-210120310002         ");
        json.put("destination_account", "SBA607-210120310002         ");
        json.put("customer_name", "Aiden             ");
        json.put("pan", "364235");
        json.put("customer_id", "    001207");
        json.put("merchant_type", "6789");

        return  json;
    }
}

