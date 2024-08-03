package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;

import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
import com.emtechhouse.accounts.Models.Accounts.TransactionDtos.RecievedTransactions.ReceivedTransactionHolder;
import com.emtechhouse.accounts.Models.Accounts.TransactionDtos.RecievedTransactions.RecievedTransactionDetails;
import com.emtechhouse.accounts.TransactionService.Requests.TransactionInterface;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.ChargePartran.ChargePartran;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class TransactionServiceCaller {
    @Autowired
    private TranHeaderRepository tranHeaderRepository;
    //    @Value("${server.host}")
    @Autowired
    private AccountTransactionService accountTransactionService;
    private  String host = "localhost";

    String GET_ACCOUNT_URL = "http://"+host+":9006/accounts/";
    String GET_SYSTEM_CONFIG_SERVICE= "http://"+host+":9003/api/v1/parameters/configurations/event_id/";


    //TODO: get withdrawal details

    public List<ChargePartran> getWithdrwalChargePartran(String chargetype, Double amount,String debitacc) throws IOException {
        //select amt from charges where low_amt <= ? and max_amt >=?
        //select amt from charges where low_amt <= ? and max_amt >=?
        List<ChargePartran> chargePartranList = new ArrayList<>();
        try {


            //todo:collect widthdrawal charges
            log.info("Collecting withdrawal charges...");
//        http://52.15.152.26:9003/api/v1/parameters/configurations/event_id/find/by/chargetype/WithdrawalFee/and/amount/600
            String url = GET_SYSTEM_CONFIG_SERVICE + "find/by/chargetype/" + chargetype + "/and/amount/" + amount;
            OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .get().url(url).addHeader("userName", UserRequestContext.getCurrentUser())
                    .addHeader("entityId", EntityRequestContext.getCurrentEntityId())
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            JSONObject jo = new JSONObject(res);
            System.out.println("response " + jo);
            Integer statusCode = (Integer) jo.get("statusCode");
            log.info("Status code " + statusCode);
            if (statusCode == 200) {
                log.info("Charge collection successful");
                Object obj = jo.get("entity");
                JSONObject job = new JSONObject(obj.toString());
                System.out.println("JSONObject  ::" + job);
                String tranType = job.getString("partTranType");
                Double chargeamount = job.getDouble("transactionAmount");
                String acid = job.getString("acid");
                String currency = job.getString("currency");
                String narration = "Withdrawal charges";

                ChargePartran chargePartran = new ChargePartran();
                chargePartran.setTransactionAmount(chargeamount);
                chargePartran.setAcid(acid);
                chargePartran.setCurrency(currency);
                chargePartran.setIsoFlag('S');
                chargePartran.setExchangeRate("1");
                chargePartran.setPartTranType(tranType);
                chargePartran.setTransactionParticulars(narration);
                chargePartran.setTransactionDate(new Date());

                ChargePartran chargeP = new ChargePartran();
                chargeP.setTransactionAmount(chargeamount);
                chargeP.setAcid(debitacc);
                chargeP.setCurrency("KES");
                chargeP.setIsoFlag('S');
                chargeP.setExchangeRate("1");
                chargeP.setPartTranType("Debit");
                chargeP.setTransactionParticulars(narration);

                chargePartranList.add(chargeP);
                chargePartranList.add(chargePartran);


                log.info("Charge collection complete");
                System.out.println("TOTAL charge partrans :: "+ chargePartranList);
                return chargePartranList;


            } else {
                log.error("Charge collection failed");
                log.error(jo.getString("message"));

                return chargePartranList;

            }
        }catch (Exception e){
            log.error("Error "+ e.getMessage());
            e.printStackTrace();
            return chargePartranList;
        }
    }

    public EntityResponse updateTransaction(TransactionHeader transaction) {
        EntityResponse response = new EntityResponse<>();
        try {
            log.info("Updating transaction...");
            transaction.setModifiedBy(UserRequestContext.getCurrentUser());
            transaction.setModifiedTime(new Date());
            transaction.setModifiedFlag(CONSTANTS.YES);
            tranHeaderRepository.save(transaction);
            response.setMessage("Transaction updated successfully");
            response.setStatusCode(HttpStatus.ACCEPTED.value());
            log.info("Transaction updated successfully");


        } catch (Exception e) {
            log.error("Error ", e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public EntityResponse updateAccounts1(List<TransactionInterface> transactionInterfaces) throws IOException {
        List<RecievedTransactionDetails> recievedTransactionDetails=new ArrayList<>();
        transactionInterfaces.forEach(transactionInterface -> {
            RecievedTransactionDetails recievedTransactionDetails1=new RecievedTransactionDetails();
            recievedTransactionDetails1.setTransactionAmount(transactionInterface.getTransactionAmount());
            recievedTransactionDetails1.setAcid(transactionInterface.getAcid());
            recievedTransactionDetails1.setPartTranType(transactionInterface.getPartTranType());
            recievedTransactionDetails1.setTransactionDesc(transactionInterface.getTransactionDesc());
            recievedTransactionDetails.add(recievedTransactionDetails1);
        });
        ReceivedTransactionHolder receivedTransactionHolder=new ReceivedTransactionHolder();
        receivedTransactionHolder.setTransactions(recievedTransactionDetails);
        EntityResponse resp = new EntityResponse<>();
        resp=accountTransactionService.incomingFullTransaction(receivedTransactionHolder);
        Integer statusCode=resp.getStatusCode();
        String res= resp.getMessage();
        if (statusCode == 200) {
            log.info("Account update successfully");
            System.out.println("response " + res);
            resp.setMessage("Account update successfully");
            resp.setEntity(res);
            resp.setStatusCode(HttpStatus.OK.value());
        } else {
            log.error("Error occurred while updating accounts");
//            resp.setMessage("Transaction failed " );
            log.error("Status code ::" +statusCode);
            log.error("Message ::" +resp.getMessage());
            resp.setStatusCode(statusCode);
        }
        return resp;
    }

    public Double checkTellerlimit(String acid,String transsactionType) throws IOException {
        EntityResponse r = new EntityResponse<>();


        String url = GET_ACCOUNT_URL + "get/office/account/specific/details/"+acid;

        OkHttpClient client = new OkHttpClient()
                .newBuilder()
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
        JSONObject jo = new JSONObject(res);
        System.out.println("response" + jo);
        Integer statusCode = (Integer) jo.get("statusCode");
        String message= jo.getString("message");
        System.out.println("Status code == "+ statusCode);
        JSONObject j = new JSONObject(jo.get("entity").toString());
        System.out.println("Entity "+j);
//        r.setEntity(j);
//        r.setMessage(message);
//        r.setStatusCode(statusCode);
        if (transsactionType.equalsIgnoreCase("Cash Withdrawal")){
            Double cashLimitDr = j.getDouble("cashLimitDr");
            return cashLimitDr;
        }else if(transsactionType.equalsIgnoreCase("Transfer")){
            Double transferLimitCr= j.getDouble("transferLimitCr");
            Double transferLimitDr = j.getDouble("transferLimitDr");

            return transferLimitDr;

        } else if (transsactionType.equalsIgnoreCase("Cash Deposit")) {
            Double cashLimitCr = j.getDouble("cashLimitCr");
            return cashLimitCr;
        }else if (transsactionType.equalsIgnoreCase("Cheque")) {
            Double clearingLimitCrExce = j.getDouble("clearingLimitCrExce");
            return clearingLimitCrExce;
        }else {
            log.info("Uknown tranasction type");
            return 0.0;
        }

    }
}