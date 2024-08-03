package com.emtechhouse.accounts.Models.OfficeAccounts;//package com.emtechhouse.accounts.Models.OfficeAccounts;
//
//import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
//import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorsRepo;
//import com.emtechhouse.accounts.Utils.AcidGenerator;
//import com.emtechhouse.accounts.Utils.CONSTANTS;
//import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
//import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
//import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//
//@Service
//@Slf4j
//public class OfficeAccountRegistrationService {
//    @Autowired
//    private ValidatorsRepo validatorsRepo;
//    @Autowired
//    private ValidatorService validatorsService;
//    @Autowired
//    private OfficeAccountRepo officeAccountRepo;
//    @Autowired
//    private AcidGenerator acidGenerator;
//
//    public EntityResponse<OfficeAccount> registerAccount(OfficeAccount officeAccount){
//        try{
//            EntityResponse userValidator= validatorsService.userValidator();
//            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                return userValidator;
//            }else {
//                return postOfficeAccount(officeAccount);
//            }
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    public EntityResponse<OfficeAccount> postOfficeAccount(OfficeAccount officeAccount){
//        try{
//            String postedBy = UserRequestContext.getCurrentUser();
//            String EntityId= EntityRequestContext.getCurrentEntityId();
//            String prodCode=officeAccount.getProductCode();
//            String acid = acidGenerator.generateAcid(EntityId, prodCode);
//
//            officeAccount.setAccountBalance(0.0);
//            officeAccount.setPostedFlag(CONSTANTS.YES);
//            officeAccount.setPostedTime(new Date());
//            officeAccount.setOpeningDate(new Date());
//            officeAccount.setPostedBy(postedBy);
//            officeAccount.setDeleteFlag(CONSTANTS.NO);
//            officeAccount.setVerifiedFlag(CONSTANTS.NO);
//            officeAccount.setAccountBalance(0.0);
//            officeAccount.setAcid(acid);
//            officeAccount.setAccountStatus(CONSTANTS.ACTIVE);
//
//            OfficeAccount sOfficeAccount = officeAccountRepo.save(officeAccount);
//            EntityResponse response = new EntityResponse();
//            response.setMessage("Office account opened successfully");
//            response.setStatusCode(200);
//            response.setEntity(sOfficeAccount);
//            return response;
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//}
