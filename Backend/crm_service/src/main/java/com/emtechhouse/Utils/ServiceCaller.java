package com.emtechhouse.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ServiceCaller {
    @Value("${SEND_SMS_EMT}")
    private String sms_notification;

    private final ObjectMapper objectMapper;

    public ServiceCaller(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public EntityResponse sendSmsEmt(SmsDto dto){
        CompletableFuture.runAsync(() ->{
            try{
                log.info("********************* Sending an sms");
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
}
