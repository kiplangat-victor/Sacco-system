package emt.sacco.middleware.Mobile.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import emt.sacco.middleware.Mobile.Models.AccountReq;
import emt.sacco.middleware.Mobile.Models.MobileTransfer;
import emt.sacco.middleware.Utils.RestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MobileService {

    @Autowired
    private final RestService restService;
    @Autowired
    private final ObjectMapper objectMapper;

    public String processMobileTransfer(MobileTransfer mobileTransfer) throws Exception {
        //EntityResponse response = new EntityResponse();
        String transfer;
        try {

            String checkCustomer = restService.getCustomer(mobileTransfer.getPhoneNumber(), "21019");
            JsonNode customerJsonNode = objectMapper.readTree(checkCustomer);
            String sendingCustomerCode = String.valueOf(customerJsonNode.get("statusCode"));
            int customerStatusCode = Integer.parseInt(sendingCustomerCode);
            if (customerStatusCode == HttpStatus.NOT_FOUND.value()) {
                log.info("Customer MSISDN " + mobileTransfer.getPhoneNumber() + " has NOT been Registered with us.");
                return "success";
            }
            else if (customerStatusCode != HttpStatus.OK.value()) {
                log.error("Error fetching customer info. StatusCode: " + customerStatusCode);
                return "success";
            } else {
                boolean b = customerStatusCode == HttpStatus.OK.value();

                Gson gson = new Gson();
                log.info("Mobile Transfer Request Received - " + gson.toJson(mobileTransfer));
                String jsonPayload = gson.toJson(mobileTransfer);

            // Make the RESTful API call with the JSON payload
            transfer = restService.mobileTransferRest(jsonPayload, "21019");
            }
        } catch (Exception e) {
            throw new Exception("Error occurred while processing money transfer: " + e.getMessage());
        }
            return transfer;
 //       String checkCustomer = restService.getCustomer(phoneNumber, bankShortCode);
//        JsonNode customerJsonNode = objectMapper.readTree(response);
//        String accountCode = String.valueOf(customerJsonNode.get("statusCode"));
//        int customerStatusCode = Integer.parseInt(accountCode);
//        if (customerStatusCode != HttpStatus.OK.value()) {
//            return "Customer MSISDN " + "acid" + " has NOT been Registered with us. Thank You for using our services.";
//        }else
//            return response;
        }
    public ResponseEntity<byte[]> getAccountStatement(String acid, String fromdate, String todate) {


        log.info("Fetching data for account statement");
        ResponseEntity<byte[]> accountStatementResponse = restService.getAccountStatement(acid, fromdate, todate);
        if (accountStatementResponse.getStatusCode().is2xxSuccessful()) {
            byte[] pdfContent = accountStatementResponse.getBody();
            log.info("Received PDF document from SWITCH MIDDLEWARE. Size: {} bytes", pdfContent.length);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfContent.length);
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfContent);
        } else {
            log.error("Failed to fetch account statement. Status code: {}", accountStatementResponse.getStatusCodeValue());
            return ResponseEntity.status(accountStatementResponse.getStatusCode()).body(accountStatementResponse.getBody());
        }
    }

    public String getAccountMinistatements(@RequestParam String acid,
                                           @RequestParam String maxCount,
                                           @RequestParam String entityId,
                                           @RequestParam String phoneNumber

    ) throws Exception {
        String acoount;
        try {

            String checkCustomer = restService.getCustomer(phoneNumber, entityId);
            JsonNode customerJsonNode = objectMapper.readTree(checkCustomer);
            String sendingCustomerCode = String.valueOf(customerJsonNode.get("statusCode"));
            int customerStatusCode = Integer.parseInt(sendingCustomerCode);
            if (customerStatusCode == HttpStatus.NOT_FOUND.value()) {
                log.info("Customer MSISDN " + phoneNumber + " has NOT been Registered with us.");
                return "Not successful";
            } else if (customerStatusCode != HttpStatus.OK.value()) {
                log.error("Error fetching customer info. StatusCode: " + customerStatusCode);
                return "Not successful";
            } else {
                boolean b = customerStatusCode == HttpStatus.OK.value();
                acoount = restService.getAccountMinistatements(acid, maxCount, entityId, phoneNumber);
                return acoount;
            }
        } catch (Exception e) {
            throw new Exception("Error occurred while retrieving account Ministatement: " + e.getMessage());
        }
    }

//AccountCreationService
//    public  AccountCreationService(ObjectMapper objectMapper, UrlConfig urlConfig, CommonService commonService, RestService restService) {
//        this.objectMapper = objectMapper;
//        this.urlConfig = urlConfig;
//        this.commonService = commonService;
//        this.restService = restService;
//    }




    public List<String> getAccounts(String customerCode) {
        try {
//            String entityId = EntityRequestContext.getCurrentEntityId();
            String entityId = "20993";
            // Call the Rest Service to get the list of acid values
            String response = restService.getCustomerAccountByCustomerCode(customerCode, entityId);

            // Parse the JSON response to extract the list of account numbers
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode entityNode = jsonResponse.get("entity");
            if (entityNode != null && entityNode.isArray()) {
                List<String> accountNumbers = objectMapper.convertValue(entityNode, new TypeReference<List<String>>() {});
                return accountNumbers;
            } else {
                // Handle the case where the 'entity' node is missing or not an array
                return Collections.emptyList(); // or any other appropriate error handling
            }
        } catch (IOException e) {
            // Handle the JSON parsing exception
            log.error("Error parsing JSON response: {}", e.getMessage());
            return Collections.emptyList(); // or any other appropriate error handling
        } catch (Exception e) {
            log.error("Error occurred while retrieving accounts : {}", e.getMessage());
            return Collections.emptyList(); // or any other appropriate error handling
        }

    }
    public String createAccount(AccountReq accountReq) {
        return restService.openAccount(accountReq.toString(),"001");
    }

}




