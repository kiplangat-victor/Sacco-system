package emt.sacco.middleware.Iso8583Proxy;

import emt.sacco.middleware.Iso8583Proxy.iso8583converter.converter.translator.MessageTranslator;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.org.json.JSONArray;
import emt.sacco.middleware.Iso8583Proxy.iso8583converter.org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/generator")
public class ISOMessageGenerator {

//    public static void main(String[] args) {
//        generateIsoMessage();
//    }

//    public static void generateIsoMessage() {
//        System.out.println("GENERATING ISO FOR TEST PURPOSES " + new Date());
//
//        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-22s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"22\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"10\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%22s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"22\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}]";
//
//        MessageTranslator mt = new MessageTranslator();
//        String mti_id = "0210";
//        String iso_new = "";
//        JSONArray config = new JSONArray();
//        JSONObject json = jsonObject();
//
//        config = new JSONArray(configStr);
//        System.out.println("HERE IS THE CONFIG STRING" + config);
//
//        iso_new = new String(mt.buildISO8583(json, config, mti_id));
//
//        System.out.println("HERE IS THE NEW ISO MESSAGE: " + iso_new);
//    }
//
//    public static JSONObject jsonObject() {
//        JSONObject json = new JSONObject();
//
//        json.put("region_code", "2754");
//        json.put("amount", "500");
//        json.put("settlement_date", "0728");
//        json.put("meter_id_id", "71837122171");
//        json.put("switcher_id", "9876   ");
//        json.put("ba_reference_number", "23582375872385728357823758235235");
//        json.put("processing_code", "361000");
//        json.put("transaction_currency_code", "360");
//        json.put("id_selector", "1");
//        json.put("reference_number", "232442364723");
//        json.put("local_date", "0727");
//        json.put("pln_reference_number", "24623482422658235825235235472233");
//        json.put("local_time", "515151");
//        json.put("destination_account", "G01-012850");
//        json.put("stan", "346573");
//        json.put("acq_institution_code", "555");
//        json.put("card_acceptor_terminal", "376832  ");
//        json.put("tariff", "  R5");
//        json.put("customer_account", "SBA607-210120310002         ");
//        json.put("customer_name", "Aiden             ");
//        json.put("pan", "364235");
//        json.put("customer_id", "    210120310002");
//        json.put("merchant_type", "6789");
//
//        return json;
//    }


    @GetMapping("/trans/iso")
    public String jsonObject(String amount, String destinationAccount, String customerAccount, String pin) {
        JSONObject json = new JSONObject();

        json.put("region_code", "2754");
//        json.put("amount", "5555");
        json.put("amount", amount);
        json.put("settlement_date", "0304");
        json.put("meter_id_id", "71837122171");
        json.put("switcher_id", "9876   ");
        json.put("ba_reference_number", "23582375872385728357823758235235");
        json.put("processing_code", "361000");
        json.put("transaction_currency_code", "360");
        json.put("id_selector", "1");
        json.put("reference_number", "232442364723");
        json.put("local_date", "0727");
        json.put("pln_reference_number", "24623482422658235825235235472233");
        json.put("local_time", "515151");
//        json.put("destination_account", "SBA54-21019037000001");
        json.put("destination_account", destinationAccount);

        json.put("stan", "346573");
        json.put("acq_institution_code", "555");
        json.put("card_acceptor_terminal", "376832  ");
        json.put("tariff", "  R5");
//        json.put("customer_account", "SBA54-21019037000002");
        json.put("customer_account", customerAccount);
        json.put("customer_name", "KENNETH KINYANJUI           ");
        json.put("card_pin", "pin");
//        json.put("customer_name", customerName);

        json.put("pan", "364235");
        json.put("customer_id", "    001207");
        json.put("merchant_type", "6789");

        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-20s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"10\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%20s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}, {\"field\":\"52\",\"format\":\"%04d\",\"variable\":\"card_pin\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"}]";
//        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-20s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%-20s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}, {\"field\":\"52\",\"format\":\"%04d\",\"variable\":\"card_pin\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"}]";

        MessageTranslator mt = new MessageTranslator();
        String mti_id = "0210";
        String iso_new = "";
        String xml = "";
        JSONArray config = new JSONArray();

        config = new JSONArray(configStr);
        System.out.println("HERE IS THE CONFIG STRING"+config);

        iso_new = new String(mt.buildISO8583(json, config, mti_id));

        System.out.println("HERE IS THE NEW ISO MESSAGE: "+iso_new);

        return  iso_new;
    }

    //TODO GENERATE MESSAGE FOR BALANCE ENQUIRY
    @GetMapping("/bal/iso")
    public String jsonObjecti(String amount, String customerAccount, String pin) {
        JSONObject json = new JSONObject();

        json.put("region_code", "2754");
//        json.put("amount", "5555");
        json.put("amount", amount);
        json.put("settlement_date", "0304");
        json.put("meter_id_id", "71837122171");
        json.put("switcher_id", "9876   ");
        json.put("ba_reference_number", "23582375872385728357823758235235");
        json.put("processing_code", "361000");
        json.put("transaction_currency_code", "360");
        json.put("id_selector", "1");
        json.put("reference_number", "232442364723");
        json.put("local_date", "0727");
        json.put("pln_reference_number", "24623482422658235825235235472233");
        json.put("local_time", "515151");
//        json.put("destination_account", "SBA54-21019037000001");
//        json.put("destination_account", destinationAccount);

        json.put("stan", "346573");
        json.put("acq_institution_code", "555");
        json.put("card_acceptor_terminal", "376832  ");
        json.put("tariff", "  R5");
//        json.put("customer_account", "SBA54-21019037000002");
        json.put("customer_account", customerAccount);
        json.put("customer_name", "KENNETH KINYANJUI           ");
        json.put("card_pin", "pin");
//        json.put("customer_name", customerName);

        json.put("pan", "364235");
        json.put("customer_id", "    001207");
        json.put("merchant_type", "6789");

        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-20s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"10\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%20s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}, {\"field\":\"52\",\"format\":\"%04d\",\"variable\":\"card_pin\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"}]";
//        String configStr = "[{\"field\":\"2\",\"format\":\"%-6s\",\"variable\":\"pan\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"6\"},{\"field\":\"3\",\"format\":\"%06d\",\"variable\":\"processing_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"4\",\"format\":\"%012d\",\"variable\":\"amount\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"7\",\"format\":\"%-20s\",\"variable\":\"destination_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"11\",\"format\":\"%06d\",\"variable\":\"stan\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"6\"},{\"field\":\"12\",\"format\":\"%-6s\",\"variable\":\"local_time\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"6\"},{\"field\":\"13\",\"format\":\"%-4s\",\"variable\":\"local_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"15\",\"format\":\"%-4s\",\"variable\":\"settlement_date\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"4\"},{\"field\":\"18\",\"format\":\"%04d\",\"variable\":\"merchant_type\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"},{\"field\":\"32\",\"format\":\"%-3s\",\"variable\":\"acq_institution_code\",\"options\":\"\",\"type\":\"LLVAR\",\"field_length\":\"3\"},{\"field\":\"37\",\"format\":\"%012d\",\"variable\":\"reference_number\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"12\"},{\"field\":\"41\",\"format\":\"%-8s\",\"variable\":\"card_acceptor_terminal\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"8\"},{\"field\":\"42\",\"format\":\"%-20s\",\"variable\":\"customer_account\",\"options\":\"\",\"type\":\"STRING\",\"field_length\":\"20\"},{\"field\":\"48\",\"format\":\"%-7s%011d%012d%01d%-32s%-32s%-25s%4s%-9s\",\"variable\":\"switcher_id, meter_id_id, customer_id, id_selector, pln_reference_number, ba_reference_number, customer_name, tariff, region_code\",\"options\":\"\",\"type\":\"LLLVAR\",\"field_length\":\"133\"},{\"field\":\"49\",\"format\":\"%03d\",\"variable\":\"transaction_currency_code\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"3\"}, {\"field\":\"52\",\"format\":\"%04d\",\"variable\":\"card_pin\",\"options\":\"\",\"type\":\"NUMERIC\",\"field_length\":\"4\"}]";

        MessageTranslator mt = new MessageTranslator();
        String mti_id = "0210";
        String iso_new = "";
        String xml = "";
        JSONArray config = new JSONArray();

        config = new JSONArray(configStr);
        System.out.println("HERE IS THE CONFIG STRING"+config);

        iso_new = new String(mt.buildISO8583(json, config, mti_id));

        System.out.println("HERE IS THE NEW ISO MESSAGE: "+iso_new);

        return  iso_new;
    }

}
