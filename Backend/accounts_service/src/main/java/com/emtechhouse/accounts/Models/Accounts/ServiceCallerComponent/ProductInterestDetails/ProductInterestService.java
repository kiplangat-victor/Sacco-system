package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails;

import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProductInterestService {

    @Value("${GET_LOAN_INTEREST_RATE}")
    String loan_interest_url;

    public EntityResponse getLoanInterest(String prodCode, Double principalAmount) {
        try {
            log.info("Product Code: "+prodCode);
            String userName=UserRequestContext.getCurrentUser();
            String entityId=EntityRequestContext.getCurrentEntityId();
            if(userName==null){
                userName= CONSTANTS.SYSTEM_USERNAME;
                entityId=CONSTANTS.SYSTEM_ENTITY;
            }
            EntityResponse entityResponse= new EntityResponse<>();
//            String url= URLS.GET_LOAN_INTEREST_RATE+principalAmount+"&productCode="+prodCode;
            String url= loan_interest_url+principalAmount+"&productCode="+prodCode;
            System.out.println(url);
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
            System.out.println("Interest response ::" + res);
            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                Integer statusCode= Integer.valueOf(etyResponse.get("statusCode").toString());
                log.info("Response status code is :: "+statusCode);
                if(statusCode.equals(200)){
                    String body=etyResponse.get("entity").toString();
                    Boolean isBodyJSONValid= isJSONValid(body);
                    if(isBodyJSONValid){
                        JSONObject etyResponseJSONObject =etyResponse.getJSONObject("entity");

                        Double rate =etyResponseJSONObject.getDouble("rate");
                        String calculationMethod =etyResponseJSONObject.getString("calculationMethod");
                        String interestPeriod =etyResponseJSONObject.getString("interestPeriod");
                        Double penalInterstAmount=etyResponseJSONObject.getDouble("penalInterest");

                        ProductInterestDetails productInterestDetails= new ProductInterestDetails();
                        productInterestDetails.setInterestRate(rate);
                        productInterestDetails.setInterestCalculationMethod(calculationMethod);
                        productInterestDetails.setInterestPeriod(interestPeriod);
                        productInterestDetails.setPenalInterestAmount(penalInterstAmount);

                        entityResponse.setStatusCode(HttpStatus.OK.value());
                        entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                        entityResponse.setEntity(productInterestDetails);
                        return entityResponse;
                    }else {
                        entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                        entityResponse.setMessage("WRONG RESPONSE! COULD NOT FIND THE ATTACHED INTEREST RATE: "+prodCode);
                        return entityResponse;
                    }
                    //
                }else {
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("FAILED! COULD NOT FIND THE ATTACHED INTEREST RATE: "+prodCode);
                    return entityResponse;
                }
            }else {
                log.info("---------> INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("API ERROR! COULD NOT FIND THE ATTACHED INTEREST RATE: "+prodCode);
                return entityResponse;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
//            return null;
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
