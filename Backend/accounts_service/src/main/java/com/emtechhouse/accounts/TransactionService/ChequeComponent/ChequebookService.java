package com.emtechhouse.accounts.TransactionService.ChequeComponent;

import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DataNotFoundException;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class ChequebookService {
    private final ChequebookRepo chequebookRepo;
    private final TranHeaderRepository transrepo;

    public ChequebookService(ChequebookRepo chequebookRepo, TranHeaderRepository transrepo) {
        this.chequebookRepo = chequebookRepo;
        this.transrepo = transrepo;
    }


    public static String generateChequeCode(int len) {
        String chars = "0123456789";
        Random rnd = new Random();
        String M = "C";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        return M + sb;
    }
    public Chequebook addChequebook(Chequebook chequebook) {
        try {
            chequebook.setMicrCode(generateChequeCode(6));
            return chequebookRepo.save(chequebook);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Chequebook> findAllChequebooks() {
        try {
            return chequebookRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Chequebook findById(Long id){
        try{
            return chequebookRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Chequebook updateChequebook(Chequebook chequebook) {
        try {
            return chequebookRepo.save(chequebook);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteChequebook(Long id) {
        try {
            chequebookRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }



    public EntityResponse validateCheck(String chequeNo, Integer leafNo){
        EntityResponse response = new EntityResponse<>();
        Integer isExist = chequebookRepo.checkChequeExistance(chequeNo);
        if (isExist == 1) {
            Chequebook chequebook = chequebookRepo.findByMicrCodeAndEntityIdAndDeletedFlag(chequeNo, EntityRequestContext.getCurrentEntityId(), CONSTANTS.NO);
            log.info("Check found...");
            //check remaining no of leaves
            Integer isPresent = chequebookRepo.checkIfleafIsInrange(chequeNo,leafNo);
            if(isPresent==1){
                log.info("Leaf number exist in range ...");

                Date expryDate= chequebook.getExpiryDate();
                log.info("Expiry date :: "+ expryDate);
                Date today=new Date();
                if(today.before(expryDate)) {
                    log.info("Cheque is active");
                    //check if leave number has been used

                    Integer[] leafnumbers = transrepo.getChequeLeafNos();
                    log.info("Cheque leaf Numbers length "+leafnumbers.length );
                    if(Arrays.stream(leafnumbers).anyMatch(leafNo::equals)){
                        log.info("Cheque leaf number "+leafNo+" has been used");
                        response.setMessage("Cheque leaf number has been used");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }else {
                        log.info("Cheque leaf number has NOT been used");
                        response.setMessage("Cheque is active,Not used");
                        response.setStatusCode(HttpStatus.OK.value());
                    }



                }else if(today.equals(expryDate)) {
                    log.info("Cheque "+chequeNo+ " expires TODAY ");
                    response.setMessage("Cheque "+chequeNo+ " expires TODAY ");
                    response.setStatusCode(HttpStatus.OK.value());

                }else if(today.after(expryDate)){
                    log.info("Cheque "+chequeNo+ " expired ");
                    response.setMessage("Cheque "+chequeNo+ " expired ");
                    response.setEntity(chequebook);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }

            }else {
                log.info("Leaf number Does NOt exist in range of cheque No "+chequeNo);
                response.setMessage("Leaf number Does NOt exist in range of cheque No "+chequeNo);
                response.setEntity(chequebook);
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }


        }else {
            log.info("Cheque not found...");
            response.setMessage("Cheque does not exist");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());


        }
        return response;
    }




}
