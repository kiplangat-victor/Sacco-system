package com.emtechhouse.accounts.KraStampedTransactions;

import com.emtechhouse.accounts.KraStampedTransactions.Deviceinfo.DeviceInfo;
import com.emtechhouse.accounts.KraStampedTransactions.Deviceinfo.DeviceInfoRepo;
import com.emtechhouse.accounts.KraStampedTransactions.Tariffs.Tariff;
import com.emtechhouse.accounts.KraStampedTransactions.Tariffs.TariffRepo;
import com.emtechhouse.accounts.KraStampedTransactions.dto.DeviceInfoDTO;
import com.emtechhouse.accounts.KraStampedTransactions.dto.KraStampedTransactionRequestDTO;
import com.emtechhouse.accounts.KraStampedTransactions.dto.StampedTransactionDTO;
import com.emtechhouse.accounts.KraStampedTransactions.dto.StampedTransactionResponse;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDateTime;


@Service
@Slf4j
public class Kraservice {

    @Value("${spring.application.exciseDuty.kraMiddlewareInitializeDeviceURL}")
    private String KRA_MIDDLEWARE_DEVICE_INIT_URL;
    @Value("${spring.application.exciseDuty.kraMiddlewareGetTariffURL}")
    private String  KRA_MIDDLEWARE_GET_TARIFF_URL;
    @Value("${spring.application.exciseDuty.kraMiddlewareSendTransactionURL}")
    private String  KRA_MIDDLEWARE_SEND_TRANSACTION_URL;
    @Value("${spring.application.exciseDuty.transTyCd}")
    private String  KRA_MIDDLEWARE_TRANS_TYPE_CD;
    @Value("${spring.application.exciseDuty.rcptTyCd}")
    private String  KRA_MIDDLEWARE_RECPT_TYPE_CD;
    private final DeviceInfoRepo deviceInfoRepo;
    private final StampedTransactionRepo stampedTransactionRepo;
    private final ObjectMapper objectMapper;
    private final TariffRepo tariffRepo;
    public Kraservice(DeviceInfoRepo deviceInfoRepo, StampedTransactionRepo stampedTransactionRepo, ObjectMapper objectMapper, TariffRepo tariffRepo) {
        this.deviceInfoRepo = deviceInfoRepo;
        this.stampedTransactionRepo = stampedTransactionRepo;
        this.objectMapper = objectMapper;
        this.tariffRepo = tariffRepo;
    }
//    Send Transactions
    public EntityResponse sendTransactionToKra(TransactionHeader transactionHeader){
        EntityResponse response = new EntityResponse();
        try {
            if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)){
                List<PartTran> partTranList = transactionHeader.getPartTrans();
                List<PartTran> debits   = partTranList.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
                PartTran partTran = debits.get(0);
                StampedTransaction stampedTransactionExisting = stampedTransactionRepo.findFirstByOrderByIdDesc();
                Long invID = stampedTransactionExisting.getId() + 1;
                if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)){
                    Optional<Tariff> checkTarrif = tariffRepo.findByTarrifNm("Counter Cash Withdrawal");
                    if (checkTarrif.isPresent()) {
                        Double exDutyAmt = partTran.getTransactionAmount() * (checkTarrif.get().getExdutyRt() / 100);
                        LocalDateTime localDateTime = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
                        Long formattedDateTime = Long.valueOf(localDateTime.format(formatter));
                        DeviceInfo deviceInfos = deviceInfoRepo.findAll().get(0);
                        StampedTransaction stampedTransaction = new StampedTransaction();
                        stampedTransaction.setPin(deviceInfos.getPin());
                        stampedTransaction.setBhfId(deviceInfos.getBhfId());
                        stampedTransaction.setDvcSrlNo(deviceInfos.getDvcSrlNo());
                        stampedTransaction.setDt(formattedDateTime);
                        stampedTransaction.setAccNo(partTran.getAcid());
                        stampedTransaction.setTransTyCd(KRA_MIDDLEWARE_TRANS_TYPE_CD);
                        stampedTransaction.setRcptTyCd(KRA_MIDDLEWARE_RECPT_TYPE_CD);
                        stampedTransaction.setInvId(invID.toString());
                        stampedTransaction.setRefId("0");
                        stampedTransaction.setTarrifTyCd(checkTarrif.get().getTarrifCd());
                        stampedTransaction.setExRt(checkTarrif.get().getExdutyRt());
                        stampedTransaction.setTrnAmt(partTran.getTransactionAmount());
                        stampedTransaction.setExDutyAmt(exDutyAmt);
                        stampedTransaction.setStatus("Pending");
                        stampedTransaction.setPostedBy("System");
                        stampedTransaction.setPostedFlag('Y');
                        stampedTransaction.setPostedTime(new Date());
                        stampedTransactionRepo.save(stampedTransaction);
////                         post to stamp transaction
//                        KraStampedTransactionRequestDTO transactionRequestDTO = new KraStampedTransactionRequestDTO();
//                        StampedTransactionDTO stampedTransaction1 = new StampedTransactionDTO();
//                        stampedTransaction1.setPin(stampedTransaction.getPin());
//                        stampedTransaction1.setBhfId(stampedTransaction.getBhfId());
//                        stampedTransaction1.setDvcSrlNo(stampedTransaction.getDvcSrlNo());
//                        stampedTransaction1.setDt(stampedTransaction.getDt());
//                        stampedTransaction1.setAccNo(stampedTransaction.getAccNo());
//                        stampedTransaction1.setTransTyCd(stampedTransaction.getTransTyCd());
//                        stampedTransaction1.setRcptTyCd(stampedTransaction.getRcptTyCd());
//                        stampedTransaction1.setInvId(stampedTransaction.getInvId());
//                        stampedTransaction1.setRefId(stampedTransaction.getRefId());
//                        stampedTransaction1.setTarrifTyCd(stampedTransaction.getTarrifTyCd());
//                        stampedTransaction1.setExRt(stampedTransaction.getExRt());
//                        stampedTransaction1.setTrnAmt(stampedTransaction.getTrnAmt());
//                        stampedTransaction1.setExDutyAmt(stampedTransaction.getExDutyAmt());
//                        transactionRequestDTO.setData(stampedTransaction1);
//                        System.out.println("-----------------------------------------------------------------------------");
//                        System.out.println(sendTransactionToKraMiddleware(transactionRequestDTO));

                        //                         post to stamp transaction
                        KraStampedTransactionRequestDTO transactionRequestDTO = new KraStampedTransactionRequestDTO();
                        StampedTransactionDTO stampedTransaction1 = new StampedTransactionDTO();
                        stampedTransaction1.setPin(stampedTransaction.getPin());
                        stampedTransaction1.setBhfId(stampedTransaction.getBhfId());
                        stampedTransaction1.setDvcSrlNo(stampedTransaction.getDvcSrlNo());
                        stampedTransaction1.setDt(stampedTransaction.getDt());
                        stampedTransaction1.setAccNo(stampedTransaction.getAccNo());
                        stampedTransaction1.setTransTyCd(stampedTransaction.getTransTyCd());
                        stampedTransaction1.setRcptTyCd(stampedTransaction.getRcptTyCd());
                        stampedTransaction1.setInvId(stampedTransaction.getInvId());
                        stampedTransaction1.setRefId(stampedTransaction.getRefId());
                        stampedTransaction1.setTarrifTyCd(stampedTransaction.getTarrifTyCd());
                        stampedTransaction1.setExRt(stampedTransaction.getExRt());
                        stampedTransaction1.setTrnAmt(stampedTransaction.getTrnAmt());
                        stampedTransaction1.setExDutyAmt(stampedTransaction.getExDutyAmt());
                        transactionRequestDTO.setData(stampedTransaction1);
                        response = sendTransactionToKraMiddleware(transactionRequestDTO);
                        JSONObject kraResponse = new JSONObject(response);
                        if (kraResponse.getInt("statusCode") == HttpStatus.OK.value()){
                            StampedTransactionResponse dataResp = objectMapper.readValue(kraResponse.get("entity").toString(), StampedTransactionResponse.class);
                            Optional<StampedTransaction> transactionCheck = stampedTransactionRepo.findByInvId(dataResp.getRcptNo());
                            if (transactionCheck.isPresent()){
                                StampedTransaction transaction = transactionCheck.get();
                                transaction.setSignatureGenerated(dataResp.getRcptStamp());
                                transaction.setStatus("Completed");
                                transaction.setCleared(true);
                                stampedTransactionRepo.save(transaction);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){

        }
        return response;
    }
    public EntityResponse getKraTarrifs(DeviceInfoDTO deviceInfoDTO){
        try {
            log.info("--------------------------------sending transaction to kra middleware begins");
            EntityResponse entityResponse= new EntityResponse<>();
            String url= KRA_MIDDLEWARE_GET_TARIFF_URL;
            log.info("url:="+url);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Gson g = new Gson();
            String deviceInfoDTOSTR = g.toJson(deviceInfoDTO);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), deviceInfoDTOSTR);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                String result= etyResponse.get("result").toString();
                if(result.equalsIgnoreCase("request successful")){
                    JSONObject resultOB = new JSONObject(etyResponse.get("data").toString());
                    JSONArray entity = resultOB.getJSONArray("itemList");
                    Tariff[] TF = objectMapper.readValue(entity.toString(), Tariff[].class);
                    List<Tariff> tariffList = new ArrayList<>(Arrays.asList(TF));
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setEntity(tariffList);
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


    public EntityResponse sendTransactionToKraMiddleware(KraStampedTransactionRequestDTO kraStampedTransactionRequestDTO){
        try {
            log.info("--------------------------------sending transaction to kra middleware begins");
            EntityResponse entityResponse= new EntityResponse<>();
            String url= KRA_MIDDLEWARE_SEND_TRANSACTION_URL;
            log.info("url:="+url);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Gson g = new Gson();
            String kraStampedTransactionRequestDTOSTR = g.toJson(kraStampedTransactionRequestDTO);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), kraStampedTransactionRequestDTOSTR);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Boolean isJSONValid= isJSONValid(res);
            if(isJSONValid){
                JSONObject etyResponse = new JSONObject(res);
                String resultCd= etyResponse.get("resultCd").toString();
//                997
                if (resultCd.equalsIgnoreCase("998")){
                    entityResponse.setMessage(etyResponse.get("resultMsg").toString());
                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (resultCd.equalsIgnoreCase("997")){
                        entityResponse.setMessage(etyResponse.get("resultMsg").toString());
                        entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    StampedTransactionResponse dataResp = objectMapper.readValue(etyResponse.get("data").toString(), StampedTransactionResponse.class);
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                    entityResponse.setEntity(dataResp);
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
