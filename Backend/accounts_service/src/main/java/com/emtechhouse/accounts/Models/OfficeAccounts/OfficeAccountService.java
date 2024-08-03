package com.emtechhouse.accounts.Models.OfficeAccounts;//package com.emtechhouse.accounts.Models.OfficeAccounts;
//import com.emtechhouse.accounts.Models.Accounts.Account;
//import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
//import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorsRepo;
//import com.emtechhouse.accounts.Utils.CONSTANTS;
//import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
//import com.emtechhouse.accounts.Utils.RESPONSEMESSAGES;
//import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.joda.time.LocalDate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Slf4j
//public class OfficeAccountService {
//    @Autowired
//    private ValidatorsRepo validatorsRepo;
//    @Autowired
//    private OfficeAccountRepo officeAccountRepo;
//    @Autowired
//    private ValidatorService validatorsService;
//
//    public EntityResponse<OfficeAccount> findByAcid(String acid) {
//        try{
//            EntityResponse userValidator= validatorsService.userValidator();
//            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                return userValidator;
//            }else{
//                Optional<OfficeAccount> officeAccount = officeAccountRepo.findByAcidAndDeleteFlag(acid,CONSTANTS.NO);
//                if(officeAccount.isPresent()){
//                    OfficeAccount exstingOfficeAccount = officeAccount.get();
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage(RESPONSEMESSAGES.RECORD_FOUND);
//                    response.setStatusCode(HttpStatus.FOUND.value());
//                    response.setEntity(exstingOfficeAccount);
//                    return response;
//                }else {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage(RESPONSEMESSAGES.RECORDS_NOT_FOUND);
//                    response.setStatusCode(HttpStatus.FOUND.value());
//                    response.setEntity("");
//                    return response;
//                }
//
//            }
//
//        }catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//    public EntityResponse<List<OfficeAccount>> listAllUndeletedAccounts(){
//        try{
//            EntityResponse userValidator= validatorsService.userValidator();
//            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                return userValidator;
//            }else {
//                List<OfficeAccount> officeAccounts=officeAccountRepo.findByDeleteFlag(CONSTANTS.NO);
//                EntityResponse listChecker = validatorsService.listLengthChecker(officeAccounts);
//                return listChecker;
//            }
//        }catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//
//    //TODO: VERIFY
//    public EntityResponse<?> verifyCreatedAccount(String acid){
//        try{
//            EntityResponse userValidator= validatorsService.userValidator();
//            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                return userValidator;
//            }else{
//                Optional<OfficeAccount> officeAccount= officeAccountRepo.findByAcidAndDeleteFlag(acid,CONSTANTS.NO);
//                if(officeAccount.isPresent()){
//                    OfficeAccount existingOfficeAccount=officeAccount.get();
//
//                    if(existingOfficeAccount.getVerifiedFlag().equals(CONSTANTS.YES)){
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage(RESPONSEMESSAGES.ACCOUNT_ALREADY_VERIFIED);
//                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        response.setEntity("");
//                        return response;
//                    }else {
//                        if (existingOfficeAccount.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
//                            EntityResponse response = new EntityResponse();
//                            response.setMessage(RESPONSEMESSAGES.VERIFICATION_ERROR);
//                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                            response.setEntity("");
//                            return response;
//                        }else{
//                            String verifiedBy=UserRequestContext.getCurrentUser();
//                            existingOfficeAccount.setVerifiedBy(verifiedBy);
//                            existingOfficeAccount.setVerifiedTime(LocalDate.now().toDate());
//                            existingOfficeAccount.setVerifiedFlag(CONSTANTS.YES);
//                            OfficeAccount sExistingOfficeAccount=officeAccountRepo.save(existingOfficeAccount);
//
//                            EntityResponse response = new EntityResponse();
//                            response.setMessage(RESPONSEMESSAGES.SUCCESS);
//                            response.setStatusCode(HttpStatus.OK.value());
//                            response.setEntity(sExistingOfficeAccount);
//                            return response;
//                        }
//                    }
//                }else {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage(RESPONSEMESSAGES.RECORDS_NOT_FOUND);
//                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                    response.setEntity("");
//                    return response;
//                }
//            }
//        }catch (Exception e) {
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }
//}
