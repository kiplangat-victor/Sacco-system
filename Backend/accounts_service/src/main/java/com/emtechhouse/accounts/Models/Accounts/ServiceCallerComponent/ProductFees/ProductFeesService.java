package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees;

import com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees.FeeAmountRecur;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
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
public class ProductFeesService {
    @Value("${LOAN_PRODUCT}")
    String loan_url;
    @Value("${GET_SYSTEM_CONFIG_SERVICE}")
    String system_config_url;

    private final ObjectMapper objectMapper;

    public ProductFeesService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public FeeAmountRecur getOneProductFeeRecurring(String prodCode, Double amount, String eventId) {
        try {
            String url= loan_url+prodCode;
            System.out.println(url);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", "SYSTEM")
                    .addHeader("entityId", "001")
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();

            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                JSONObject genProdDetails = etyResponse.getJSONObject("entity");
                JSONArray fees = genProdDetails.getJSONArray("fees");
                if(fees.isEmpty()) {
                    return null;
                } else {
                    FeeAmountRecur feeAmountRecur = new FeeAmountRecur();
                    System.out.println("-----product fees---------");
                    fees.forEach(fee-> {
//                        System.out.println(fee);
                        System.out.println(eventId);
                        JSONObject feeObject= (JSONObject) fee;
                        String event_id_code = feeObject.getString("eventIdCode");
                        if (event_id_code.equalsIgnoreCase(eventId)) {
                            EntityResponse feeEntityResponse= getFeeAmount(event_id_code, amount, "SYSTEM", "001");
//                            System.out.println(feeEntityResponse.getEntity());
                            ProductFeeAmount productFeeAmount= (ProductFeeAmount) feeEntityResponse.getEntity();
                            feeAmountRecur.setRecurPeriod(productFeeAmount.chargeAnnualFee==null || productFeeAmount.chargeAnnualFee == 'N'? productFeeAmount.chargeMonthlyFee==null || productFeeAmount.chargeMonthlyFee == 'N'? "" : "MONTHLY": "YEARS");

                            String recurEventId = null;
                            if (feeAmountRecur.getRecurPeriod().equalsIgnoreCase("months")){
                                recurEventId = productFeeAmount.monthlyEventId;
                            }else if(feeAmountRecur.getRecurPeriod().equalsIgnoreCase("years")) {
                                recurEventId = productFeeAmount.annualEventId;
                            }
                            if (recurEventId != null) {
                                feeEntityResponse= getFeeAmount(recurEventId, amount, "SYSTEM", "001");
//                                System.out.println(feeEntityResponse.getEntity());
                                productFeeAmount= (ProductFeeAmount) feeEntityResponse.getEntity();
                                feeAmountRecur.setAmount(productFeeAmount.getInitialAmt());
                            } else {
                                feeAmountRecur.setAmount(0.0);
                            }
                        }
                    });

                    return feeAmountRecur;
                }
            } else {
                log.info("---------> INVALID JSON");
                return null;
            }

        }catch (Exception e){
            System.out.println("Could not get product fees");
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return null;
        }
    }

    public EntityResponse getProductFees(String prodCode, Double amount) {
        try {
            EntityResponse entityResponse= new EntityResponse<>();
//            String url= URLS.LOAN_PRODUCT+prodCode;
            String url= loan_url+prodCode;
            System.out.println(url);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("userName", UserRequestContext.getCurrentUser())
                    .addHeader("entityId", EntityRequestContext.getCurrentEntityId())
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println("Fees response :: "+res);
            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);

                JSONObject genProdDetails = etyResponse.getJSONObject("entity");
                List<ProductFeeAmount> productFeeAmountList = new ArrayList<>();
                JSONArray fees = genProdDetails.getJSONArray("fees");
                if(fees.isEmpty()){
                    entityResponse.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return entityResponse;
                } else {
                    fees.forEach(fee-> {
                        JSONObject feeObject= (JSONObject) fee;
                        String event_id_code = feeObject.getString("eventIdCode");
                        EntityResponse feeEntityResponse= getFeeAmount(event_id_code, amount);
                        ProductFeeAmount productFeeAmount= (ProductFeeAmount) feeEntityResponse.getEntity();
                        productFeeAmountList.add(productFeeAmount);
                    });

                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(productFeeAmountList);
                    return entityResponse;
                }
            } else {
                log.info("---------> INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED PRODUCT");
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

    public EntityResponse getFeeAmount(String event_id_code, Double totalAmount) {
        return getFeeAmount(event_id_code, totalAmount,  UserRequestContext.getCurrentUser(), EntityRequestContext.getCurrentEntityId());
    }


    //todo: getting fee amount from params
    public EntityResponse getFeeAmount(String event_id_code, Double totalAmount, String username, String entity) {
        try {
            EntityResponse entityResponse = new EntityResponse<>();
//            String url = URLS.GET_SYSTEM_CONFIG_SERVICE + "find/by/code/" + event_id_code;
            String url = system_config_url + "find/by/code/" + event_id_code;
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            Request request = new Request.Builder().get().url(url)
//                .addHeader("Accept",mediaType.toString())
                    .addHeader("userName", username).addHeader("entityId", entity).build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject jsonObject = new JSONObject(res);
                if (!jsonObject.get("entity").toString().equals("null")) {
                    log.info("fees not null:");
                    JSONObject jsonData = jsonObject.getJSONObject("entity");

//                    System.out.println(jsonData);
                    String chargeMonthlyFee = null;
                    if (!jsonData.isNull("chargeMonthlyFee"))
                        chargeMonthlyFee = jsonData.getString("chargeMonthlyFee");
                    String monthlyEventId = null;
                    if (!jsonData.isNull("monthlyEventId"))
                        monthlyEventId = jsonData.getString("monthlyEventId");
                    String chargeAnnualFee = null;
                    if (!jsonData.isNull("chargeAnnualFee"))
                        chargeAnnualFee = jsonData.getString("chargeAnnualFee");
                    String annualEventId = null;
                    if (!jsonData.isNull("annualEventId"))
                        annualEventId = jsonData.getString("annualEventId");

                    String charge_coll_ac = jsonData.getString("ac_placeholder");
                    String amt_derivation_type = jsonData.getString("amt_derivation_type");
                    Double min_amt = jsonData.getDouble("min_amt");
                    Double max_amt = jsonData.getDouble("max_amt");
                    Double amt = jsonData.getDouble("amt");
                    Double percentage = jsonData.getDouble("percentage");
                    Double monthlyAmount=jsonData.getDouble("monthlyFee");
                    String event_type_desc=jsonData.getString("event_type_desc");
                    String event_id_desc=jsonData.getString("event_id_desc");

                    Double chargeAmount = 0.00;

                    if (amt_derivation_type.equalsIgnoreCase("FIXED")) {
                        chargeAmount = 0.00;
                        if (amt < min_amt) {
                            chargeAmount = min_amt;
                        } else if (amt > max_amt) {
                            chargeAmount = max_amt;
                        } else {
                            chargeAmount = amt;
                        }
                    }else if (amt_derivation_type.equalsIgnoreCase("PCNT")) {
                        chargeAmount = 0.00;
                        Double percentageAmt = (percentage / 100) * totalAmount;
                        if (percentageAmt < min_amt) {
                            chargeAmount = min_amt;
                        } else if (percentageAmt > max_amt) {
                            chargeAmount = max_amt;
                        } else {
                            chargeAmount = percentageAmt;
                        }
                    }else {
                        chargeAmount=0.0;
                    }

                    ProductFeeAmount productFeeAmount= new ProductFeeAmount();
                    productFeeAmount.setInitialAmt(chargeAmount);
                    productFeeAmount.setEventIdCode(event_id_code);
                    productFeeAmount.setEventTypeDesc(event_id_desc);
                    productFeeAmount.setChargeCollectionAccount(charge_coll_ac);
                    productFeeAmount.setMonthlyAmt(monthlyAmount);

                    productFeeAmount.setAnnualEventId(annualEventId);
                    productFeeAmount.setChargeAnnualFee(nullOrChar(chargeAnnualFee));
                    productFeeAmount.setMonthlyEventId(monthlyEventId);
                    productFeeAmount.setChargeMonthlyFee(nullOrChar(chargeMonthlyFee));


                    entityResponse.setEntity(productFeeAmount);
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    return entityResponse;
                }else{
                    log.info("fees null:");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    return entityResponse;
                }
            }else{
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("WRONG_ENTITY_JSON");
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

    private Character nullOrChar(String chargeMonthlyFee) {
        if (chargeMonthlyFee == null || chargeMonthlyFee.isEmpty())   return null;
        return chargeMonthlyFee.charAt(0);
    }

    //SUM ALL PRODUCT FEES
    public Double getTotalProductFees(String prodCode, Double amount){
        Double totalProductFees=0.0;
        try{
            EntityResponse productEntityResponse=getProductFees(prodCode, amount);
            List<ProductFeeAmount> productFeeAmountList= (List<ProductFeeAmount>) productEntityResponse.getEntity();
            if(productFeeAmountList.size()>0){
                for(Integer i=0;i<productFeeAmountList.size();i++){
                    totalProductFees=productFeeAmountList.get(i).getInitialAmt();
                }
            }else {
                totalProductFees=0.0;
            }

        }catch (Exception e){
            log.info("Caught Error {}"+e);
        }
        return totalProductFees;
    }


    // TODO: 4/3/2023 get fees two
    public EntityResponse getProductFees2(String eventCode){
        try{


            log.info("*********************got called to fetch fees details");
            EntityResponse entityResponse= new EntityResponse<>();
            String url = system_config_url + "find/by/code/" + eventCode;

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
            System.out.println("Fees response ::" + res);

            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                log.info("fee item json valid");
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                log.info("entity-->"+entityStatusCode);
                if(entityStatusCode.equals("200")){
                    log.info("status code: "+ entityStatusCode);
                    JSONObject genProdDetails = etyResponse.getJSONObject("entity");

                    FeeDto feeDto= objectMapper.readValue(genProdDetails.toString(),
                            FeeDto.class);

                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(feeDto);
                    return entityResponse;
                }else {
                    log.info("---------> INVALID JSON");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND FEE "+eventCode);
                    return entityResponse;
                }
            }else {
                log.info("---------> INVALID JSON");
                System.out.println("Account response ::" + res);
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND FEE: "+eventCode);
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

    public EntityResponse getProductFees3(List<EventReqDto> evts){
        try{


            log.info("*********************got called to fetch fees details");
            EntityResponse entityResponse= new EntityResponse<>();
            String url = system_config_url + "find/by/codes";


            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(evts);
            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .put(body)
                    .url(url)
                    .addHeader("userName", "String")
                    .addHeader("entityId", "001")
                    .build();
            Response response = client.newCall(request).execute();

            String res = response.body().string();
            System.out.println("Fees response ::" + res);

            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                log.info("fee item json valid");
                JSONObject etyResponse = new JSONObject(res);
                String entityStatusCode= etyResponse.get("statusCode").toString();
                log.info("entity-->"+entityStatusCode);
                if(entityStatusCode.equals("200")){
                    log.info("status code: "+ entityStatusCode);
                    JSONArray entity =etyResponse.getJSONArray("entity");

                    FeeDto[] feeDto= objectMapper.readValue(entity.toString().toString(),
                            FeeDto[].class);

                    List<FeeDto> feeDtos= new ArrayList<>(Arrays.asList(feeDto));

                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(feeDtos);
                    return entityResponse;
                }else {
                    log.info("---------> INVALID JSON");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND FEE ");
                    return entityResponse;
                }
            }else {
                log.info("---------> INVALID JSON");
                System.out.println("Account response ::" + res);
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED! COULD NOT FIND FEE: ");
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
