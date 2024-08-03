package emt.sacco.middleware.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import emt.sacco.middleware.ATM.Dto.AtmDto;
import emt.sacco.middleware.Utils.Config.UrlConfig;
import emt.sacco.middleware.Utils.Config.UserRequestContext;
import emt.sacco.middleware.Utils.CustomerInfo.Account;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class RestService {
    @Value("${spring.application.E_WALLET_URL}")
    String url;
    @Value("${links.accountbyphone}")
    String accountbyphoneurl;

    @Value("${spring.application.pbuwals.apiKey")
    String apiKey;

    @Value("${spring.application.pbuwals.username")
    String username;
    @Value("${spring.application.pbuwals.password")
    String password;


    private final UrlConfig urlConfig;

    private final ObjectMapper objectMapper;

    public RestService(UrlConfig urlConfig, ObjectMapper objectMapper) {
        this.urlConfig = urlConfig;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<byte[]> getAccountStatement(String acid, String fromdate, String todate) {
        String url = "http://localhost:9008/api/v1/reports/account-statement?acid="
                + acid +
                "&fromdate=" + fromdate + "&todate=" + todate;
        byte[] response = execute(url, "", "application/pdf", "GET");
        if (response != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "account-statement.pdf");
            headers.setContentLength(response.length);
            return ResponseEntity.ok().headers(headers).body(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private byte[] execute(String url, String payload, String contentType, String method) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();
        try {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", contentType);
            //.addHeader("userName", username);
            if (!method.equalsIgnoreCase("GET")) {
                RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), payload);
                requestBuilder.method(method, requestBody);
            } else {
                if (payload != null && !payload.isEmpty()) {
                    url += (url.contains("?") ? "&" : "?") + "payload=" + URLEncoder.encode(payload, StandardCharsets.UTF_8);
                }
                requestBuilder.get();
            }

            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                return response.body().bytes();
            } else {
                log.error("HTTP Request unsuccessful. Status code: {}", response.code());
                return null;
            }
        } catch (IOException e) {
            log.error("HTTP Request unsuccessful: {}", e.getMessage());
            throw new RuntimeException("HTTP Request unsuccessful", e);
        } finally {
            client.dispatcher().executorService().shutdown();
        }
    }

    public String getAccountByPhone(String accountType, String phone, String entityId) {
        String url = "http://localhost:9006/accounts/accounts/by/phone?accountType=" + accountType + "&phone=" + phone;
        String response = execute(url, "", "application/json", "GET", entityId);
        System.out.println(response);
        return response;
    }

    public String getAccountByPhoneTotalBalanceForAllLoans(String accountType, String phone, String entityId) {
        String url = "http://localhost:9006/accounts/accounts/by/phone/find/total/balance/for/all/loans?accountType=" + accountType + "&phone=" + phone;
        String response = execute(url, "", "application/json", "GET", entityId);
        System.out.println(response);
        return response;
    }

    public String getAccountTransactions(String acid, String maxCount, String entityId) {
        String url = "http://localhost:9006/accounts/account-statement?acid=" + acid + "&maxCount=" + maxCount;
        var response = execute(url, "", "application/json", "GET", entityId);
        System.out.println(response);
        return response;
    }

//    public String enterTransaction(String payload, String entityId) {
//        String url = "http://localhost:9006/api/v1/transaction/enter";
//        var response = execute(url, payload, "application/json", "POST", entityId);
//        System.out.println(response);
//        return response;
//    }

    public String mobileTransferRest(String payload, String entityId) {
        String url = "http://localhost:9006/transactions/mobile/transfer";
//        String url = "http://localhost:9006/transactions/system";
        var response = execute(url, payload, "application/json", "POST", entityId);
        System.out.println(response);
        return response;
    }

    public String retrieveAccount(String acid, String entityId) {
        String url = "http://localhost:9006/accounts/" + acid;
        var response = execute(url, "", "application/json", "GET", entityId);
        System.out.println(response);
        return response;
    }

//    public String postTransaction(String receiptNumber, String entityiD) {
//        try {
//            String ur = "http://localhost:9006/api/v1/transaction/post?transactionCode=" + receiptNumber;
//            OkHttpClient client = new OkHttpClient().newBuilder()
//                    .connectTimeout(100, TimeUnit.SECONDS)
//                    .readTimeout(300, TimeUnit.SECONDS)
//                    .build();
//
//            Request.Builder requestBuilder = new Request.Builder()
//                    .url(ur)
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("userName", "EM-MDWARE")
//                    .addHeader("entityId", entityiD)
//                    .put(RequestBody.create(null, new byte[0]));
//
//            Request request = requestBuilder.build();
//            Response response = client.newCall(request).execute();
//            return response.body() != null ? response.body().string() : null;
//        } catch (Exception e) {
//            log.info("HTTP Request unsuccessful: " + e.getLocalizedMessage());
//        }
//        return null;
//    }

    public String getCustomer(String msisdn, String entityId) {
        String url = "http://localhost:9005/api/v1/customer/retail/check/by/phone/" + msisdn;
        String response = execute(url, "", "application/json", "GET", entityId);
        return response;
    }

    public String getLoanLimit(String customerCode, String productCode, String entityId) {
        String url = "http://localhost:9006/loans/loan/limit?customerCode=" + customerCode + "&productCode=" + productCode;
        String response = execute(url, "", "application/json", "GET", entityId);
        return response;
    }

    public String verifyAccount(String Acid, String entityId) {
        String url = "http://localhost:9006/accounts/verify?Acid=" + Acid;
        String response = executeVerify(url, "", "application/json", "PUT", entityId);
        return response;
    }

    public String activateMobileMoney(String customerCode, String entityId) {
        String url = "http://localhost:9005/api/v1/customer/retail/activate-mobile-money?customerCode=" + customerCode;
        String response = execute(url, "", "application/json", "POST", entityId);
        return response;
    }

    public String retrieveSavingsAccount(String accountType, String msisdn, String entityId) {
        String url = "http://localhost:9006/accounts/accounts/by/phone?accountType=" + accountType + "&phone=" + msisdn;
        String response = execute(url, "", "application/json", "GET", entityId);
        return response;
    }

    public String getSavingsAccount(String msisdn, String entityId) {
        String url = "http://localhost:9006/accounts/main-savings-account/by/phone?phone=" + msisdn;
        String response = execute(url, "", "application/json", "GET", entityId);
        return response;
    }

    public String retrieveLoanAccount(String accountType, String msisdn, String entityId) {
        String url = "http://localhost:9006/accounts/accounts/by/phone?accountType=" + accountType + "&phone=" + msisdn;
        String response = execute(url, "", "application/json", "GET", entityId);
        return response;
    }

    public String requestDisbursement(String acid, String entityId) {
        String url = "http://localhost:9006/loans/disburse?acid=" + acid;
        String response = execute(url, "", "application/json", "PUT", entityId);
        return response;
    }

    public String verifyDisbursement(String acid, String entityId) {
        String url = "http://localhost:9006/loans/verify/loan/disbursment?acid=" + acid;
//        String url = "http://localhost:9006/loans/verify/loan/disbursment?acid=" + acid;
        String response = executeVerify(url, "", "application/json", "PUT", entityId);
        return response;
    }

    public String prepayLoan(String payload, String entityId) {
        String url = "http://localhost:9006/loans/prepay";
        var res = execute(url, payload, "application/json", "PUT", entityId);
        System.out.println(res);
        return res;
    }

    public String checkExternalTransactionState(String externalReceiptNumber, String entityId) {
        String url = "http://localhost:9006/api/v1/transaction/by/externalReceiptNumber/" + externalReceiptNumber;
        return execute(url, "", "application/json", "GET", entityId);
    }

    public String postResultToMM(String payload, String messageId) {
        String url = "https://pbuwalsuatmw.postbank.co.ug/sms";
        return mmCaller(url, payload, "application/xml", "POST", messageId);
    }

    public String openAccount(String payload, String entityId) {
        String url = "http://localhost:9006/accounts/open";
        return execute(url, payload, "application/json", "POST", entityId);
    }

    public String execute(String url, String payload, String contentType, String method, String entityId) {
        return execute(url, payload, contentType, method, UserRequestContext.getCurrentUser(), entityId);
    }

    public String executeVerify(String url, String payload, String contentType, String method, String entityId) {
        return execute(url, payload, contentType, method, "MDWARE-VFR", entityId);
    }


    public String execute(String url, String payload, String contentType, String method, String username, String entityId) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", contentType)
                    .addHeader("userName", username)
                    .addHeader("entityId", entityId);

            if (!method.equalsIgnoreCase("GET")) {
                RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), payload);
                requestBuilder.method(method, requestBody);
            } else {
                requestBuilder.get();
            }

            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            log.info("HTTP Request unsuccessful: " + e.getLocalizedMessage());
        }
        return null;
    }


    public String mmCaller(String url, String payload, String contentType, String method, String messageId) {
        try {
            String username = "SMS_OPENAPI_2023";
            String password = "A%7928yH5";
            String apiKey = "92864983454546346298423423";
            String uuid = UUID.randomUUID().toString();

            SignatureGenerator signatureGenerator = new SignatureGenerator();
            String signature = signatureGenerator.base64Encode(username, password, apiKey, messageId);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", contentType)
                    .addHeader("apiKey", apiKey)
                    .addHeader("username", username)
                    .addHeader("x-forwarded-for", "41.72.214.150")
                    .addHeader("signature", signature)
                    .addHeader("UUID", uuid);

            if (!method.equalsIgnoreCase("GET")) {
                RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), payload);
                requestBuilder.method(method, requestBody);
            } else {
                requestBuilder.get();
            }

            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            log.info("HTTP Request unsuccessful: " + e.getLocalizedMessage());
        }
        return null;
    }

    public String verifier(String url, String payload, String contentType, String method) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", contentType)
                    .addHeader("userName", "SYSTEM")
                    .addHeader("entityId", "001");

            if (!method.equalsIgnoreCase("GET")) {
                RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), payload);
                requestBuilder.method(method, requestBody);
            } else {
                requestBuilder.get();
            }

            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            log.info("HTTP Request unsuccessful: " + e.getLocalizedMessage());
        }
        return null;
    }

    public String getAccountMinistatements(String acid, String maxCount, String entityId, String phoneNumber) {

        try {
            String url = "http://localhost:9006/accounts/account-ministatement?acid=" + acid + "&maxCount=" + maxCount;
            var response = execute(url, "", "application/json", "GET", entityId);
            System.out.println(response);
            return response;
        } catch (Exception e) {
            log.info("HTTP Request unsuccessful: " + e.getLocalizedMessage());
        }
        return null;

    }


    public String getCustomerAccountByCustomerCode(String customerCode, String entityId) {
        String url = "http://localhost:9006/accounts/find/account/by/customer/code/active/" + customerCode;
        String response = execute(url, "", "application/json", "GET", entityId);
        System.out.println(response);
        return response;
    }

    public String getCustomerByAcid(String DebitAccount, String entityId) {
        System.out.println("Headed to get Customer by acid");
        String url = "http://localhost:9006/accounts/check/if/account/exists/" + DebitAccount;
//        String url = "http://localhost:9006/accounts/check/by/acid/" + DebitAccount;
        String response = execute(url, "", "application/json", "GET", entityId);
        return response;
    }



    public String getAtms(String entityId) {
        String url = "http://localhost:9004/atms/getAtms";
        String response = execute(url, "", "application/json", "GET", entityId);
        System.out.println(response);
        return response;
    }

    public JsonNode deleteAtmMachineById(Long Id, String entityId) {
        String url = "http://localhost:9004/atms/delete/" + Id;
        var response = execute(url, "", "application/json", "DELETE", entityId);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);
            return jsonResponse;
        } catch (IOException e) {
            log.error("Error parsing JSON response: {}", e.getMessage());
            return null;
        }
    }

    public String acceptRequest(String batchId) {
        String url = "http://localhost:9004/inventory/card/accept";
        String response = execute(url, batchId, "application/json", "POST", batchId);
        System.out.println(response);
        return response;
        }

    public String rejectRequest(String batchId) {
        String url = "http://localhost:9004/inventory/card/reject";
        String response = execute(url, batchId, "application/json", "POST", batchId);
        System.out.println(response);
        return response;
    }
    public String disburseCards(String batchId) {
        String url = "http://localhost:9004/inventory/card/disburse";
        String response = execute(url, batchId, "application/json", "POST", batchId);
        System.out.println(response);
        return response;
    }
    public String generateErrorReports(String reportFormat) {
        String url = "http://localhost:9008/atmlogs/errors/" + reportFormat;
        var response = execute(url, "", "application/json", "GET",reportFormat);
        System.out.println(response);
        return response;
    }
    public String generateReversalReports(String reportFormat) {
        String url = "http://localhost:9008/atmlogs/reversals/" + reportFormat;
        var response = execute(url, "", "application/json", "GET",reportFormat);
        System.out.println(response);
        return response;
    }
    public String generateTransactionReports(String reportFormat) {
        String url = "http://localhost:9008/atmlogs/transactions/" + reportFormat;
        var response = execute(url, "", "application/json", "GET",reportFormat);
        System.out.println(response);
        return response;
    }
    public JsonNode verifyAtm(String entityId, Long id) {
        String url = "http://localhost:9004/atms/verify/" + id;
        var response = execute(url, "", "application/json", "PUT", entityId);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);
            return jsonResponse;
        } catch (IOException e) {
            log.error("Error parsing JSON response: {}", e.getMessage());
            // Handle the exception accordingly, for example, return null or throw it
            return null;
        }
    }

    public String modifyAtms(String jsonPayload, String entityId) {
        String url ="http://localhost:9004/atms/modify";
        String response = execute(url, jsonPayload, "application/json", "PUT", entityId);
        return response;
    }

    public String getAtmBalance(String terminalId, String entityId) {
        String url = "http://localhost:9004/api/atmsBalance/balance?terminalId=" + terminalId + "&entityId=" + entityId;
        String response = execute(url, "", "application/json", "GET",entityId);
        return response;
    }
  
    public String getAtmCustomerBalance(String acid) {
        String url = "http://localhost:9006/api/accounts/atmbalance?acid=" + acid ;
        String response = execute(url, "", "application/json", "GET", acid);
        return response;
    }




    public class TransactionResponse {
        private boolean success;
        private String message;

        public TransactionResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
    public String enterTransaction1 (String requestJson, String transID) throws Exception {
        String url = "http://localhost:9006/api/v1/transaction/enter";
        System.out.println(url);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestJson);
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("userName", "Denver")
                .addHeader("entityId", "001")
                .url(url)
                .post(body);
        System.out.println("After request");
        // Check if the transID is not null and add it to the request header
        if (transID != null) {
            requestBuilder.addHeader("transID", transID);
        }
        System.out.println("after trans");
        Request request = requestBuilder.build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        System.out.println("Before returning");
        return response.body().string();
    }
    public String findMerchantAcid(String userName, String entityId) {
        String url = "http://localhost:9006/accounts/find/Sba/account/";
        var response = execute(url, "", "application/json", "GET",userName,entityId);
        System.out.println(response);
        return response;
    }
    public String findAtmGl(String userName, String entityId) {
        String url = "http://localhost:9006/accounts/find/Atm/account/";
        var response = execute(url, "", "application/json", "GET",userName,entityId);
        System.out.println(response);
        return response;
    }
    public JsonNode addAtm(String jsonPayload, String entityId){
        String url = "http://localhost:9004/atms/addAtms";
        var response = execute(url,jsonPayload, "application/json", "POST", entityId);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);
            return jsonResponse;
        } catch (IOException e) {
            log.error("Error parsing JSON response: {}", e.getMessage());
            // Handle the exception accordingly, for example, return null or throw it
            return null;
        }
    }

    public JsonNode addPos(String jsonPayload, String entityId){
        String url = "http://localhost:9004/pos/add";
        var response = execute(url,jsonPayload, "application/json", "POST", entityId);
        System.out.println(response);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);
            return jsonResponse;
        } catch (IOException e) {
            log.error("Error parsing JSON response: {}", e.getMessage());
            // Handle the exception accordingly, for example, return null or throw it
            return null;
        }
    }


    public TransactionResponse postTransaction(String receiptNumber, String entityiD) {
        try {
            String url = "http://localhost:9006/api/v1/transaction/post?transactionCode=" + receiptNumber;
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), "");

            OkHttpClient client = new OkHttpClient().newBuilder()
//                    .connectTimeout(1, TimeUnit.MICROSECONDS) // Set connect timeout to 10 seconds
//                    .readTimeout(1, TimeUnit.MICROSECONDS)    // Set read timeout to 30 seconds
//                    .build();

            .connectTimeout(10, TimeUnit.SECONDS) // Set connect timeout to 10 seconds
                    .readTimeout(30, TimeUnit.SECONDS)    // Set read timeout to 30 seconds
                    .build();

            Request request = new Request.Builder()
                    .addHeader("userName", EntityRequestContext.getCurrentEntityId())
                    .addHeader("entityId", UserRequestContext.getCurrentUser())
                    .url(url)
                    .put(body)
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println("Before returning");

            if (response.isSuccessful()) {
                return new TransactionResponse(true, "Transaction successful.");
            } else {
                return new TransactionResponse(false, "Transaction failed: HTTP error code " + response.code());
            }
        } catch (SocketTimeoutException e) {
            // Handle socket timeout exception
            log.error("Socket timeout exception occurred: " + e.getMessage());
            return new TransactionResponse(false, "Socket timeout: The server took too long to respond. Please try again later or reverse the transaction.");
        } catch (Exception e) {
            // Handle other exceptions
            log.error("HTTP Request unsuccessful: " + e.getMessage());
            return new TransactionResponse(false, "HTTP Request unsuccessful: " + e.getMessage());
        }
    }

    public String testPost(String trCode2, String transID) {
        try {
            String url = "http://localhost:9006/api/v1/transaction/post?transactionCode=" + trCode2;

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), "");
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("userName", "ATM")
                    .addHeader("entityId", "21021")
                    .put(body);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();

            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            log.error("HTTP Request unsuccessful: " + e.getMessage());
        }
        return null;
    }
    public static EntityResponse retrieveAccount(String acid) {
        //EntityResponse<?> response = new EntityResponse<>();
        try {
            String userName = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (userName == null) {
                userName = Constants.SYSTEM_USERNAME;
                entityId = Constants.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse = new EntityResponse<>();
            String url = "http://localhost:9006/accounts/" + acid;
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", userName)
                    .addHeader("entityId", entityId)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                int statusCode = response.code();
                String res = response.body().string();
                log.info("CRM API Response: {}", res);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(res);
                String message = jsonNode.path("message").asText();
                JsonNode entityNode = jsonNode.path("entity");
                if ("The requested record could not be found".equals(message)) {
                    //  List<AtmGlResponse> atmGlResponses = convertToAtmGlResponses(entityNode);
                    entityResponse.setMessage("no record found");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    // entityResponse.setEntity(entityNode.get);
//                if (res == HttpStatus.NOT_FOUND.value()) {
//                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
//                    entityResponse.setMessage("Customer not found in CRM");
                    log.info("Customer not found in CRM");
                    return entityResponse;
                } else if  ("Success".equals(message))
                {
                    log.info("CRM fetch successful");
                    try {
                        JSONObject jsonResponse = new JSONObject(res); // Parse JSON response
                        Gson g = new Gson();
                        Account account = g.fromJson(jsonResponse.getJSONObject("entity").toString(), Account.class);
                        Account customerDetails = new Account();
                        customerDetails.setAcid(jsonResponse.getJSONObject("entity").getString("acid"));
                        entityResponse.setStatusCode(HttpStatus.OK.value());
                        entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                        entityResponse.setEntity(account);
                    }
                    catch (JSONException jsonException) {
                        entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        entityResponse.setMessage("Error parsing CRM response");
                        log.error("Error parsing CRM response: {}", jsonException.getMessage(), jsonException);
                    }
                }
                else {
                    entityResponse.setStatusCode(statusCode);
                    entityResponse.setMessage("Unexpected response from CRM");
                    log.error("Unexpected response from CRM. Status code: {}", statusCode);
                }
            } catch (IOException ioException) {
                return entityResponse;
            }
            return entityResponse;
        } catch (Exception e) {
            log.error("Error occurred while fetching customer info by phone number: {}", e.getMessage(), e);
            EntityResponse<Throwable> response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }
    public static EntityResponse cardValidation(String cardNumber,String pin) {
        try {
            String userName = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (userName == null) {
                userName = Constants.SYSTEM_USERNAME;
                entityId = Constants.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse = new EntityResponse<>();
            // Construct the CRM API URL for fetching customer info by phone number
            String url = "http://localhost:9004/api/v1/atmCard/cardValidation?cardNumber=" + cardNumber + "&pin=" + pin;

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", userName)
                    .addHeader("entityId", entityId)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            log.info("CRM API Response: {}", res);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(res);
            String message = jsonNode.path("message").asText();
            JsonNode entityNode = jsonNode.path("entity");
            if ("Successful".equals(message)) {
                log.info("CARD DETAILS fetch successful");
                // Extracting the 'acid' value from the 'entity' node
                String acid = entityNode.path("acid").asText();
                // Setting up the EntityResponse with success message and acid value
                entityResponse.setMessage("Success");
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setEntity(acid); // Assuming you want to set the 'acid' value in the entity response

            } else if ("ATM card is expired".equals(message)) {
                // Handling the case where the ATM card is expired
                entityResponse.setMessage("ATM card is expired");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            else if ("Incorrect PIN".equals(message)) {
                // Handling the case where the PIN is incorrect
                entityResponse.setMessage("Incorrect PIN");
                entityResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            }
            else if ("Card not verified".equals(message)) {
                // Handling the case where the card is not verified
                entityResponse.setMessage("Card not verified");
                entityResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
            } else if ("ATM card is deleted".equals(message)) {
                // Handling the case where the ATM card is deleted
                entityResponse.setMessage("ATM card is deleted");
                entityResponse.setStatusCode(HttpStatus.GONE.value());
            } else {
                // Handling other cases, such as invalid JSON structure in the response
                entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                entityResponse.setMessage("Unknown error occurred.Try Again!!");
                log.error("Unknown error occurred. Message: {}", message);
            }
            return entityResponse; // Add this return statement at the end
        } catch (Exception e) {
            log.error("Error occurred while validating card: {}", e.getMessage(), e);
            EntityResponse<Throwable> response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }
    public static EntityResponse<AtmDto> validateAtmMachine(String acid) {
        try {
            String userName = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (userName == null) {
                userName = Constants.SYSTEM_USERNAME;
                entityId = Constants.SYSTEM_ENTITY;
            }
            EntityResponse<AtmDto> entityResponse = new EntityResponse<>();
            String url = "http://localhost:9004/atms/getAtmByIpAddress/ipAddress?ipAddress=" + acid;
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", userName)
                    .addHeader("entityId", entityId)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            log.info("CRM API Response: {}", res);
            log.info("this is the ip address that send the request"+acid);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(res);
            String message = jsonNode.path("message").asText();
            JsonNode entityNode = jsonNode.path("entity");

            if ("The requested record could not be found".equals(message)) {
                entityResponse.setMessage("No record found");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                log.info("Device Ip not Registered");
            } else if ("Success".equals(message)) {
                log.info("Machine details fetch successful");
                if (entityNode != null && entityNode.size() > 0) {
                    JsonNode firstElement = entityNode.get(0);
                    AtmDto onboardingRequestDto = new AtmDto();
                    String terminalId1 = firstElement.path("terminalId").asText();
                    String ip = firstElement.path("ipAddress").asText();
                    String atmAccount = firstElement.path("atmAccount").asText();
                    onboardingRequestDto.setAtmAccount(atmAccount);
                    onboardingRequestDto.setIpAddress(ip);
                    onboardingRequestDto.setTerminalId(terminalId1);
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setEntity(onboardingRequestDto);
                } else {
                    entityResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                    entityResponse.setMessage("Invalid JSON structure in CRM response");
                    log.error("Invalid JSON structure in CRM response: {}", entityNode.toString());
                }
            } else {
                entityResponse.setStatusCode(Integer.valueOf(res));
                entityResponse.setMessage("Unexpected response from CRM");
                log.error("Unexpected response from CRM. Status code: {}", res);
            }
            return entityResponse;
        } catch (Exception e) {
            log.error("Error occurred while fetching customer info by phone number: {}", e.getMessage(), e);
            EntityResponse<AtmDto> response = new EntityResponse<>();
            response.setMessage("Error occurred while fetching customer info by phone number: " + e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }
    }
    public String initiateReversal(String trCode) throws Exception {
        String url = "http://localhost:9006/api/v1/transaction/reverse?transactionCode="+trCode;
        System.out.println(url);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), trCode);
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("userName", "Denver")
                .addHeader("entityId", "001")
                .url(url)
                .put(body);
        System.out.println("After request");
        // Check if the transID is not null and add it to the request header
//        if (transID != null) {
//            requestBuilder.addHeader("transID", transID);
//        }
        System.out.println("after trans");
        Request request = requestBuilder.build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        System.out.println("Before returning");
        return response.body().string();
    }

    public String verifyReversal(String trCode) throws Exception {
        String url = "http://localhost:9006/api/v1/transaction/post?transactionCode="+trCode;
        System.out.println(url);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), trCode);
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("userName", "Denver")
                .addHeader("entityId", "001")
                .url(url)
                .put(body);
        System.out.println("After request");
        // Check if the transID is not null and add it to the request header
//        if (transID != null) {
//            requestBuilder.addHeader("transID", transID);
//        }
        System.out.println("after trans");
        Request request = requestBuilder.build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        System.out.println("Before returning");
        return response.body().string();
    }

    public String resetPin( String cardNumber, String previousPin, String newPin, String confirmNewPin) {
//        String url = "http://localhost:9004/atm/pin/reset-pin";
//        String url = "http://localhost:9004/atm/pin/reset-pin?cardNumber=cardNumber&previousPin=previousPin&newPin=newPin&confirmNewPin=confirmNewPin"+cardNumber+previousPin+newPin+confirmNewPin;
//        String url = "http://localhost:9004/api/atmsBalance/balance?terminalId=" + terminalId + "&entityId=" + entityId;
        String url = "http://localhost:9004/atm/pin/reset-pin?cardNumber="+cardNumber+"&previousPin="+previousPin+"&newPin="+newPin+"&confirmNewPin="+confirmNewPin;

        // Prepare the request payload
        String payload = "{" +
//                "\"entityId\": \"" + entityId + "\"," +
                "\"cardNumber\": \"" + cardNumber + "\"," +
                "\"previousPin\": \"" + previousPin + "\"," +
                "\"newPin\": \"" + newPin + "\"," +
                "\"confirmNewPin\": \"" + confirmNewPin + "\"" +
                "}";

        // Execute the HTTP request with the correct method (POST)
        String response = execute(url, payload, "application/json", "POST", newPin);
        return response;
    }

//        public String resetPin(String cardNumber, String newPin) {
//            String url = "http://localhost:9004/atm/pin/reset-pin";
//            // Call the CBS service to execute PIN reset logic
//            // This could involve making HTTP requests, calling a SOAP service, etc.
//            // Return the response from the CBS service
//            return "Pin reset successfully for card number: " + cardNumber;
//        }

//    public String addPos(String jsonPayload, String entityId){
//        String url = "http://localhost:9004/pos/add";
//        var response = execute(url,jsonPayload, "application/json", "POST", entityId);
//        System.out.println(response);
//        return response;
//    }

    public String getAllPos(String entityId) {
        String url = "http://localhost:9004/pos/get/all";
        String response = execute(url, "", "application/json", "GET", entityId);
        System.out.println(response);
        return response;
    }

    public String getPosById(String entityId, Long id) {
        String url = "http://localhost:9004/pos/get/by/" + id;
        String response = execute(url, "", "application/json", "GET", entityId);
        System.out.println(response);
        return response;
    }

    public String unverifiedPos(String entityId) {
        String url = "http://localhost:9004/pos/find/all/unverified";
        String response = execute(url, "", "application/json", "GET", entityId);
        System.out.println(response);
        return response;
    }

    public String modifyPos(String jsonPayload, String entityId) {
        //String url ="http://localhost:9004/pos/modify";
        String url = "http://localhost:9004/pos/modify";

        String response = execute(url, jsonPayload, "application/json", "PUT", entityId);
        return response;
    }

    public String verifyPos(String entityId, Long id) {
        String url = "http://localhost:9004/pos/verify/" + id; // Corrected URL construction
        String response = execute(url, "", "application/json", "PUT", entityId);
        System.out.println(response);
        return response;
    }

    public String deletePos(String terminalId) {
        String url = "http://localhost:9004/pos/deleteByTerminalId/" + terminalId;
        String response = execute(url, "", "application/json", "DELETE", terminalId);
        System.out.println(response);
        return response;
    }

    public String enterTransaction(String payload, String entityId) {
        String url = "http://localhost:9006/api/v1/transaction/enter";
        var response = execute(url, payload, "application/json", "POST", entityId);
        System.out.println(response);
        return response;
    }

    public String emailNotification(String payload, String entityId) {
        String url = "http://localhost:9006/api/v1/transaction/send-email?payload=" + payload;
        var response = execute(url, payload, "application/json", "POST", entityId);
        System.out.println(response);
        return response;
    }

    public String postTransaction1(String receiptNumber, String entityiD) {
        try {
            String ur = "http://localhost:9006/api/v1/transaction/post?transactionCode=" + receiptNumber;
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(ur)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("userName", UserRequestContext.getCurrentUser())
                    .addHeader("entityId", EntityRequestContext.getCurrentEntityId())
                    .put(RequestBody.create(null, new byte[0]));

            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            log.info("HTTP Request unsuccessful: " + e.getLocalizedMessage());
        }
        return null;
    }
    public String verifyAgent(Long Id, String entityId) {
        String url = "http://localhost:9004/api/v1/verify/" + Id;
        String response = execute(url, "", "application/json", "PUT", "21021");
        return response;
    }

    public String modifyAgency(String requestBody, String entityId) {
        String url = "http://localhost:9004/api/v1/ModifyAgency";
        String response = execute(url, requestBody, "application/json", "PUT", "21021");
        return response;
    }

    public String getAllUnverifiedAgencies(String entityId) {
        String url = "http://localhost:9004/api/v1/ListOfUnverifiedAgency/all";
        String response = execute(url, "", "application/json", "GET", entityId);

        if (response == null) {
            log.error("Error retrieving unverified agencies: Response content is null");
            return ""; // or
        }
        return response;
    }

    public String getAllAgentsStatus(String status) {
        String url = "http://localhost:9004/api/v1/allAgentsStatus";
        String response = execute(url, "", "application/json", "GET", status);

        if (response == null) {
            log.error("Error retrieving agent status: Response content is null");
            return ""; // or
        }
        return response;
    }

    public String deleteAgent(Long Id, String entityId) {
        // String url = "http://localhost:9004/api/v1/DeleteAgency"+ Id ;
        String url = "http://localhost:9004/api/v1/DeleteAgency/" + Id;

        String response = execute(url, "", "application/json", "DELETE", entityId);
        return response;
    }

    public String getAllAgents(String entityId) {
        String url = "http://localhost:9004/api/v1/getAgency";
        String response = execute(url, "", "application/json", "GET", entityId);
        return response;
    }

    public String addAgent(String requestBody, String entityId) {
        String url = "http://localhost:9004/api/v1/add";
        String response = execute(url, requestBody, "application/json", "POST",entityId);
        return response;
    }
    public String findAtmReversals(String userName, String entityId) {
        String url = "http://localhost:9004/atms/AtmReversals";
        var response = execute(url, "", "application/json", "GET",userName,entityId);
        System.out.println(response);
        return response;
    }
    public String findErrorLogs(String userName, String entityId) {
        String url = "http://localhost:9004/atms/ErrorLogs";
        var response = execute(url, "", "application/json", "GET",userName,entityId);
        System.out.println(response);
        return response;
    }

    public String findPosTransactions(String  entityId){
        String url = "http://localhost:9004/pos/get/all/transactions";
        String response = execute(url, "", "application/json", "GET", entityId);
        return response;

    }
}


