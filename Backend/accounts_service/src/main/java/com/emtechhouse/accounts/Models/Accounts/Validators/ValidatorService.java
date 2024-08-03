package com.emtechhouse.accounts.Models.Accounts.Validators;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.LoanLimitDto.LoanLimitDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails.LoanAccountProduct;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails.TdaProdDto;
import com.emtechhouse.accounts.Models.Accounts.RelatedParties.RelatedParties;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorInterfaces.ProductItem;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorInterfaces.RetailCustomerItem;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.RESPONSEMESSAGES;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ValidatorService {
    @Autowired
    ValidatorsRepo validatorsRepo;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ItemServiceCaller itemServiceCaller;

    //TODO: USER VALIDATOR
    public EntityResponse<UserRequestContext> userValidator(){
        try{
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            }else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("---User Present------");
                    response.setStatusCode(200);
                    response.setEntity("");
                    return response;
                }
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //TODO: RETAIL CUSTOMER VALIDATOR
    public EntityResponse<RetailCustomerItem> retailCustomerValidator(String customerCode) {
        try{
            log.info("validating customer");
            Optional<RetailCustomerItem> retailCustomerItem=validatorsRepo.getRetailCustomerInfo(customerCode);
            if(retailCustomerItem.isPresent()) {
                log.info("customerPresent");
                RetailCustomerItem newRetailCustomerItem=retailCustomerItem.get();
                log.info("newRetailCustomerItem:"+ newRetailCustomerItem);
                if(newRetailCustomerItem.getdeleted_flag().equals(CONSTANTS.NO)) {
                    if(newRetailCustomerItem.getverified_flag().equals(CONSTANTS.YES)) {
                        log.info(" customer validated");
                        EntityResponse response = new EntityResponse();
                        response.setMessage("----Retail customer exists ------");
                        response.setStatusCode(200);
                        response.setEntity(newRetailCustomerItem);
                        return response;
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Failed! Customer is not verified");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(newRetailCustomerItem);
                        return response;
                    }
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Failed! Customer is deleted");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(newRetailCustomerItem);
                    return response;
                }
            }else {

                Optional<RetailCustomerItem> groupCustomerInfo=validatorsRepo.getGroupMemberInfo(customerCode);
                if(groupCustomerInfo.isPresent()){
                    log.info(" group customerPresent");
                    RetailCustomerItem newRetailCustomerItem=groupCustomerInfo.get();
                    if(newRetailCustomerItem.getdeleted_flag().equals('N')){
                        if(newRetailCustomerItem.getverified_flag().equals('Y')){
                            log.info("group customer validated");
                            EntityResponse response = new EntityResponse();
                            response.setMessage("----Retail customer exists ------");
                            response.setStatusCode(200);
                            response.setEntity(newRetailCustomerItem);
                            return response;
                        }else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Failed! Customer is not verified");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity(newRetailCustomerItem);
                            return response;
                        }
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Failed! Customer is deleted");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(newRetailCustomerItem);
                        return response;
                    }
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Failed! Customer code does not exist");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                }

//                EntityResponse response = new EntityResponse();
//                response.setMessage("Failed! Customer code does not exist");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return response;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //TODO: CHECK IF A CUSTOMER HAS ANOTHER SAVINGS ACCOUNT
    public EntityResponse retailCustomerNumberOfAccountValidator(String productCode, String customerCode){
        try{
            Optional<Account> account= accountRepository.findByProductCodeAndCustomerCode(productCode,customerCode);
            if(account.isPresent()){
                EntityResponse response = new EntityResponse();
                response.setMessage("Invalid! CUSTOMER ALREADY HAS AN ACCOUNT OF PRODUCT CODE: "+productCode);
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                return response;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //TODO: PRODUCT VALIDATOR
    public EntityResponse<ProductItem> productValidator(String productCode){
        try{
            log.info("validating product");
            Optional<ProductItem> productItem= validatorsRepo.getProductInfo(productCode);
            if(productItem.isPresent()){
                log.info("Product is present");
                System.out.println("Product details: "+productItem);
                ProductItem newProductItem=productItem.get();
                if(newProductItem.getdeleted_flag().equals('N')){
                    log.info("Product not deleted");
                    LocalDate today= LocalDate.now();
                    LocalDate effectiveFromDate= newProductItem.geteffective_from_date();
                    LocalDate effectiveToDate =newProductItem.geteffective_to_date();
                    if(newProductItem.getverified_flag().equals('Y')){
                        System.out.println("Product is verified");
                        if(effectiveFromDate.compareTo(today)<1){
                            //
                            if(effectiveToDate.compareTo(today)>0){
                                //
                                log.info("product exists");
                                EntityResponse response = new EntityResponse();
                                response.setMessage("---Product Ok-------");
                                response.setStatusCode(200);
                                response.setEntity(productItem);
                                return response;
                            }else {
                                //error
                                EntityResponse response = new EntityResponse();
                                response.setMessage("Failed! Product "+productCode+" expired on "+effectiveToDate);
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity(productItem);
                                return response;
                            }
                        }else {
                            //
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Failed! Product "+productCode+" will be effective from date: "+effectiveFromDate);
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity(productItem);
                            return response;
                        }
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Failed! Product "+productCode+" is not verified");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(productItem);
                        return response;
                    }
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Failed! A deleted Product "+productCode+" cannot be used");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(productItem);
                    return response;
                }
            }else {
                log.info("Product not present");
                EntityResponse response = new EntityResponse();
                response.setMessage("Failed! Product "+productCode+" code Name does not exist");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(productItem);
                return response;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //TODO: PRODUCT VALIDATOR
//    public EntityResponse<ProductItem> officeProductValidator(String productCode){
//        try{
//            Optional<ProductItem> productItem= validatorsRepo.getOfficeProductInfo(productCode);
//            if(productItem.isPresent()){
//                ProductItem newProductItem=productItem.get();
//                if(newProductItem.getdeleted_flag().equals('N')){
//                    if(newProductItem.getverified_flag().equals('Y')){
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("---Product Exists-------");
//                        response.setStatusCode(200);
//                        response.setEntity(productItem);
//                        return response;
//                    }else {
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("Failed! Product is not verified");
//                        response.setStatusCode(404);
//                        response.setEntity(productItem);
//                        return response;
//                    }
//                }else {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Failed! A deleted Product cannot be used");
//                    response.setStatusCode(404);
//                    response.setEntity(productItem);
//                    return response;
//                }
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("Failed! Product code Name does not exist");
//                response.setStatusCode(404);
//                response.setEntity(productItem);
//                return response;
//            }
//        }catch (Exception e){
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }

    //TODO: CHECK IF A LIST HAS ITEMS
    public EntityResponse<?> listLengthChecker(List<?> list){
        try{
            Integer length= list.size();
            if(length>0){
                EntityResponse response = new EntityResponse();
                response.setMessage("Records Found");
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(list);
                return response;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage(RESPONSEMESSAGES.RECORDS_NOT_FOUND);
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("");
                return response;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    //TODO: CHECK IF THE ACCOUNTS HAS RELATED PARTIES
    public boolean hasRelatedParties(Account account){
        try{
            if(Objects.isNull(account.getRelatedParties())){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return false;
        }
    }
    public boolean hasSavingsAcDetails(Account account){
        try{
            if(Objects.isNull(account.getSavings())){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return false;
        }
    }
    public boolean hasLoanAcDetails(Account account){
        try{
            if(Objects.isNull(account.getLoan())){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return false;
        }
    }
    public boolean hasTermDepositAcDetails(Account account){
        try{
            if(Objects.isNull(account.getTermDeposit())){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return false;
        }
    }

    public boolean hasCurrentAccountDetails(Account account){
        try{
            if(Objects.isNull(account.getCurrentAccount())){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return false;
        }
    }

    public boolean hasOfficeAccountDetails(Account account){
        try{
            if(Objects.isNull(account.getOfficeAccount())){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return false;
        }
    }
//    TODO: CHECK ACCOUNT TYPE
    public String getAccountType(Account account){
        try{
            if (hasLoanAcDetails(account)) {
                return "LAA";
            }else if(hasSavingsAcDetails(account)){
                return "SBA";
            }else if(hasTermDepositAcDetails(account)){
                return "TDA";
            }else {
                return "UNKNOWN";
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    //TODO: CHECK IF THERE ARE DUPLICATES IN RELATED PARTIES DETAILS
//    public EntityResponse<RelatedParties> relatedPartiesHasDuplicates(List<RelatedParties> relatedParties){
//        try {
//            List<String> customerCode=relatedParties.stream().map(x->x.getCustomerCode()).filter(y-> Collections.frequency());
//        }catch (Exception e){
//            log.info("Caught Error {}"+e);
//            return false;
//        }
//    }

    //TODO: VALIDATE ACCOUNT TYPE AND ACCOUNT JSON ATTACHMENTS
    //TODO: CONFIRMING ACCOUNT TYPE

    public EntityResponse invalidAccountTypeMessage(){
        try {
            EntityResponse response = new EntityResponse();
            response.setMessage("Invalid! Account type does not match with the submitted account details");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            return response;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public EntityResponse<?> accountTypeValidator(Account account){
        try{
            String accountType= account.getAccountType();
            log.info("account_type--->"+accountType);

            if(accountType.equals(CONSTANTS.SAVINGS_ACCOUNT)){
                if(!hasOfficeAccountDetails(account) && hasSavingsAcDetails(account) && !hasLoanAcDetails(account) && !hasTermDepositAcDetails(account) && !hasCurrentAccountDetails(account)){
                    EntityResponse response = new EntityResponse();
                    response.setMessage("--OK-----");
                    response.setStatusCode(200);
                    response.setEntity(account);
                    return response;
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Invalid! Account type does not match with the submitted account details");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(account);
                    return response;
                }
            }else if(accountType.equals(CONSTANTS.LOAN_ACCOUNT)){
                if(!hasOfficeAccountDetails(account) && !hasSavingsAcDetails(account) && hasLoanAcDetails(account) && !hasTermDepositAcDetails(account) && !hasCurrentAccountDetails(account)){
                    log.info("loanAccount");
                    EntityResponse response = new EntityResponse();
                    response.setMessage("--OK-----");
                    response.setStatusCode(200);
                    response.setEntity(account);
                    return response;
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Invalid! Account type does not match with the submitted account details");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(account);
                    return response;
                }
            }else if(accountType.equals(CONSTANTS.TERM_DEPOSIT)){
                if(!hasOfficeAccountDetails(account) && !hasSavingsAcDetails(account) && !hasLoanAcDetails(account) && hasTermDepositAcDetails(account) && !hasCurrentAccountDetails(account)){
                    EntityResponse response = new EntityResponse();
                    response.setMessage("--OK-----");
                    response.setStatusCode(200);
                    response.setEntity(account);
                    return response;
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Invalid! Account type does not match with the submitted account details");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(account);
                    return response;
                }
            }else if(accountType.equals(CONSTANTS.OFFICE_ACCOUNT)){
                if(!hasSavingsAcDetails(account) && !hasLoanAcDetails(account) && !hasTermDepositAcDetails(account)  && !hasCurrentAccountDetails(account) && hasOfficeAccountDetails(account)){
                    EntityResponse response = new EntityResponse();
                    response.setMessage("--OK-----");
                    response.setStatusCode(200);
                    response.setEntity(account);
                    return response;
                }else {
                    //
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Invalid! Account type does not match with the submitted account details");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(account);
                    return response;
                }
//            }else if(accountType.equals(CONSTANTS.CURRENT_ACCOUNT)){
//                if(!hasOfficeAccountDetails(account) && !hasSavingsAcDetails(account) && !hasLoanAcDetails(account) && !hasTermDepositAcDetails(account) && hasCurrentAccountDetails(account)){
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("--OK-----");
//                    response.setStatusCode(200);
//                    response.setEntity(account);
//                    return response;
//                }else {
//                    //
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("Invalid! Account type does not match with the submitted account details");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity(account);
//                    return response;
//                }
            }else{
                // throw an error
                EntityResponse response = new EntityResponse();
                response.setMessage("Invalid account type");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(account);
                return response;
            }

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    //TODO: VALIDATE IF ALL RELATED PARTIES CIF ID ARE REGISTERED AND VERIFIED
    //TODO: LATER CONFIRM THAT THERE ARE NO REPETITIVE RELATED PARTIES
    public EntityResponse<RelatedParties> relatedPartiesValidator(List<RelatedParties> relatedParties){
        try{
            log.info("validating related parties");
            Integer i=0;
            EntityResponse response = new EntityResponse();
            response.setMessage(RESPONSEMESSAGES.SUCCESS);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(relatedParties);

            while(i<relatedParties.size()){
                log.info("num---"+i.toString());
                RelatedParties relatedParty= relatedParties.get(i);
                String customerCode=relatedParty.getRelPartyCustomerCode();
                Optional<RetailCustomerItem> retailCustomerItem=validatorsRepo.getRetailCustomerInfo(customerCode);
                if(retailCustomerItem.isPresent()){
                    RetailCustomerItem existingRetailCustomerItem= retailCustomerItem.get();
                    if(existingRetailCustomerItem.getdeleted_flag().equals('N')){
                        if(existingRetailCustomerItem.getverified_flag().equals('Y')){
                            i++;
                            continue;
                        }else {
                            response.setMessage("Custormer Code: "+customerCode+" for related party has not been verified");
                            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                            response.setEntity(relatedParties);
                            break;
                        }
                    }else {
                        response.setMessage("Custormer Code: "+customerCode+" for related party was deleted");
                        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                        response.setEntity(relatedParties);
                        break;
                    }
                }else {
                    response.setMessage("Custormer Code: "+customerCode+" for related party of does not exist");
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    response.setEntity(relatedParties);
                    break;
                }

            }
            return response;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    /**---------------------------------------------LOANS VALIDATION-------------------------------------------------------**/
    //TODO: LOAN AMOUNT VALIDATORS (MIN AND MAX)
//    public EntityResponse<?> loanAmountValidator(Account account){
//        try{
//            Double minmunum= -1.0;
//            Double maximum =0.0;
//            if(hasLoanAcDetails(account)){
//
//                Loan loan= account.getLoan();
//                String productCode= account.getProductCode();
//                EntityResponse loanProductMinMaxHolderResponse= getLoanProductMinMaxHolder(productCode);
//                if(loanProductMinMaxHolderResponse.getStatusCode().equals(HttpStatus.NOT_FOUND.value())){
//                    //throw error
//                }else {
//                    ProductMinMaxHolder newProductMinMaxHolder= (ProductMinMaxHolder) loanProductMinMaxHolderResponse.getEntity();
//                    minmunum= Double.valueOf(newProductMinMaxHolder.getMinAmt());
//                    maximum= Double.valueOf(newProductMinMaxHolder.getMaxAmt());
//                    log.info(newProductMinMaxHolder.toString());
//                }
//            }
//
//            Loan laonAcc=account.getLoan();
//            Double principalAmount=laonAcc.getPrincipalAmount();
//            if(principalAmount<minmunum){
//                EntityResponse response = new EntityResponse();
//                response.setMessage("Principal amount is less than product minimum");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity(account);
//                return response;
//            }else if(principalAmount > maximum){
//                EntityResponse response = new EntityResponse();
//                response.setMessage("Principal amount is greater than product maximum");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity(account);
//                return response;
//            }else {
//                //okay
//                EntityResponse response = new EntityResponse();
//                response.setMessage("--OK-----");
//                response.setStatusCode(200);
//                response.setEntity(account);
//                return response;
//            }
//
//        }catch (Exception e){
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }

    public EntityResponse loanAmountAndPeriodValidator(Double principalAmount,
                                                       Long loanPeriodInMonths,
                                                       String prodCode){
        try{
            EntityResponse loanAccountProductEntity= itemServiceCaller.getLoanAccountProductDetails(prodCode);

            if(loanAccountProductEntity.getStatusCode().equals(HttpStatus.OK.value())){
                LoanAccountProduct loanAccountProduct= (LoanAccountProduct) loanAccountProductEntity.getEntity();
                Double productMin= loanAccountProduct.getLoan_amt_min();
                Double productMax =loanAccountProduct.getLoan_max_amt();

                Long loanMinimumPeriodInMonths=loanAccountProduct.getLoan_period_min_mm();
                Long loanMaximumPeriodInMonths=loanAccountProduct.getLoan_period_max_mm();

                if(principalAmount>productMax){
                    EntityResponse response = new EntityResponse();
                    response.setMessage("MAXIMUM LOAN AMOUNT FOR THIS PRODUCT IS :"+productMax);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return response;
                } else if (principalAmount<productMin) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("MINIMUM LOAN AMOUNT FOR THIS PRODUCT IS :"+productMin);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return response;
                }else {
                    if(loanPeriodInMonths<loanMinimumPeriodInMonths){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MINIMUM LOAN PERIOD FOR THIS PRODUCT IS :"+loanMinimumPeriodInMonths.toString()+" MONTHS");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    }else if(loanPeriodInMonths>loanMaximumPeriodInMonths){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MAXIMUM LOAN PERIOD FOR THIS PRODUCT IS :"+loanMaximumPeriodInMonths.toString()+" MONTHS");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("--OK-----");
                        response.setStatusCode(200);
                        response.setEntity(HttpStatus.OK.value());
                        return response;
                    }
                }
            }else {
                return loanAccountProductEntity;
            }

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }


    /****-------------------------------------ACCOUNTS VALIDATION------------------------------------------**/
    //TODO: ACCOUNT VALIDATOR
    public EntityResponse<?> acidValidator(String acid, String accountName){
        try{
            log.info("validating acid");
            Optional<Account> account = accountRepository.findByAccountId(acid);
            if(account.isPresent()){
                Account existingAccount =account.get();
                if(existingAccount.getDeleteFlag().equals(CONSTANTS.NO)){
                    if(existingAccount.getVerifiedFlag().equals(CONSTANTS.YES)){
                        if(existingAccount.getAccountStatus().equals(CONSTANTS.ACTIVE)){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("----Account Ok-------");
                            response.setStatusCode(200);
                            response.setEntity(existingAccount);
                            return response;
                        }else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Failed! Account not active, Account Number "+acid);
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return response;
                        }
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Failed! Account not verified, Account Number "+acid);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return response;
                    }
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Failed! deleted Account, Account Number "+acid);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                }
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("Failed! "+accountName+" Account id does not exist, Account Number "+acid);
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public EntityResponse<?> acidValidator(String acid){
        try{
            log.info("validating acid");
            Optional<Account> account = accountRepository.findByAccountId(acid);
            if(account.isPresent()){
                Account existingAccount =account.get();
                if(existingAccount.getDeleteFlag().equals(CONSTANTS.NO)){
                    if(existingAccount.getVerifiedFlag().equals(CONSTANTS.YES)){
                        if(existingAccount.getAccountStatus().equals(CONSTANTS.ACTIVE)){
                            EntityResponse response = new EntityResponse();
                            response.setMessage("----Account Ok-------");
                            response.setStatusCode(200);
                            response.setEntity(existingAccount);
                            return response;
                        }else {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Failed! Account not active, Account Number "+acid);
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return response;
                        }
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Failed! Account not verified, Account Number "+acid);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return response;
                    }
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Failed! deleted Account, Account Number "+acid);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                }
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("Failed!  Account id does not exist, Account Number "+acid);
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public EntityResponse<?> acidDebitValidator(Account account, Double amount){
        try{
            Double accountBalance =account.getAccountBalance();
            Double lienAmount = account.getLienAmount();
            Double withdrawableAmount =accountBalance-lienAmount;

            //account type
            String accountType= account.getAccountType();
            EntityResponse response = new EntityResponse();
            if (accountType.equals(CONSTANTS.LOAN_ACCOUNT) || accountType.equals(CONSTANTS.OFFICE_ACCOUNT) || accountType.equals(CONSTANTS.TERM_DEPOSIT)) {
                response.setMessage("----Account Ok-------");
                response.setStatusCode(200);
            } else {
                if(withdrawableAmount < amount){
                    response.setMessage("Insufficient funds, Account withdrawable amount is: "+ withdrawableAmount.toString()+" Account Number "+account.getAcid());
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
                    response.setMessage("----Account Ok-------");
                    response.setStatusCode(200);
                }
            }
            response.setEntity("");
            return response;

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public EntityResponse<?> acidDebitLienValidator(Account account, Double amount){
        try{
            Double accountBalance =account.getAccountBalance();
            Double withdrawableAmount =accountBalance;

            //account type
            String accountType= account.getAccountType();
            EntityResponse response = new EntityResponse();
            if (accountType.equals(CONSTANTS.LOAN_ACCOUNT) || accountType.equals(CONSTANTS.OFFICE_ACCOUNT)) {
                response.setMessage("----Account Ok-------");
                response.setStatusCode(200);
            } else {
                if(withdrawableAmount < amount){
                    response.setMessage("Insufficient funds, Account withdrawable amount is: "+ withdrawableAmount.toString()+" Account Number "+account.getAcid());
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }else {
                    response.setMessage("----Account Ok-------");
                    response.setStatusCode(200);
                }
            }
            response.setEntity("");
            return response;

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public EntityResponse<?> acidWithdrawalValidator(Account account){
        try{
            Boolean isWithdrawalAllowed =account.getIsWithdrawalAllowed();
            if(isWithdrawalAllowed){
                EntityResponse response = new EntityResponse();
                response.setMessage("----Account Ok-------");
                response.setStatusCode(200);
                response.setEntity("");
                return response;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("WITHDRWALS NOT PERMITTED FROM THIS ACCOUNT,  ACCOUNT ID: "+account.getAcid());
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public EntityResponse<?> acidWithdrawalValidator(String acid){
        try{
            Account account= accountRepository.findByAcid(acid).orElse(null);
            Boolean isWithdrawalAllowed =account.getIsWithdrawalAllowed();
            if(isWithdrawalAllowed){
                EntityResponse response = new EntityResponse();
                response.setMessage("----Account Ok-------");
                response.setStatusCode(200);
                response.setEntity("");
                return response;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("WITHDRWALS NOT PERMITTED FROM THIS ACCOUNT, ACCOUNT ID: "+acid);
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //TODO: LOAN LIMIT VALIDATOR
    //should be 3 * of Savings Non Withdrawable account balance
    //mwamba
    public EntityResponse validateLoanLimit(String customerCode, Double principalAmt){
        try{
            Optional<AccountRepository.AccountBalanceByAccountType> acBal =accountRepository.getAccountBalanceByCustomerType(customerCode, CONSTANTS.SAVINGS_NON_WITHDRAWABLE_PROD);
            if(acBal.isPresent()){
                Double accountBalance=acBal.get().getaccount_balance();
                String acid= acBal.get().getacid();

                Double loanLimit =accountBalance *3;
                EntityResponse response = new EntityResponse();
                if(principalAmt<=loanLimit){
                    response.setMessage("----Account Ok-------");
                    response.setStatusCode(HttpStatus.OK.value());
                }else {
                    response.setMessage("FAILED! LOAN LIMIT FOR CUSTOMER: "+ customerCode+" IS : "+loanLimit);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
                return response;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("MEMBER DOES NOT HAVE A DEPOSIT CONTRIBUTION ACCOUNT OF PRODUCT CODE: "+CONSTANTS.SAVINGS_NON_WITHDRAWABLE_PROD);
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //TODO: INDIVIDUAL LOAN LIMIT VALIDATOR
    public EntityResponse validateIndividualLoanLimit(String customerCode,
                                                      Double principalAmt,
                                                      String productCode){
        try{
            EntityResponse res = new EntityResponse();
            res.setMessage("----Account Ok-------");
            res.setStatusCode(HttpStatus.OK.value());
            //get loan limit definitions from product
            EntityResponse prodLes= itemServiceCaller.getLoanLimitsDetails(productCode);
            System.out.println("Prod les"+ prodLes.toString());
            if(prodLes.getStatusCode().equals(HttpStatus.OK.value())){
                log.info("Product limits found");
                List<LoanLimitDto> loanLimitDtos= (List<LoanLimitDto>) prodLes.getEntity();
                log.info("Product limits size :: "+loanLimitDtos.size());
                for( Integer i=0; i<loanLimitDtos.size(); i++){
                    LoanLimitDto ld= loanLimitDtos.get(i);
                    if(ld.getConditionType().equals(CONSTANTS.accMultiplier)) {
                        log.info("Found account balance limit :: ");
                        //get product code
                        // get mulitiplier
                        String prodCode= ld.getProductCode();
                        Double multiplier =ld.getAccMultiplier();

                        //get cutsomer account balance
                        Optional<AccountRepository.AccountBalanceByAccountType> acBal =accountRepository.getAccountBalanceByCustomerType(customerCode,
                                prodCode);
                        if(acBal.isPresent()){
                            //check if the account is active
                            if(acBal.get().getaccount_status().equals(CONSTANTS.ACTIVE)) {
                                Double accountBal=acBal.get().getaccount_balance();
                                Double loanLimit= accountBal*multiplier;
                                log.info("Individual Loan limit is :: "+ loanLimit);

                                if(principalAmt<=loanLimit){
                                    res.setMessage("----Account Ok-------");
                                    res.setStatusCode(HttpStatus.OK.value());
                                }else {
                                    res.setMessage("FAILED! LOAN LIMIT FOR CUSTOMER: "+ customerCode+" IS : "+loanLimit);
                                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }
                            }else {
                                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                res.setMessage("Customers account of product code "+prodCode+" is not active " +
                                        "for loan limit validation");
                            }
                        }else {
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            res.setMessage("Customer does not have an account of product code: "+prodCode+" " +
                                    "for loan limit validation");
                        }

                    }
                }
            }
            return res;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }


    //todo: term deposit validation
    public EntityResponse termDepositValidation(Double principalAmount,
                                                       Integer tdaPeriodInMonths,
                                                       String prodCode){
        try{
            EntityResponse tdaRes= itemServiceCaller.getTdaSpecificDetails(prodCode);

            if(tdaRes.getStatusCode().equals(HttpStatus.OK.value())){
                TdaProdDto td= (TdaProdDto) tdaRes.getEntity();
                Double productMin= td.getTda_deposit_amt_min();
                Double productMax =td.getTda_deposit_amt_max();

                Integer loanMinimumPeriodInMonths=td.getTda_period_mm_min();
                Integer loanMaximumPeriodInMonths=td.getTda_period_mm_max();

                if(principalAmount>productMax){
                    EntityResponse response = new EntityResponse();
                    response.setMessage("MAXIMUM  AMOUNT FOR THIS PRODUCT IS :"+productMax);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return response;
                } else if (principalAmount<productMin) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("MINIMUM AMOUNT FOR THIS PRODUCT IS :"+productMin);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return response;
                }else {
                    if(tdaPeriodInMonths<loanMinimumPeriodInMonths){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MINIMUM PERIOD FOR THIS PRODUCT IS :"+loanMinimumPeriodInMonths.toString()+" MONTHS");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    }else if(tdaPeriodInMonths>loanMaximumPeriodInMonths){
                        EntityResponse response = new EntityResponse();
                        response.setMessage("MAXIMUM PERIOD FOR THIS PRODUCT IS :"+loanMaximumPeriodInMonths.toString()+" MONTHS");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("--OK-----");
                        response.setStatusCode(200);
                        response.setEntity(HttpStatus.OK.value());
                        return response;
                    }
                }
            }else {
                return tdaRes;
            }

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

}
