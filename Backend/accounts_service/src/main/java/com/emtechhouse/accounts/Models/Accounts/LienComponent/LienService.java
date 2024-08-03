package com.emtechhouse.accounts.Models.Accounts.LienComponent;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.LienTransactions.LienTransactions;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.LienTransactions.LienTransactionsRepo;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.AcidGenerator;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class LienService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TranHeaderService tranHeaderService;

    @Autowired
    private LienRepository lienRepository;

    @Autowired
    private TransactionsController transactionsController;
    @Autowired
    private LienTransactionsRepo lienTransactionsRepo;
    @Autowired
    private AcidGenerator acidGenerator;

    public EntityResponse addLien(Lien lien) {
        try {
            EntityResponse res=new EntityResponse<>();
            String drAcid=lien.getSourceAcid();
            //String crAcid=lien.getDestinationAcid();

            //lien account validation
            Optional<Account> drAccount= accountRepository.findByAcidAndDeleteFlag(drAcid, CONSTANTS.NO);
            if(drAccount.isPresent()) {
                Account presentDrAccount= drAccount.get();
                //Optional<Account> crAccount= accountRepository.findByAcidAndDeleteFlag(crAcid, CONSTANTS.NO);
                if(drAccount.isPresent()) {
                    Date today = new Date();
                    Date effectiveFrom= lien.getEffectiveFrom();

                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date effectiveFromZeroTime= formatter.parse(formatter.format(effectiveFrom));
                    Date todayWithZeroTime = formatter.parse(formatter.format(today));

//                    System.out.println(effectiveFromZeroTime);
//                    System.out.println(todayWithZeroTime);
                    if(effectiveFromZeroTime.compareTo(todayWithZeroTime)>=0) {
                        if(lien.getLienAmount()>0) {
                            String lienCode= tranHeaderService.generateRandomCode(5).toUpperCase();

                            System.out.println(lienCode);

                            //Account presentCrAccount= crAccount.get();
                            lien.setSourceAccount(presentDrAccount);
                            lien.setDestinationAccount(presentDrAccount);
                            lien.setPostedBy(UserRequestContext.getCurrentUser());
                            lien.setPostedFlag(CONSTANTS.YES);
                            lien.setPostedTime(new Date());
                            lien.setVerifiedFlag(CONSTANTS.NO);
                            lien.setDeletedFlag(CONSTANTS.NO);
                            lien.setLienAdjustedAmount(0.00);
                            lien.setLienCode(lienCode);
                            //save lien

                            //check if the lien effective date e.g
                            //if it is today set it is active

                            if(effectiveFromZeroTime.compareTo(todayWithZeroTime)==0) {
                                lien.setStatus(CONSTANTS.ACTIVE);
                            }else{
                                lien.setStatus(CONSTANTS.NOT_ACTIVE);
                            }
                            Lien sLien= lienRepository.save(lien);

                            //update source account
                            presentDrAccount.setLienAmount(presentDrAccount.getLienAmount()+ lien.lienAmount);
//                            accountRepository.updateLienBalance(presentDrAccount.getLienAmount()+ lien.lienAmount, presentDrAccount.getAcid());
                            res.setMessage("Lien created successfully");
                            res.setStatusCode(HttpStatus.CREATED.value());
                            res.setEntity(sLien);
                            return res;
                        } else {
                            res.setMessage("Lien amount should be greater than zero");
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }else {
                        res.setMessage("Effective from date should not be less than today");
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                }else {
                    res.setMessage("Could not find the destination account from the json");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                res.setMessage("Could not find the source account");
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<EntityResponse> satisfyLiens(String acid){
        try{
            List<EntityResponse> entityResponses= new ArrayList<>();
            Optional<Account> drAccount= accountRepository.findByAcidAndDeleteFlag(acid, CONSTANTS.NO);
            if(drAccount.isPresent()){
                Account account= drAccount.get();
                List<Lien> liens= account.getDebitLiens();
                if(liens.size()>0){
                    liens.forEach(lien -> {
                        entityResponses.add(satisfyLien(lien));
                    });

                }else {
                    EntityResponse res= new EntityResponse<>();
                    res.setMessage("Account does not have liens");
                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponses.add(res);
                }
            }
            return entityResponses;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse satisfyLienByLienCode(String lienCode){
        try {
            EntityResponse entityResponse= new EntityResponse<>();
            Optional<Lien> lien= lienRepository.findByLienCode(lienCode);
            if(lien.isPresent()){
                Lien presentLien= lien.get();
                entityResponse =satisfyLien(presentLien);
            }else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("Lien Not found");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse satisfyLien(Lien lien) {
//        System.out.println("-------------------------------New Lien---------------------------------------");
        try{
            EntityResponse res= new EntityResponse<>();
            //check if lien is active
            if(lien.getStatus().equals(CONSTANTS.L_ACTIVE)) {
//                System.out.println("Is active");
                //check the source account balance
                AccountRepository.AcBalanceAndLien bal= accountRepository.getAccountBalanceAndLien(lien.getSourceAccount().getAcid()).get();

                Double lienAmt= lien.getLienAmount();
//                Double sourceAccountBal= lien.getSourceAccount().getAccountBalance();
//                Double bookBalance =lien.getSourceAccount().getBookBalance();

                Double sourceAccountBal= bal.getAccountBalance();
                Double bookBalance =bal.getBookBalance();
                if((sourceAccountBal-bookBalance)>=lienAmt){
                    System.out.println("Account balance=="+sourceAccountBal+"acid"+lien.getSourceAccount().getAcid());
                    //perform transaction
                    String transDesc=lien.getLienDescription();
                    String currency=lien.getSourceAccount().getCurrency();
                    String debitAcid=lien.getSourceAccount().getAcid();
                    String creditAcid= lien.getDestinationAccount().getAcid();

                    System.out.println(debitAcid+" - "+creditAcid);

                    TransactionHeader transactionHeader= createTransactionHeader (
                            currency,
                            transDesc+ " lien "+lien.getId(),
                            lienAmt,
                            debitAcid,
                            creditAcid);
                    EntityResponse transactionRes= transactionsController.systemLienTransaction(transactionHeader).getBody();

//                    System.out.println(transactionRes);
                    if (transactionRes.getStatusCode()!=(HttpStatus.OK.value())) {
                        //failed transaction
                        res.setMessage("TRANSACTION ERROR! COULD NOT PERFORM LIEN TRANSACTION FOR ACCOUNT: "+debitAcid);
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        res.setEntity(transactionHeader);
                    } else {
                        System.out.println("-----Lien transaction captured as successful---------");
                        //successful transaction
                        //save transaction details
                        String transactionCode = (String) transactionRes.getEntity();
                        LienTransactions lienTransactions= new LienTransactions();
                        lienTransactions.setTransactionDate(new Date());
                        lienTransactions.setTransactionCode(transactionCode);
                        lienTransactions.setLien(lien);

                        lienTransactionsRepo.save(lienTransactions);

                        lienRepository.updateLienAdjustedAmount(lienAmt, lien.getId());
                        lien.setStatus(CONSTANTS.L_SATISFIED);
                        lienRepository.updateLienStatus(CONSTANTS.L_SATISFIED, lien.getId());
                        Double accountNewLienAmt= lien.getSourceAccount().getLienAmount()-lienAmt;
//                        accountRepository.updateLienBalance(accountNewLienAmt, debitAcid);

                        res.setMessage("LIEN TRANSACTION OF "+lienAmt+" WAS SUCCESSFUL, ACID: "+debitAcid);
                        res.setStatusCode(HttpStatus.OK.value());
                        res.setEntity(transactionHeader);
                    }
                } else {
//                    System.out.println("Insufficient  funds");
                    res.setMessage("INSUFFICIENT FUNDS IN THE SOURCE ACCOUNT ");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            } else {
                res.setMessage("INACTIVE LIEN");
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getAllLiensBySourceAccount(String acid) {
        try {
            EntityResponse entityResponse= new EntityResponse<>();
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid,CONSTANTS.NO);
            if(account.isPresent()){
                List<Lien> ls= lienRepository.findBySourceAccount(account.get());
                if(ls.size()>0){
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    entityResponse.setEntity(ls);
                }else {
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("Provided account does not have liens");
                }
            }else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("Provided account id doesn't exist");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getLienByLienCode(String lienCode){
        try {
            EntityResponse entityResponse= new EntityResponse<>();
            Optional<Lien> lien= lienRepository.findByLienCode(lienCode);
            if(lien.isPresent()){
                Lien presentLien= lien.get();
                entityResponse.setStatusCode(HttpStatus.FOUND.value());
                entityResponse.setMessage(HttpStatus.FOUND.getReasonPhrase());
                entityResponse.setEntity(presentLien);
            }else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("Not found");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse verifyLien(String lienCode){
        try {
            EntityResponse entityResponse= new EntityResponse<>();
            String user = UserRequestContext.getCurrentUser();
            Optional<LienRepository.checkLiensByLienCode> checkLien = lienRepository.checkLiensByLienCode(lienCode);
            System.out.println("About to verify a lien: "+checkLien);
            if(checkLien.isPresent()){
                LienRepository.checkLiensByLienCode lienflag = checkLien.get();
                if(lienflag.getPostedByWhom().equals(user)){
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage("You cannot verify what you created!");
                    entityResponse.setEntity("");
                    return entityResponse;
                }
                if(lienflag.getVerifiedFlag().equals("Y")){
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage("Lien with lien code "+lienCode+ " was verified by "+lienflag.getVerifiedByWho()+" on "+lienflag.getLienVerifiedTime());
                    entityResponse.setEntity(checkLien);
                }else {
                    Void lien= lienRepository.verifyLiensByLienCode(lienCode, user);
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setMessage("Lien with lien code "+lienCode+ " verified successfully");
                    entityResponse.setEntity(checkLien);
                }

            }else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("Lien with code"+ lienCode+ " cannot be found");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse modifyLien(Lien lien){
        try {
            EntityResponse entityResponse= new EntityResponse<>();
            String user = UserRequestContext.getCurrentUser();
            Optional<LienRepository.checkLiensByLienCode> checkLien = lienRepository.checkLiensByLienCode(lien.lienCode);
            System.out.println("About to modify a lien: "+checkLien);
            if(checkLien.isPresent()){
                LienRepository.checkLiensByLienCode lienflag = checkLien.get();
                if(lienflag.getLienDeletedFlag().equals("Y")){
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("You cannot modify a closed lien "+lien.lienCode);
                    entityResponse.setEntity("");
                }else {
                    Void modifyLien= lienRepository.modifyLien(lien.lienCode, lien.lienType, lien.priority,
                            lien.lienDescription,lien.lienAmount, lien.effectiveFrom,lien.expiryDate, lien.lienAdjustedAmount, user);
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setMessage("Lien with lien code "+lien.lienCode+ " modified successfully");
                    entityResponse.setEntity("");
                }

            }else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("Lien with code "+ lien.lienCode+ " cannot be found");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse closeLien(String lienCode){
        try {
            EntityResponse entityResponse= new EntityResponse<>();
            String user = UserRequestContext.getCurrentUser();
            Optional<LienRepository.checkLiensByLienCode> checkLien = lienRepository.checkLiensByLienCode(lienCode);

            if(checkLien.isPresent()){
                LienRepository.checkLiensByLienCode lienflag = checkLien.get();
                if(lienflag.getVerifiedFlag().equals("N")){
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage("Lien with lien code "+lienCode+ " is not verified, you cannot initiate a closure for unverified lien!");
                    entityResponse.setEntity("");
                    return entityResponse;
                }
                if(lienflag.getLienDeletedFlag().equals("Y")){
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage("Lien with lien code "+lienCode+ " was closed by "+lienflag.getDeletedBy()+" on "+lienflag.getLienDeletedTime());
                    entityResponse.setEntity("");
                    return entityResponse;
                }else {
                    Void lien= lienRepository.closeLienLienByLienCode(lienCode, user);
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage("Lien closure for lien code "+lienCode+ " was initiated successfully, waiting for approval.");
                    entityResponse.setEntity("");
                }
            }else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("You cannot close lien "+ lienCode+" lien code not found");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse verifyLienClose(String lienCode){
        try {
            EntityResponse entityResponse= new EntityResponse<>();
            String user = UserRequestContext.getCurrentUser();
            Optional<LienRepository.checkLiensByLienCode> checkLien = lienRepository.checkLiensByLienCode(lienCode);

            if(checkLien.isPresent()){
                LienRepository.checkLiensByLienCode lienflag = checkLien.get();
                if(lienflag.getLienDeletedFlag().equals("Y")){
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage("Lien with lien code "+lienCode+ " was closed by "+lienflag.getDeletedBy()+" on "+lienflag.getLienDeletedTime());
                    entityResponse.setEntity("");
                    return entityResponse;
                }
                if(lienflag.getVerifiedFlag().equals("N")){
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage("Lien with lien code "+lienCode+ " was not verified after creation, you cannot verify without the 1st verification!");
                    entityResponse.setEntity("");
                    return entityResponse;
                }
                if(lienflag.getDeletedBy() == null || lienflag.getDeletedBy() == "" ){
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage("Lien closure for lien code "+lienCode+ " is not yet initiated");
                    entityResponse.setEntity("");
                    return entityResponse;
                }
                if(lienflag.getDeletedBy().equals(user)){
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage("You cannot close a lien that you initiated its closure!");
                    entityResponse.setEntity("");
                    return entityResponse;
                }  else {
                    Void lien= lienRepository.verifyLienClosureByLienCode(lienCode, user);
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage("Lien with lien code "+lienCode+ " closed successfully");
                    entityResponse.setEntity("");
                }
            }else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("You cannot verify closure for lien "+ lienCode+" lien code not found");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getAllLiensByDestinationAccount(String acid){
        try {
            EntityResponse entityResponse= new EntityResponse<>();
            Optional<Account> account= accountRepository.findByAcidAndDeleteFlag(acid,CONSTANTS.NO);
            if(account.isPresent()){
                List<Lien> ls= lienRepository.findByDestinationAccount(account.get());
                if(ls.size()>0){
                    entityResponse.setStatusCode(HttpStatus.FOUND.value());
                    entityResponse.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    entityResponse.setEntity(ls);
                }else {
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("Provided account does not have liens");
                }
            }else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("Provided account id doesn't exist");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //update liens from not active to active after the effective date arises
    public EntityResponse updateLiensToActive(){
        try{
            EntityResponse entityResponse=new EntityResponse<>();
            List<Lien> liens= lienRepository.findEffectiveFromDateAfterAndStatus(new Date(), CONSTANTS.NOT_ACTIVE);
            AtomicReference<Integer> counter= new AtomicReference<>(0);
            if(liens.size()>0){
                liens.forEach(lien -> {
                    lien.setStatus(CONSTANTS.ACTIVE);
                    lienRepository.save(lien);
                    counter.getAndSet(counter.get() + 1);
                });

                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage(counter+" Liens updated");
            }else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("Could not find any liens effective from today");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse updateLiensToExpired(){
        try{
            EntityResponse entityResponse=new EntityResponse<>();
            List<Lien> liens= lienRepository.findExpiryDateAfterAndStatus(new Date(), CONSTANTS.ACTIVE);
            AtomicReference<Integer> counter= new AtomicReference<>(0);
            if(liens.size()>0){
                liens.forEach(lien -> {
                    lien.setStatus(CONSTANTS.L_EXPIRED);
                    lienRepository.save(lien);
                    counter.getAndSet(counter.get() + 1);
                });

                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage(counter+" Liens updated");

            }else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("Could not find any liens expiring today");
            }
            return entityResponse;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @Transactional
    public  List<Lien> activeLiens(){
        return lienRepository.findByStatusAndDeletedFlag(CONSTANTS.ACTIVE,
                CONSTANTS.NO);
    }

    public EntityResponse getAllActiveLiens(){
        try {
            EntityResponse res = new EntityResponse<>();
            List<Lien> liens=activeLiens();
            if(liens.size()>0){
                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.FOUND.value());
                res.setEntity(liens);
            }else {
                res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getAllSatisfiedLiens(){
        try {
            EntityResponse res = new EntityResponse<>();
            List<Lien> liens= lienRepository.findByStatusAndDeletedFlag(CONSTANTS.L_SATISFIED,
                    CONSTANTS.NO);
            if(liens.size()>0){
                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.FOUND.value());
                res.setEntity(liens);
            }else {
                res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public TransactionHeader createTransactionHeader(String currency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc){
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
