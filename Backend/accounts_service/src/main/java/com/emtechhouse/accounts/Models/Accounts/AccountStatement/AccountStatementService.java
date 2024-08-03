package com.emtechhouse.accounts.Models.Accounts.AccountStatement;

import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.itextpdf.text.pdf.PdfReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AccountStatementService {
    @Autowired
    private ItemServiceCaller itemServiceCaller;
    @Autowired
    private TransactionsController transactionsController;
    // TODO: 3/28/2023 generate users account statement
    public EntityResponse getAccountStatement(String acid, String fromdate,String todate){
        try {
            EntityResponse response = new EntityResponse<>();
            //get the report number of pages
            //get the charges from the charges service
            //calculate the total cost
            //check the account balance customer balance
            //perform a transaction
            //return the report
            response=itemServiceCaller.getAccountStatement(acid,fromdate,todate);
            if(response.getStatusCode()== HttpStatus.OK.value()){
                byte[] data= (byte[]) response.getEntity();

//                log.info("data " +data.toString());
//                PdfReader reader = new PdfReader(data);
//                int numOfPages = reader.getNumberOfPages();
                int numOfPages=4;

                log.info("The statement number of pages are :: "+numOfPages);

                //get this from 9003
                Double costPerPage = 10.0;
                String chargeCollectionAccount = "500323";

                Double totalCost =costPerPage*numOfPages;
                //perform transaction

                String transactionDesc= "Account statement processing fees for account "+acid;
                TransactionHeader feeTransaction= createTransactionHeader(
                        "KES",
                        transactionDesc,
                        totalCost,
                        acid,
                        chargeCollectionAccount
                );
//                EntityResponse transactionRes= transactionsController.systemTransaction1(feeTransaction).getBody();
//
//                if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
//                    ///failed transactiom
//                    response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM FEE TRANSACTION: "+acid);
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity(feeTransaction);
//                }else {
                    //successful transaction
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(data);
//                }

            }
            return response;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public TransactionHeader createTransactionHeader(String currency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc) {
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
        transactionHeader.setCurrency(currency);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
        transactionHeader.setTotalAmount(totalAmount);

        PartTran drPartTran = new PartTran();
        drPartTran.setPartTranType(CONSTANTS.DEBITSTRING);
        drPartTran.setTransactionAmount(totalAmount);
        drPartTran.setAcid(drAc);
        drPartTran.setCurrency(currency);
        drPartTran.setExchangeRate("");
        drPartTran.setTransactionDate(new Date());
        drPartTran.setTransactionParticulars(transDesc);
        drPartTran.setIsoFlag(CONSTANTS.YES);

        PartTran crPartTran = new PartTran();
        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
        crPartTran.setTransactionAmount(totalAmount);
        crPartTran.setAcid(crAc);
        crPartTran.setCurrency(currency);
        crPartTran.setExchangeRate("");
        crPartTran.setTransactionDate(new Date());
        crPartTran.setTransactionParticulars(transDesc);
        crPartTran.setIsoFlag(CONSTANTS.YES);

        List<PartTran> partTranList =new ArrayList<>();
        partTranList.add(drPartTran);
        partTranList.add(crPartTran);

        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }

}
