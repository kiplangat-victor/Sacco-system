package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.CollateralDto.CollateralDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.EmailDto.MailDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.LoanLimitDto.LoanLimitDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails.LoanAccountProduct;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails.SavingsAccountProduct;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails.TdaProdDto;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ItemServiceCaller {

    @Value("${LOAN_PRODUCT}")
    String loan_url;
    @Value("${SAVINGS_PRODUCT}")
    String saving_url;

    @Value("${TDA_PRODUCT}")
    String tda_product;

    @Value("${TDA_PRODUCT_SPECIFIC_DETAILS}")
    String tda_specific_details;

    @Value("${LOAN_LIMITS}")
    String loan_limit_url;

    @Value("${AUTO_CREATED_PRODUCTS}")
    String auto_created_products;

    @Value("${GET_COLLATERAL}")
    String collateral_details;

    @Value("${CREATE_OTP}")
    String collateral_otp;

    @Value("${VERIFY_OTP}")
    String verify_collateral_otp;

    @Value("${SEND_EMAIL}")
    String send_email;

    @Value("${GET_CUSTOMER_EMAIL}")
    String get_customer_email;

    @Value("${GET_ACCOUNT_STATEMENT}")
    String customer_account_statement;

    private final ObjectMapper objectMapper;

    public ItemServiceCaller(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public EntityResponse getGeneralProductDetail1(String productCode, String productType) {
        try{
            log.info("*********************got called to fetch product details");
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            log.info("*********************got called to fetch product details");
            EntityResponse entityResponse= new EntityResponse<>();
            String url= loan_url+productCode;
            if(productType.equals(CONSTANTS.LOAN_ACCOUNT)){
//                url= URLS.LOAN_PRODUCT+productCode;
                url= loan_url+productCode;
            }else if (productType.equals(CONSTANTS.SAVINGS_ACCOUNT)){
//                url= URLS.SAVINGS_PRODUCT+productCode;
                url= saving_url+productCode;
            }else {
                url= tda_product+productCode;
            }
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", "String")
                    .addHeader("entityId", "001")
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
//            System.out.println("Product response ::" + res);

            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                log.info("prod item json valid");
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                log.info("entity-->"+entityStatusCode);
                if(!entityStatusCode.equals("404")){
                    log.info("status code: "+ entityStatusCode);
                    JSONObject genProdDetails = etyResponse.getJSONObject("entity");

                    GeneralProductDetails generalProductDetails= objectMapper.readValue(genProdDetails.toString(),
                            GeneralProductDetails.class);

                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(generalProductDetails);
                    return entityResponse;
                }else {
                    log.info("---------> INVALID JSON");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED PRODUCT, PRODUCT CODE: "+productCode);
                    return entityResponse;
                }
            }else {
                log.info("---------> INVALID JSON");
//                System.out.println("Account response ::" + res);
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED PRODUCT, PRODUCT CODE: "+productCode);
                return entityResponse;
            }

        }catch (Exception e){
            log.info("*********************error");
            log.info("Caught Error {}"+e.getMessage());

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    public EntityResponse getAutoCreatedAccountProducts(String productCode){
        try{
            log.info("********************* getting auto created accounts products");
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
            String url= auto_created_products+productCode;
            log.info("url "+url);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", "String")
                    .addHeader("entityId", "001")
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
//            System.out.println("Product response ::" + res);

            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                log.info("prod item json valid");
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                Integer statusCode= Integer.valueOf(entityStatusCode);
                log.info("entity-->"+entityStatusCode);
                if(statusCode==HttpStatus.FOUND.value()){
                    log.info("status code: "+ entityStatusCode);
                    JSONArray entity = etyResponse.getJSONArray("entity");

                    AutoAddedAccountDto[] a = objectMapper.readValue(entity.toString(),
                            AutoAddedAccountDto[].class);
                    List<AutoAddedAccountDto> list =new ArrayList<>(Arrays.asList(a));

                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(list);
                    return entityResponse;
                }else {
                    log.info("---------> INVALID JSON");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED PRODUCT, PRODUCT CODE: "+productCode);
                    return entityResponse;
                }
            }else {
                log.info("---------> INVALID JSON");
//                System.out.println("Account response ::" + res);
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED PRODUCT, PRODUCT CODE: "+productCode);
                return entityResponse;
            }

        }catch (Exception e){
            log.info("*********************error");
            log.info("Caught Error {}"+e.getMessage());

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    public EntityResponse getLoanAccountProductDetails(String prodCode){
        try {
            log.info("get loan accounts loan details");

            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
//            String url= URLS.LOAN_PRODUCT+prodCode;
            String url= loan_url+prodCode;
            log.info("url:="+url);
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
//            System.out.println("Product response ::" + res);
            Boolean isJSONValid= isJSONValid(res);

            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                log.info("entity-->"+entityStatusCode);
                if(!entityStatusCode.equals("404")){
                    JSONObject genProdDetails = etyResponse.getJSONObject("entity");
//                    System.out.println("json response ::" + genProdDetails);

                    String LaaProdDetails= genProdDetails.get("laaDetails").toString();
                    Boolean isLaaJsonValid= isJSONValid(LaaProdDetails);
                    if(isLaaJsonValid){
                        JSONObject loanProdDetails = genProdDetails.getJSONObject("laaDetails");
                        LoanAccountProduct loanAccountProduct =new LoanAccountProduct();

                        loanAccountProduct= objectMapper.readValue(loanProdDetails.toString(),
                                LoanAccountProduct.class);

                        entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                        entityResponse.setStatusCode(HttpStatus.OK.value());
                        entityResponse.setEntity(loanAccountProduct);
                        return entityResponse;
                    }else {
                        entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                        entityResponse.setMessage("FAILED! PRODUCT IS NOT A LOAN PRODUCT, PRODUCT CODE: "+prodCode);
                        return entityResponse;
                    }
                }else {
                    log.info("---------> INVALID JSON");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED PRODUCT, PRODUCT CODE: "+prodCode);
                    return entityResponse;
                }


            }else {
                log.info("---------> INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED PRODUCT, PRODUCT CODE: "+prodCode);
                return entityResponse;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    public EntityResponse getSavingsAccountProductDetails(String prodCode){
        try {

            log.info("url");
            log.info("url :"+saving_url);

            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
//            String url= URLS.SAVINGS_PRODUCT+prodCode;
            String url= saving_url+prodCode;
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
//            System.out.println("Product response ::" + res);
            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                log.info("entity-->"+entityStatusCode);
                if(!entityStatusCode.equals("404")){
                    JSONObject genProdDetails = etyResponse.getJSONObject("entity");
//                    System.out.println("json response ::" + genProdDetails);

                    String sbaProdDetails= genProdDetails.get("sbaDetails").toString();
                    System.out.println("sbaDetails ::" + sbaProdDetails);
                    Boolean isSbaJsonValid= isJSONValid(sbaProdDetails);
                    if(isSbaJsonValid){
                        JSONObject savingsProdDetails = genProdDetails.getJSONObject("sbaDetails");
                        SavingsAccountProduct savingsAccountProduct = new SavingsAccountProduct();
                        savingsAccountProduct= objectMapper.readValue(savingsProdDetails.toString(),
                                SavingsAccountProduct.class);

                        System.out.println(savingsAccountProduct);

                        entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                        entityResponse.setStatusCode(HttpStatus.OK.value());
                        entityResponse.setEntity(savingsAccountProduct);
                        return entityResponse;
                    }else {
                        entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                        entityResponse.setMessage("FAILED! PRODUCT IS NOT A SBA PRODUCT, PRODUCT CODE: "+prodCode);
                        return entityResponse;
                    }
                }else {
                    log.info("---------> INVALID JSON");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED PRODUCT, PRODUCT CODE: "+prodCode);
                    return entityResponse;
                }
            }else {
                log.info("---------> INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED PRODUCT, PRODUCT CODE: "+prodCode);
                return entityResponse;
            }

        }catch (Exception e){
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    public boolean isJSONValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            try {
                new JSONArray(json);
            } catch (JSONException ne) {
                return false;
            }
        }
        return true;
    }

    public EntityResponse getLoanLimitsDetails(String prodCode){
        try {
            log.info("getting loan limits info");

            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
            String url= loan_limit_url+prodCode;
            log.info("url:="+url);
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
//            System.out.println("Product response ::" + res);
            Boolean isJSONValid= isJSONValid(res);

            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                log.info("entity-->"+entityStatusCode);
                if(entityStatusCode.equals(String.valueOf(HttpStatus.FOUND.value()))){
                    JSONArray entity =etyResponse.getJSONArray("entity");

                    LoanLimitDto[] ld = objectMapper.readValue(entity.toString(),
                           LoanLimitDto[].class);
                    List<LoanLimitDto> list =new ArrayList<>(Arrays.asList(ld));

                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(list);
                    return entityResponse;

                }else {
                    log.info("---------> STATUS CODE");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED TO FETCH LOAN LIMITS DETAILS: "+prodCode);
                    return entityResponse;
                }

            }else {
                log.info("---------> INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED TO FETCH LOAN LIMITS DETAILS: "+prodCode);
                return entityResponse;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    public EntityResponse getTdaSpecificDetails(String prodCode){
        try {
            log.info("getting loan limits info");

            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
            String url= tda_specific_details+prodCode;
            log.info("url:="+url);
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
//            System.out.println("Product response ::" + res);
            Boolean isJSONValid= isJSONValid(res);

            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                log.info("entity-->"+entityStatusCode);
                if(entityStatusCode.equals(String.valueOf(HttpStatus.FOUND.value()))){
                    JSONObject entity =etyResponse.getJSONObject("entity");

                    TdaProdDto t= objectMapper.readValue(entity.toString(),
                            TdaProdDto.class);

                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(t);
                    return entityResponse;

                }else {
                    log.info("---------> STATUS CODE");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED TO FETCH TDA DETAILS FOR PRODUCT: "+prodCode);
                    return entityResponse;
                }

            }else {
                log.info("---------> INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED TO FETCH TDA DETAILS FOR PRODUCT: "+prodCode);
                return entityResponse;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    public EntityResponse getCollateralDetails(String collateralCode){
        try{
            log.info("********************* getting collateral details");
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
            String url= collateral_details+collateralCode;
            log.info("url "+url);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", "String")
                    .addHeader("entityId", "001")
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
//            System.out.println("Product response ::" + res);

            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                log.info("prod item json valid");
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                Integer statusCode= Integer.valueOf(entityStatusCode);
                log.info("entity-->"+entityStatusCode);
                if(statusCode==HttpStatus.FOUND.value()){
                    log.info("status code: "+ entityStatusCode);
                    JSONObject entity = etyResponse.getJSONObject("entity");

                    CollateralDto c = objectMapper.readValue(entity.toString(),
                            CollateralDto.class);


                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(c);
                    return entityResponse;
                }else {
                    log.info("---------> INVALID JSON");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED COLLATERAL OF CODE: "+collateralCode);
                    return entityResponse;
                }
            }else {
                log.info("---------> INVALID JSON");
//                System.out.println("Account response ::" + res);
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED COLLATERAL OF CODE: "+collateralCode);
                return entityResponse;
            }

        }catch (Exception e){
            log.info("*********************error");
            log.info("Caught Error {}"+e.getMessage());

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    public EntityResponse sendCollateralOtp(String collateralCode, String email){
        try{
            log.info("********************* getting collateral details");
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
            String url= collateral_otp+"email="+email+"&collateralCode="+collateralCode;
            log.info("url "+url);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            RequestBody formBody = new FormBody.Builder()

                    .build();

            Request request = new Request.Builder()
                    .post(formBody)
                    .url(url)
                    .addHeader("userName", "String")
                    .addHeader("entityId", "001")
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
//            System.out.println("Product response ::" + res);

            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                log.info("prod item json valid");
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                Integer statusCode= Integer.valueOf(entityStatusCode);
                log.info("entity-->"+entityStatusCode);
                if(statusCode==HttpStatus.CREATED.value()){
                    String message= etyResponse.get("message").toString();

                    entityResponse.setMessage(message);
                    entityResponse.setStatusCode(HttpStatus.OK.value());
//                    entityResponse.setEntity(c);
                    return entityResponse;
                }else {
                    log.info("---------> INVALID JSON");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED COLLATERAL OF CODE: "+collateralCode);
                    return entityResponse;
                }
            }else {
                log.info("---------> INVALID JSON");
//                System.out.println("Account response ::" + res);
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED COLLATERAL OF CODE: "+collateralCode);
                return entityResponse;
            }

        }catch (Exception e){
            log.info("*********************error");
            log.info("Caught Error {}"+e.getMessage());

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    public EntityResponse sendEmail(MailDto mailDto){
        try{
            log.info("********************* Sending an email");
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(mailDto);
            RequestBody body = RequestBody.create(json, JSON);



            EntityResponse entityResponse= new EntityResponse<>();

            String url= send_email;

            log.info("url "+url);

            Request request = new Request.Builder()
                    .post(body)
                    .url(url)
                    .addHeader("userName", "String")
                    .addHeader("entityId", "001")
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
//            System.out.println("Email response ::" + res);

            EntityResponse r = new EntityResponse();
            r.setStatusCode(HttpStatus.OK.value());
            r.setMessage(res);
            return r;

        }catch (Exception e){
            log.info("*********************error");
            log.info("Caught Error {}"+e.getMessage());

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    public EntityResponse verifyCollateralOtp(String collateralCode, Integer otpCode){
        try{
            log.info("********************* getting collateral details");
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();

            String url= verify_collateral_otp+"collateralCode="+collateralCode+"&otpCode="+otpCode;

            log.info("url "+url);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();


            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", "String")
                    .addHeader("entityId", "001")
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
//            System.out.println("Product response ::" + res);

            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                log.info("prod item json valid");
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                Integer statusCode= Integer.valueOf(entityStatusCode);
                log.info("entity-->"+entityStatusCode);
//                if(statusCode==HttpStatus.OK.value()){
                String message= etyResponse.get("message").toString();

                entityResponse.setMessage(message);
                entityResponse.setStatusCode(HttpStatus.OK.value());
//                    entityResponse.setEntity(c);
                return entityResponse;
//                }else {
//                    log.info("---------> INVALID JSON");
//                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
//                    entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED COLLATERAL OF CODE: "+collateralCode);
//                    return entityResponse;
//                }
            }else {
                log.info("---------> INVALID JSON");
//                System.out.println("Account response ::" + res);
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED COLLATERAL OF CODE: "+collateralCode);
                return entityResponse;
            }

        }catch (Exception e){
            log.info("*********************error");
            log.info("Caught Error {}"+e.getMessage());

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    public EntityResponse getCustomerEmail(String customerCode){
        try{
            log.info("********************* getting customer email");
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();

            String url= get_customer_email+customerCode;

            log.info("url "+url);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();


            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", "String")
                    .addHeader("entityId", "001")
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
//            System.out.println("Product response ::" + res);

            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                log.info(" item json valid");
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                Integer statusCode= Integer.valueOf(entityStatusCode);
                log.info("entity-->"+entityStatusCode);
                if(statusCode==HttpStatus.FOUND.value()){

                    JSONObject entity = etyResponse.getJSONObject("entity");
                    String email = entity.get("emailAddress").toString();

                    String message= etyResponse.get("message").toString();
                    entityResponse.setMessage(message);
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(email);
                    return entityResponse;
                }else {
                    log.info("---------> Not foud");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND CUSTOMERS EMAIL");
                    return entityResponse;
                }
            }else {
                log.info("---------> INVALID JSON");
//                System.out.println("Account response ::" + res);
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND CUSTOMERS EMAIL");
                return entityResponse;
            }

        }catch (Exception e){
            log.info("*********************error");
            log.info("Caught Error {}"+e.getMessage());

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }

    // TODO: 3/28/2023 Connect to the reports service

    public EntityResponse getAccountStatement(String acid, String fromDate, String toDate){
        try{
            log.info("********************* getting account statement");
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();

            String url= customer_account_statement+"acid="+acid+"&fromdate="+fromDate+"&todate="+toDate;

            log.info("url "+url);

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();


            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", "String")
                    .addHeader("entityId", "001")
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
//            System.out.println("Reports response ::" + res);

            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                log.info(" item json valid");
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                Integer statusCode= Integer.valueOf(entityStatusCode);
                log.info("entity-->"+entityStatusCode);
                if(statusCode==HttpStatus.FOUND.value()){

                    byte[] data  = etyResponse.getString("entity").getBytes();

                    String message= etyResponse.get("message").toString();
                    entityResponse.setMessage(message);
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(data);
                    return entityResponse;
                }else {
                    log.info("---------> Not found");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND REPORT");
                    return entityResponse;
                }
            }else {
                log.info("---------> INVALID JSON");
//                System.out.println("Account response ::" + res);
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND REPORT");
                return entityResponse;
            }

        }catch (Exception e){
            log.info("*********************error");
            log.info("Caught Error {}"+e.getMessage());

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }


//    public EntityResponse getLoanInterest(String prodCode, Double principalAmount){
//        try {
//            String userName=UserRequestContext.getCurrentUser();
//            String entityId=EntityRequestContext.getCurrentEntityId();
//            if(userName==null){
//                userName=CONSTANTS.SYSTEM_USERNAME;
//                entityId=CONSTANTS.SYSTEM_ENTITY;
//            }
//            EntityResponse entityResponse= new EntityResponse<>();
//            String url= URLS.GET_LOAN_INTEREST_RATE+principalAmount+"&productCode="+prodCode;
//            OkHttpClient client = new OkHttpClient().newBuilder()
//                    .connectTimeout(100, TimeUnit.SECONDS)
//                    .readTimeout(300, TimeUnit.SECONDS)
//                    .build();
//            Request request = new Request.Builder()
//                    .get()
//                    .url(url)
//                    .addHeader("userName", userName)
//                    .addHeader("entityId", entityId)
//                    .build();
//            Response response = client.newCall(request).execute();
//            String res = response.body().string();
//            System.out.println("Interest response ::" + res);
//            Boolean isJSONValid= isJSONValid(res);
//            if(isJSONValid){
//                JSONObject etyResponse = new JSONObject(res);
//                Integer statusCode= Integer.valueOf(etyResponse.get("statusCodeValue").toString());
//                if(statusCode.equals(200)){
//                    String body=etyResponse.get("body").toString();
//                    Boolean isBodyJSONValid= isJSONValid(body);
//                    if(isBodyJSONValid){
//                        JSONObject bodyResponse =etyResponse.getJSONObject("body");
//                        Double interest = Double.valueOf(bodyResponse.get("entity").toString());
//
//                        entityResponse.setStatusCode(HttpStatus.OK.value());
//                        entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
//                        entityResponse.setEntity(interest);
//                        return entityResponse;
//                    }else {
//                        entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
//                        entityResponse.setMessage("WRONG RESPONSE! COULD NOT FIND THE ATTACHED INTEREST RATE: "+prodCode);
//                        return entityResponse;
//                    }
//                    //
//                }else {
//                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
//                    entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED INTEREST RATE: "+prodCode);
//                    return entityResponse;
//                }
//            }else {
//                log.info("---------> INVALID JSON");
//                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
//                entityResponse.setMessage("API ERROR! COULD NOT FIND THE ATTACHED INTEREST RATE: "+prodCode);
//                return entityResponse;
//            }
//        }catch (Exception e){
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }
}
