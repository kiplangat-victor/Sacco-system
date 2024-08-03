package com.emtechhouse.accounts.Utils;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.LienRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class AcidGenerator {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoanDemandRepository loanDemandRepository;

    @Autowired
    private LienRepository lienRepository;

    @Autowired
    private ItemServiceCaller itemServiceCaller;

    //TODO: ACID VALIDATORS
    public boolean findAccountExistsByAcid(String acid){
        try{
            Optional<Account> account=accountRepository.findByAcid(acid);
            if(account.isPresent()){
                return true;
            }else {
                return false;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return true;
        }
    }

    //TODO: GENERATORS
    public String generateAcid(String entityId, String productCode){
        try{
            Integer rnNumber= ThreadLocalRandom.current().nextInt(100000, 999999);
            String acid= entityId+productCode+String.valueOf(rnNumber);
            boolean acidExists= findAccountExistsByAcid(acid);
            do{
                rnNumber= ThreadLocalRandom.current().nextInt(100000, 999999);
            }while (acidExists);
            return  acid;
        }catch (Exception e) {
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }

//    public String generateAcidForSavingsAccount(String productCode, String customerCode){
//        try{
//            String innitialFive="11194";
//            String middleNumbers=productCode;
//            String lastFour="";
//            if(customerCode.length()<4){
//               lastFour=customerCode;
//            }else{
//                //get last four digits from customer code
//                lastFour=customerCode.substring(customerCode.length() - 4);
//            }
//            String acid=middleNumbers+"-"+lastFour;
//            return acid;
//        }catch (Exception e) {
//            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
//            return null;
//        }
//    }

    public String generateAcidForSavingsAccount(String productCode, String customerCode){
        try{
            String middleNumbers=productCode;
            String acid=middleNumbers+"-"+customerCode;
            return acid;
        }catch (Exception e) {
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }



    public String generateAcidForOfficeAccount(String glSubHead) {
        log.info("Generating acid");
        try {
            String firstThree = glSubHead;
            String remainingThreeDigits;

            Optional<String> maxAcid = accountRepository.findMaxAcidByGlSubHead(glSubHead);
            String mouse = maxAcid.get();
            System.out.println("Max Acid: "+mouse);
            if (maxAcid.isPresent()) {
                String lastThreeCharacters = maxAcid.get().substring(maxAcid.get().length() - 3);
                Long lastThreeDigits = Long.valueOf(lastThreeCharacters);
                log.info("Current code: {}", lastThreeDigits.toString());
                String newCode = String.format("%03d", (lastThreeDigits + 1));
                remainingThreeDigits = newCode;
                log.info("Remaining three digits: {}", remainingThreeDigits);
            } else {
                remainingThreeDigits = "001";
            }

            String generatedAcid = firstThree + remainingThreeDigits;
            Optional<Account> existingAccount = accountRepository.findByAcid(generatedAcid);

            if (!existingAccount.isPresent()){

                generatedAcid = firstThree + remainingThreeDigits;

                return generatedAcid;
            }else {
                String lastThreeCharacters = maxAcid.get().substring(maxAcid.get().length() - 3);
                Long lastThreeDigits = Long.valueOf(lastThreeCharacters);
                log.info("Current code: {}", lastThreeDigits.toString());
                String newCode = String.format("%03d", (lastThreeDigits + 2));
                remainingThreeDigits = newCode;
                log.info("Remaining three digits: {}", remainingThreeDigits);

                generatedAcid = firstThree + remainingThreeDigits;

                return generatedAcid;

            }

        } catch (Exception e) {
            log.error("Caught Error: ", e);
            return null;
        }
    }





    public String generateAcidForLoanAccount(String productCode){
        try{
            String firstThree=productCode;
            String remainingSixDigits="";

            String productCodeInit=productCode+"%";

            Optional<AccountRepository.AccountCode> accountCode= accountRepository.findAccountLastEntry(CONSTANTS.LOAN_ACCOUNT, productCode,productCodeInit);
            if(accountCode.isPresent()){
                log.info("Loan account present");
                //get last six digits
                String acid=accountCode.get().getAccountCode();
                log.info("Existing account acid: "+acid);
                String lastSixCharacters=acid.substring(acid.length() - 6);
                //convert into long
                Long lastSixDigits=Long.valueOf(lastSixCharacters);
                log.info("current code"+lastSixDigits.toString());
                String newCode= String.valueOf((lastSixDigits+1));

                do{
                    newCode="0"+newCode;
                }while(newCode.length()<6);

                remainingSixDigits=newCode;
                log.info("remainingSixDigits "+remainingSixDigits);
            }else {
                log.info("Loan  not account present");
                remainingSixDigits="000001";
            }
            String generatedAcid=firstThree+remainingSixDigits;
            return generatedAcid;
        }catch (Exception e) {
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }

    public String generateRunningNumbers(String productCode, Integer size,  String acType){
        try {
            log.info("Started generating running numbers");
            String runningNumbers="";
            String start= "1";

            String productCodeInit=productCode+"%";
            System.out.println("Codeinit: " + productCodeInit);

            int minsize = productCode.length()+ size;
            System.out.println("minsize: " +minsize);

            Optional<AccountRepository.AccountCode> accountCode= accountRepository. findAccountLastEntry(acType,
                    productCode,productCodeInit, minsize);

            log.info("account type :: "+acType);
            log.info("Product code :: "+productCode);
            if(accountCode.isPresent()){
                log.info("There is a present account");
                log.info("Running numbers size :: "+size);
                String acid=accountCode.get().getAccountCode();
                log.info("Existing Acid :: "+acid);
                String lastCharacters=acid.substring(acid.length() - size);
                log.info("last characters: "+lastCharacters);
                Long lastDigits=Long.valueOf(lastCharacters);
                String newCode= String.valueOf((lastDigits+1));
                do {
                    newCode="0"+newCode;
                    log.info("newCode :: "+ newCode);
                }while(newCode.length()<size);
                runningNumbers=newCode;
            }else {
                log.info("There is no present account");
                log.info("Size");
                do{
                    start = "0"+start;

                }while(start.length()<size);
                runningNumbers=start;
            }
            return runningNumbers;
        }catch (Exception e) {
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }

    public String generateRunningNumber(String start, Integer size){
        do{
            start = "0"+start;

        }while(start.length()<size);
        return start;
    }

    public String generateLienCode() {
        try{
            String firstTwo="LN";
            String remainingEightDigits="";

            Optional<LienRepository.LienCode> lienCode= lienRepository.findAccountLastEntryOfficeAccount();
            if(lienCode.isPresent()){
                //get last six digits
                String code=lienCode.get().getlienCode();
//                System.out.println(code);
                String lastSixCharacters=code.substring(code.length() - 8);
                //convert into long
                Long lastSixDigits=Long.valueOf(lastSixCharacters);
                log.info("current code"+lastSixDigits.toString());
                String newCode= String.valueOf((lastSixDigits+1));

                do{
                    newCode="0"+newCode;
                }while(newCode.length()<8);

                remainingEightDigits=newCode;
                log.info("remainingSixDigits "+remainingEightDigits);
            }else {
                remainingEightDigits="00000001";
            }
            String generatedAcid=firstTwo+remainingEightDigits;
            return generatedAcid;
        }catch (Exception e) {
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }

    public String generateDemandCode(String demandType) {
        return "demand";

    }

    public String generateAcid(String format,
                               String runningNumber,
                               String productCode,
                               String branchCode,
                               String glSubhead,
                               String customerCode){
        return format.replaceAll(CONSTANTS.ACID_BRANCH, branchCode)
                .replaceAll(CONSTANTS.ACID_PRODUCT, productCode)
                .replaceAll(CONSTANTS.ACID_RUNNO, runningNumber)
                .replaceAll(CONSTANTS.ACID_SUBGL, glSubhead)
                .replaceAll(CONSTANTS.ACID_MCODE, customerCode);
    }

    public EntityResponse generateAcid1(String prodCode,
                                        String acType,
                                        String branchCode,
                                        String glSubhead,
                                        String customerCode){
        try {
            log.info("Started acid generation process");
            EntityResponse res= new EntityResponse<>();
            //call product service
            res= itemServiceCaller.getGeneralProductDetail1(prodCode,acType);
            log.info("Product res status code :: "+res.getStatusCode());
            if(res.getStatusCode().equals(HttpStatus.OK.value())){
                GeneralProductDetails gp= (GeneralProductDetails) res.getEntity();
                String acidStructure= gp.getAcid_structure();
                Integer size= gp.getRunning_no_size();

                if(acidStructure !=null && size !=null){
                    log.info("acidStructure okay");
                    //check expected acid structure
                    String runningNumbers= generateRunningNumbers(prodCode, size,acType);

                    String acid= generateAcid(acidStructure,
                            runningNumbers,
                            prodCode,
                            branchCode,
                            glSubhead,
                            customerCode);



                    //check if acid exists
//                    add running numbers plus one

//                    //check if acid exists
//                    //add running numbers plus one

//                    Integer count=0;
//                    while (accountRepository.existsByAcid(acid)) {
//                        count++;
//                        log.info("looping :: "+acid);
//                        Integer runningNumber= Integer.valueOf(runningNumbers);
//                        runningNumber=runningNumber+1;
//                        runningNumbers= String.valueOf(runningNumber);
//
//                        runningNumbers=generateRunningNumber(runningNumbers, size);
//
//                        acid=generateAcid(acidStructure,
//                                runningNumbers,
//                                prodCode,
//                                branchCode,
//                                glSubhead,
//                                customerCode);
//
//                        if(count>100){
//                            break;
//                        }
//                    }

                    res.setEntity(acid);
                    System.out.println("The newly generated acid: " + acid);
                    res.setMessage(HttpStatus.OK.getReasonPhrase());
                    res.setStatusCode(HttpStatus.OK.value());
                }else {
                    log.info("Undefined acid structure or running numbers size");
                    res.setMessage("Undefined acid structure or running numbers size");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                return res;
            }
            //get format
            //get number of running numbers size
            return res;
        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }

}
