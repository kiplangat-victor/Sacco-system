package com.emtechhouse.accounts.NotificationComponent.SmsService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
@Slf4j
public class SMSService {
    @Value("${spring.application.sms.callbackurl}")
    private String callbackurl;
    @Value("${spring.application.sms.url}")
    private String url;
    @Value("${spring.application.sms.msgtype}")
    private String msgtype;
    @Value("${spring.application.sms.profileCode}")
    private String profileCode;
    @Value("${spring.application.sms.apiKey}")
    private String apiKey;
    @Value("${spring.application.enableSMS}")
    private String enableSMS;
    @Value("${spring.application.enableProdSMS}")
    private String enableProdSMS;
    @Value("${spring.application.testPhone}")
    private String testPhone;


    @Autowired
    private SMSNOtificaionRepo smsNotificationsRepository;

    public static String generatecSystemCode(int len) {
        String chars = "BAHATIDAIRYFARM1234567890";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 12; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        log.info("RANDOM STRING :: "+sb);
        return sb.toString();
    }
    public SMSResponse sendSMS(String message, String phone)
    {
        System.out.println(callbackurl);
        System.out.println("---------------------");
        System.out.println();

       String phoneno = phone.trim();
        log.info("Phone number == "+ phoneno);
        if(phoneno.startsWith("0")){
//            phoneno.substring(0);
            phoneno.replaceFirst("0","254");
        }else if (phoneno.startsWith("+")){
            phoneno.substring(0);
        }else if (phoneno.startsWith("7")|| phoneno.startsWith("1")){
            phoneno="254"+phoneno;
        }else {
            log.info("Invalid phone number");
        }
        log.info(phoneno);
        //Time Stamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String messageref= generatecSystemCode(6);

        SMSResponse sr = new SMSResponse();
        String requestJson = "{\"profile_code\": \""+profileCode+"\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"mobile_number\": \""+phoneno+"\",\n" +
                "      \"message\": \""+message+"\",\n" +
                "      \"message_type\": \""+msgtype+"\",\n" +
                "      \"message_ref\": \""+messageref+"\"\n" +
                "      \n" +
                "    }\n" +
                "  ],\n" +
                "  \"dlr_callback_url\": \""+callbackurl+"\"\n" +
                "}";

        log.info("SENDING REQUEST AT "+timestamp+ " ");
        log.info("REQUEST TO CROSSGATE Profile Code  { "+profileCode+" } Destination { "+phoneno+" } Message { " +message+ " }");

//        OkHttpClient client = null;
//        client = new OkHttpClient.Builder()
//                .connectTimeout(90000, TimeUnit.MILLISECONDS)
//                .readTimeout(90000, TimeUnit.MILLISECONDS)
//                .build();
//
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        //Disable SSL
        String javaHomePath = System.getProperty("java.home");
        String keystore = javaHomePath + "/lib/security/cacerts";
        String storepass= "changeit";
        String storetype= "JKS";
        String[][] props = {
                { "javax.net.ssl.trustStore", keystore, },
                { "javax.net.ssl.keyStore", keystore, },
                { "javax.net.ssl.keyStorePassword", storepass, },
                { "javax.net.ssl.keyStoreType", storetype, },
        };
        for (int i = 0; i < props.length; i++) {
            System.getProperties().setProperty(props[i][0], props[i][1]);
        }

        RequestBody body = RequestBody.create(mediaType, requestJson);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("api-key", apiKey)
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println(res);
            log.info("RESPONSE BODY FROM CROSSGATE { "+res+" }");
            JSONArray jar = new JSONArray(res);
            JSONObject json = null;
            for (Object obj : jar) {
                json = new JSONObject(obj.toString());
            }

            int code = response.code();

            if (response.isSuccessful()) {
                log.info("RECEIVING RESPONSE AT { " + timestamp + " } CODE { " + code + " }");
                sr.setResponseCode(code);
                sr.setMessageId(json.getString("message_id"));
            } else {
                log.info("ERROR FROM CROSSGATE SMS GATEWAY \n" + res);
                sr.setResponseCode(code);
                sr.setMessageId("-");
            }
        }
        catch (Exception e)
        {
            log.info("ERROR WHEN SENDING SMS GATEWAY { " +e.getLocalizedMessage()+" }");
            sr.setResponseCode(1009);
            sr.setMessageId("-");
        }
        return sr;
    }
    public void SMSNotification(String message,String phoneNumber)
    {
        String toPhone = phoneNumber;
        if (enableProdSMS.equalsIgnoreCase("false")){
            toPhone = testPhone;
            System.out.println("-------------------------------SMS sending in test env Check application.yml---------------------------------");
        }
        if ( enableSMS.equalsIgnoreCase("false")){
            System.out.println("--------------------------------SMS is blocked! Check application.yml--------------------------------");
        }else {
            System.out.println("-------------------------------SMS sending in prod env Check application.yml---------------------------------");
            //Create Message and Save In DB
            SMSResponse sr = sendSMS(message, phoneNumber);
            SMSNotifications sms = new SMSNotifications();
            sms.setResponseCode(sr.getResponseCode());
            sms.setEventType("-");
            sms.setDeliveryTime("-");
            sms.setMessageRef(generatecSystemCode(10));
            sms.setMessageId(sr.getMessageId());
            sms.setMessage(message);
            sms.setSentDate(new Date());
            sms.setPhoneNumber(toPhone);
            smsNotificationsRepository.save(sms);
        }
    }
}
