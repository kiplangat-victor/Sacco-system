package com.emtechhouse.accounts.Utils;

import com.emtechhouse.accounts.DTO.ChargeCollectionReq;
import com.emtechhouse.accounts.DTO.ContactInfo;
import com.emtechhouse.accounts.DTO.Member;
import com.emtechhouse.accounts.DTO.PartTran;
import com.emtechhouse.accounts.Models.Accounts.AccountDtos.AccountImageDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.EmailDto.MailDto;

import com.emtechhouse.accounts.Models.Accounts.AccountDtos.SmsDto;

//import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.Dtos.EmailDto;
//import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.Dtos.EmailDtoHolder;


import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ServiceCaller {
    @Value("${GET_CUSTOMER_PHONE_AND_EMAIL}")
    private String GET_CUSTOMER_PHONE_AND_EMAIL;

    @Value("${GET_CUSTOMER_DETAILS}")
    private String GET_CUSTOMER_DETAILS;



    @Value("${GET_USER_IMAGES}")
    private String img_url;

    @Value("${spring.application.service.system_config_coll_withdrawal}")
    private String system_config_coll_withdrawal;

    @Value("${spring.application.service.system_config_coll_activation_fee}")
    private String system_config_coll_activation_fee;


    @Value("${POST_MAIL_NOTIFICATION}")
    private String mail_notification;

    @Value("${SEND_SMS}")
    private String sms_notification;

    @Value("${SEND_SMS_EMT}")
    private String sms_notification_emt;

    @Value("${spring.application.service.system_config_money_transfer}")
    private String system_config_money_transfer;

    @Value("${spring.application.service.system_config_balance_enquiry}")
    private String system_config_balance_enquiry;

    @Value("${spring.application.service.system_config_sms_charges}")
    private String system_config_sms_charges;

    private final ObjectMapper objectMapper;

    public ServiceCaller(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


public EntityResponse getWithdrawalCharges(List<ChargeCollectionReq> chargeCollectionReqs){
    try {
//        log.info("getting withdrawal fee info");
//        System.out.println(Arrays.deepToString(chargeCollectionReqs.toArray()));
        String userName=UserRequestContext.getCurrentUser();
        String entityId=EntityRequestContext.getCurrentEntityId();
        if(userName==null){
            userName=CONSTANTS.SYSTEM_USERNAME;
            entityId=CONSTANTS.SYSTEM_ENTITY;
        }
        EntityResponse entityResponse= new EntityResponse<>();
        String url= system_config_coll_withdrawal;
//        log.info("url:="+url);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();
        Gson g = new Gson();
        String chargeCollectionReqsSTR = g.toJson(chargeCollectionReqs);
//        System.out.println("----------------------------------");
//        System.out.println(chargeCollectionReqsSTR);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), chargeCollectionReqsSTR);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("userName", userName)
                .addHeader("entityId", entityId)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        String res = response.body().string();
//        System.out.println("res is "+res);
        Boolean isJSONValid= isJSONValid(res);
        if(isJSONValid){
            JSONObject etyResponse = new JSONObject(res);
            String entityStatusCode= etyResponse.get("statusCode").toString();
            if(entityStatusCode.equals(String.valueOf(HttpStatus.FOUND.value()))){
                JSONArray entity =etyResponse.getJSONArray("entity");
                PartTran[] ld = objectMapper.readValue(entity.toString(),
                        PartTran[].class);
                List<PartTran> list = new ArrayList<>(Arrays.asList(ld));
                entityResponse.setMessage(HttpStatus.FOUND.getReasonPhrase());
                entityResponse.setStatusCode(HttpStatus.FOUND.value());
                entityResponse.setEntity(list);
            }
            else if (entityStatusCode.equals(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()))) {
                log.info("------------------------>Insufficient fund for withdrawal");
                entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                entityResponse.setMessage(etyResponse.get("message").toString());
            }
            else {
                log.info("------------------------>CHARGE NOT FOUND");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("CHARGE NOT FOUND");
            }
        }else {
            log.info("------------------------>INVALID JSON");
            entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            entityResponse.setMessage("FAILED TO FETCH CHARGES: ");

        }
//        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
//        System.out.println(entityResponse);
        return entityResponse;
    }catch (Exception e) {
        log.info("Caught Error {}"+e);

        EntityResponse response = new EntityResponse();
        response.setMessage(e.getLocalizedMessage());
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setEntity(e.getCause());
        return response;
    }
}

    public EntityResponse getActivationCharges (List<ChargeCollectionReq> chargeCollectionReqs){
        try {
            log.info("getting withdrawal fee info");
            System.out.println(Arrays.deepToString(chargeCollectionReqs.toArray()));
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
            String url= system_config_coll_activation_fee;
            log.info("url:="+url);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Gson g = new Gson();
            String chargeCollectionReqsSTR = g.toJson(chargeCollectionReqs);
            System.out.println("----------------------------------");
            System.out.println(chargeCollectionReqsSTR);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), chargeCollectionReqsSTR);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("userName", userName)
                    .addHeader("entityId", entityId)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println("res is "+res);
            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                if(entityStatusCode.equals(String.valueOf(HttpStatus.FOUND.value()))){
                    JSONArray entity =etyResponse.getJSONArray("entity");
                    PartTran[] ld = objectMapper.readValue(entity.toString(),
                            PartTran[].class);
                    List<PartTran> list = new ArrayList<>(Arrays.asList(ld));
                    entityResponse.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setEntity(list);
                }
                else if (entityStatusCode.equals(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()))) {
                    log.info("------------------------>Insufficient fund for withdrawal");
                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    entityResponse.setMessage(etyResponse.get("message").toString());
                }
                else {
                    log.info("------------------------>CHARGE NOT FOUND");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("CHARGE NOT FOUND");
                }
            }else {
                log.info("------------------------>INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED TO FETCH CHARGES: ");

            }
//        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
//        System.out.println(entityResponse);
            return entityResponse;
        }catch (Exception e){
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }



    public EntityResponse getMoneyTransferCharges (List<ChargeCollectionReq> chargeCollectionReqs){
        try {
            log.info("getting withdrawal fee info");
            System.out.println(Arrays.deepToString(chargeCollectionReqs.toArray()));
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
            String url= system_config_money_transfer;
            log.info("url:="+url);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Gson g = new Gson();
            String chargeCollectionReqsSTR = g.toJson(chargeCollectionReqs);
            System.out.println("----------------------------------");
            System.out.println(chargeCollectionReqsSTR);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), chargeCollectionReqsSTR);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("userName", userName)
                    .addHeader("entityId", entityId)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println("res is "+res);
            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                if(entityStatusCode.equals(String.valueOf(HttpStatus.FOUND.value()))){
                    JSONArray entity =etyResponse.getJSONArray("entity");
                    PartTran[] ld = objectMapper.readValue(entity.toString(),
                            PartTran[].class);
                    List<PartTran> list = new ArrayList<>(Arrays.asList(ld));
                    entityResponse.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setEntity(list);
                }
                else if (entityStatusCode.equals(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()))) {
                    log.info("------------------------>Insufficient fund for withdrawal");
                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    entityResponse.setMessage(etyResponse.get("message").toString());
                }
                else {
                    log.info("------------------------>CHARGE NOT FOUND");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("CHARGE NOT FOUND");
                }
            }else {
                log.info("------------------------>INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED TO FETCH CHARGES: ");

            }
//        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
//        System.out.println(entityResponse);
            return entityResponse;
        }catch (Exception e){
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }



    public EntityResponse getBalanceEnquiryCharges (List<ChargeCollectionReq> chargeCollectionReqs){
        try {
            log.info("getting balance enquiry fee info");
            System.out.println(Arrays.deepToString(chargeCollectionReqs.toArray()));
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
            String url= system_config_balance_enquiry;
            log.info("url:="+url);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Gson g = new Gson();
            String chargeCollectionReqsSTR = g.toJson(chargeCollectionReqs);
            System.out.println("----------------------------------");
            System.out.println(chargeCollectionReqsSTR);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), chargeCollectionReqsSTR);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("userName", userName)
                    .addHeader("entityId", entityId)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println("res is "+res);
            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                if(entityStatusCode.equals(String.valueOf(HttpStatus.FOUND.value()))){
                    JSONArray entity =etyResponse.getJSONArray("entity");
                    PartTran[] ld = objectMapper.readValue(entity.toString(),
                            PartTran[].class);
                    List<PartTran> list = new ArrayList<>(Arrays.asList(ld));
                    entityResponse.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setEntity(list);
                }
                else if (entityStatusCode.equals(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()))) {
                    log.info("------------------------>Insufficient fund for withdrawal");
                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    entityResponse.setMessage(etyResponse.get("message").toString());
                }
                else {
                    log.info("------------------------>CHARGE NOT FOUND");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("CHARGE NOT FOUND");
                }
            }else {
                log.info("------------------------>INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED TO FETCH CHARGES: ");

            }

            return entityResponse;
        }catch (Exception e){
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }



    public EntityResponse getSMSCharges (List<ChargeCollectionReq> chargeCollectionReqs){
        try {
            log.info("getting balance enquiry SMS fee info");
            System.out.println(Arrays.deepToString(chargeCollectionReqs.toArray()));
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
            String url= system_config_sms_charges;
            log.info("url:="+url);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Gson g = new Gson();
            String chargeCollectionReqsSTR = g.toJson(chargeCollectionReqs);
            System.out.println("----------------------------------");
            System.out.println(chargeCollectionReqsSTR);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), chargeCollectionReqsSTR);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("userName", userName)
                    .addHeader("entityId", entityId)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println("res is "+res);
            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                if(entityStatusCode.equals(String.valueOf(HttpStatus.FOUND.value()))){
                    JSONArray entity =etyResponse.getJSONArray("entity");
                    PartTran[] ld = objectMapper.readValue(entity.toString(),
                            PartTran[].class);
                    List<PartTran> list = new ArrayList<>(Arrays.asList(ld));
                    entityResponse.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setEntity(list);
                }
                else if (entityStatusCode.equals(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()))) {
                    log.info("------------------------>Insufficient fund for withdrawal");
                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    entityResponse.setMessage(etyResponse.get("message").toString());
                }
                else {
                    log.info("------------------------>CHARGE NOT FOUND");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("CHARGE NOT FOUND");
                }
            }else {
                log.info("------------------------>INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED TO FETCH CHARGES: ");

            }

            return entityResponse;
        }catch (Exception e){
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }




    public EntityResponse getCustomerImages(String customerCode){
        try {
            log.info("getting loan limits info");

            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName=CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
            String url= img_url;
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
//                log.info("entity-->"+entityStatusCode);
                if(entityStatusCode.equals(String.valueOf(HttpStatus.OK.value()))){
                    JSONArray entity =etyResponse.getJSONArray("entity");
                    AccountImageDto[] ld = objectMapper.readValue(entity.toString(),
                            AccountImageDto[].class);
                    List<AccountImageDto> list =new ArrayList<>(Arrays.asList(ld));
                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(list);
                    return entityResponse;
                }else {
                    log.info("---------> STATUS CODE");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED TO FETCH CUSTOMER DETAILS CUSTOMER CODE: "+customerCode);
                    return entityResponse;
                }

            }else {
                log.info("---------> INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED TO FETCH CUSTOMER DETAILS CUSTOMER CODE: "+customerCode);
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
    public EntityResponse getCustomerPhone_and_Email(String customerCode, String userName, String entityId ) throws IOException {
        try {
            EntityResponse entityResponse = new EntityResponse();
            String apiUrl= GET_CUSTOMER_PHONE_AND_EMAIL;
            List<Customerimages> customerimages = new ArrayList<>();
            String url = apiUrl+customerCode;
            OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(300, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .get().url(url).addHeader("userName", userName).addHeader("entityId", entityId).build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println(res);
            JSONObject jo = new JSONObject(res);
            Integer statusCode = jo.getInt("statusCode");
            System.out.println("-------------------------------------------" + statusCode);
            if (statusCode == Integer.parseInt("200")){
                JSONObject entity = jo.getJSONObject("entity");
//                String emailAddress = entity.getString("emailAddress");
//                String phoneNumber = entity.getString("phoneNumber");

                ContactInfo c = objectMapper.readValue(entity.toString(),
                        ContactInfo.class);

//                ContactInfo contactInfo = new ContactInfo();
//                contactInfo.setPhoneNumber(phoneNumber);
//                contactInfo.setEmailAddress(emailAddress);
                entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setEntity(c);
            }else{
                entityResponse.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setEntity("");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched an error {} "+e);
            return null;
        }

    }
    public EntityResponse getCustomerInfo(String customerCode, String userName, String entityId) throws IOException {
        EntityResponse entityResponse = new EntityResponse();
        String apiUrl= GET_CUSTOMER_DETAILS;
        List<Customerimages> customerimages = new ArrayList<>();
        String url = apiUrl+customerCode;
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(300, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .get().url(url).addHeader("userName", userName).addHeader("entityId", entityId).build();
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        JSONObject jo = new JSONObject(res);
        Integer statusCode = jo.getInt("statusCode");
        if (statusCode == Integer.parseInt("200")){
            JSONObject entity = jo.getJSONObject("entity");
            String CustomerName = entity.getString("CustomerName");
            Member member = new Member();
            member.setCustomerName(CustomerName);
            entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setEntity(member);
        }else{
            entityResponse.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            entityResponse.setEntity("");
        }
        return entityResponse;
    }


//    public EntityResponse sendTransactionReceipt(EmailDto mailDto){
//        CompletableFuture.runAsync(() ->{
//            try{
//                log.info("********************* Sending an email");
//                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                OkHttpClient client = new OkHttpClient();
//                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//                String json = ow.writeValueAsString(mailDto);
//                RequestBody body = RequestBody.create(json, JSON);
//
//
//
//                EntityResponse entityResponse= new EntityResponse<>();
//
//                String url= mail_notification;
//
//                log.info("url "+url);
//
//                Request request = new Request.Builder()
//                        .put(body)
//                        .url(url)
//                        .addHeader("userName", "String")
//                        .addHeader("entityId", "001")
//                        .build();
//                client.newCall(request).execute();
////            String res = response.body().string();
////            System.out.println("Email response ::" + res);
//
//                EntityResponse r = new EntityResponse();
//                r.setStatusCode(HttpStatus.OK.value());
////            r.setMessage(res);
////                return r;
//
//            }catch (Exception e){
//                log.info("*********************error");
//                log.info("Caught Error {}"+e.getMessage());
//
//                EntityResponse response = new EntityResponse();
//                response.setMessage(e.getLocalizedMessage());
//                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//                response.setEntity(e.getCause());
////                return response;
//            }
//        });
//        return new EntityResponse<>("sss",null,200);
//    }


//    public EntityResponse sendTransactionReceipt1(EmailDtoHolder m){
//        CompletableFuture.runAsync(() ->{
//            try{
//                log.info("********************* Sending an email");
//                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                OkHttpClient client = new OkHttpClient();
//                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//                String json = ow.writeValueAsString(m);
//                RequestBody body = RequestBody.create(json, JSON);
//
//
//
//                EntityResponse entityResponse= new EntityResponse<>();
//
//                String url= mail_notification;
//
//                log.info("url "+url);
//
//                Request request = new Request.Builder()
//                        .put(body)
//                        .url(url)
//                        .addHeader("userName", "String")
//                        .addHeader("entityId", "001")
//                        .build();
//                client.newCall(request).execute();
////            String res = response.body().string();
////            System.out.println("Email response ::" + res);
//
//                EntityResponse r = new EntityResponse();
//                r.setStatusCode(HttpStatus.OK.value());
////            r.setMessage(res);
////                return r;
//
//            }catch (Exception e){
//                log.info("*********************error");
//                log.info("Caught Error {}"+e.getMessage());
//
//                EntityResponse response = new EntityResponse();
//                response.setMessage(e.getLocalizedMessage());
//                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//                response.setEntity(e.getCause());
////                return response;
//            }
//        });
//        return new EntityResponse<>("sss",null,200);
//    }

    public EntityResponse sendSms(SmsDto dto){
        CompletableFuture.runAsync(() ->{
            try{
                log.info("*********One************ Sending an sms");
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(dto);
                RequestBody body = RequestBody.create(json, JSON);



                EntityResponse entityResponse= new EntityResponse<>();

                String url= sms_notification;

                log.info("url "+url);

                Request request = new Request.Builder()
                        .post(body)
                        .url(url)
                        .addHeader("userName", "String")
                        .addHeader("entityId", "001")
                        .build();
                client.newCall(request).execute();
//                Response response = client.newCall(request).execute();
//            String res = response.body().string();
//            System.out.println("Email response ::" + res);

                EntityResponse r = new EntityResponse();
                r.setStatusCode(HttpStatus.OK.value());
//            r.setMessage(res);
//                return r;

            }catch (Exception e){
                log.info("*********************error");
                log.info("Caught Error {}"+e.getMessage());

                EntityResponse response = new EntityResponse();
                response.setMessage(e.getLocalizedMessage());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setEntity(e.getCause());
//                return response;
            }
        });
        return new EntityResponse<>("sss",null,200);
    }

    public EntityResponse sendSmsEmt(SmsDto dto) {
        CompletableFuture.runAsync(() ->{
            try{
                log.info("**********Two*********** Sending an sms");
                System.out.println(dto.toString());
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(dto);
                RequestBody body = RequestBody.create(json, JSON);
                EntityResponse entityResponse= new EntityResponse<>();

                String url= sms_notification_emt;

                log.info("url "+url);

                Request request = new Request.Builder()
                        .post(body)
                        .url(url)
                        .addHeader("userName", "String")
                        .addHeader("entityId", "001")
                        .build();
                client.newCall(request).execute();
//                Response response = client.newCall(request).execute();
//            String res = response.body().string();
//            System.out.println("Email response ::" + res);

                EntityResponse r = new EntityResponse();
                r.setStatusCode(HttpStatus.OK.value());
//            r.setMessage(res);
//                return r;

            }catch (Exception e){
                log.info("*********************error");
                log.info("Caught Error {}"+e.getMessage());

                EntityResponse response = new EntityResponse();
                response.setMessage(e.getLocalizedMessage());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setEntity(e.getCause());
//                return response;
            }
        });
        return new EntityResponse<>("sss",null,200);
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
}

