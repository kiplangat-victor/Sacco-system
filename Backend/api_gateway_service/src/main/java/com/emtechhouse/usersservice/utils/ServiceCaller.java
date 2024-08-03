package com.emtechhouse.usersservice.utils;
import com.emtechhouse.usersservice.DTO.MailDto;
import com.emtechhouse.usersservice.DTO.Mailparams;
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

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ServiceCaller {

    @Value("${SEND_SMS_EMT}")
    private String mail_url;

        public EntityResponse sendEmail(MailDto mail){
        CompletableFuture.runAsync(() ->{
            try{
                log.info("********************* Sending an email");
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(mail);
                RequestBody body = RequestBody.create(json, JSON);



                EntityResponse entityResponse= new EntityResponse<>();

                String url= mail_url;

                log.info("url "+url);

                Request request = new Request.Builder()
                        .put(body)
                        .url(url)
                        .addHeader("userName", "String")
                        .addHeader("entityId", "001")
                        .build();
                client.newCall(request).execute();
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
}
