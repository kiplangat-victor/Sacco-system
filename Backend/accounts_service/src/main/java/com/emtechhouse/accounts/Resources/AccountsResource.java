package com.emtechhouse.accounts.Resources;

import com.emtechhouse.accounts.Exception.ApiRequestException;
import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountCustomRepo;
import com.emtechhouse.accounts.Models.Accounts.AccountDocuments.AccountDocument;
import com.emtechhouse.accounts.Models.Accounts.AccountDocuments.AccountDocumentService;
import com.emtechhouse.accounts.Models.Accounts.AccountDtos.SearchRequestDto;
import com.emtechhouse.accounts.Models.Accounts.AccountDtos.SmsDto;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountCreation.GoodWays.AccountRegistrationServiceGoodWays;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountModification.AccountModificationGoodways;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountService;
import com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces.AccountLookUpInterface;
import com.emtechhouse.accounts.Models.Accounts.FiltersSpecification;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCollateral.LoanCollateralService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanLimit.LoanLimiter;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.EmailDto.MailDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.EventReqDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeesService;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionUtils;
import com.emtechhouse.accounts.Utils.*;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("accounts")
@RestController
public class AccountsResource {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ValidatorService validatorService;
    //    @Autowired
//    private AccountRegistrationService accountRegistrationService;
//    @Autowired
//    private AccountModificationService accountModificationService;
    @Autowired
    private ServiceCaller serviceCaller;
    @Autowired
    private AccountDocumentService accountDocumentService;
    @Autowired
    private FiltersSpecification filtersSpecification;
    @Autowired
    private TransactionUtils transactionUtils;

    @Autowired
    private ItemServiceCaller itemServiceCaller;

    @Autowired
    private AccountRegistrationServiceGoodWays accountRegistrationService;

    @Autowired
    private AcidGenerator acidGenerator;

    @Autowired
    private AccountModificationGoodways accountModificationService;

    @Autowired
    private LoanCollateralService loanCollateralService;
    @Autowired
    private ProductFeesService productFeesService;

    @Autowired
    private DatesCalculator datesCalculator;


    @PutMapping("aa/send/sms")
    public ResponseEntity<?> sendSms(@RequestBody SmsDto smsDto) {
        try {
            EntityResponse res = serviceCaller.sendSms(smsDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("aa/collateral/details")
    public ResponseEntity<?> getCollateralDetails(@RequestParam String colateralCode, @RequestParam Integer otp) {
        try {
            EntityResponse res = itemServiceCaller.verifyCollateralOtp(colateralCode, otp);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }


    @PutMapping("aa/statement/details")
    public ResponseEntity<?> getAccountStatement(@RequestBody List<EventReqDto> evts) {
        try {
            EntityResponse res = productFeesService.getProductFees3(evts);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("/send/collateral/verification/otp")
    public ResponseEntity<?> sendCollateralOtp(@RequestParam String colateralCode) {
        try {
            EntityResponse res = loanCollateralService.sendCollateralVerificationCode(colateralCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("/get/customer/email")
    public ResponseEntity<?> getCustomerEmail(@RequestParam String customerCode) {
        try {
            EntityResponse res = itemServiceCaller.getCustomerEmail(customerCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("/send/email")
    public ResponseEntity<?> sendEmail(@RequestBody MailDto mailDto) {
        try {
            EntityResponse res = itemServiceCaller.sendEmail(mailDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("/verify/collateral/otp")
    public ResponseEntity<?> verifyCollateralOtp(@RequestParam String colateralCode, @RequestParam Integer otp) {
        try {
            EntityResponse res = loanCollateralService.collateralOtpVerification(colateralCode, otp);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("prod/details")
    public ResponseEntity<?> getGeneralProdDetails(@RequestParam String productCode) {
        try {
            EntityResponse res = itemServiceCaller.getTdaSpecificDetails(productCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("prod/specific/details")
    public ResponseEntity<?> getSpecificProdDetails(@RequestParam String productCode) {
        try {
            EntityResponse res = itemServiceCaller.getLoanAccountProductDetails(productCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("get/auto/created/accounts")
    public ResponseEntity<?> getAutoCreatedAccounts(@RequestParam String productCode) {
        try {
            EntityResponse res = itemServiceCaller.getAutoCreatedAccountProducts(productCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("specification")
    public ResponseEntity<?> specification(@RequestBody SearchRequestDto searchRequestDto) {
        try {
            Specification<Account> specification = filtersSpecification.getSearchSpecification(searchRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(accountRepository.findAll(specification));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PutMapping("specification2")
    public ResponseEntity<?> specification2(@RequestBody List<SearchRequestDto> searchRequestDtos) {
        try {
            Specification<Account> specification = filtersSpecification.getSearchSpecification(searchRequestDtos);
            return ResponseEntity.status(HttpStatus.CREATED).body(accountRepository.findAll(specification));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PostMapping("open")
    public ResponseEntity<?> openAccount(@RequestBody Account account, HttpServletRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(accountRegistrationService.registerAccount(account));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
//            return null;
            throw new ApiRequestException("failed");
        }
    }

    @PostMapping("validate/general/details")
    public ResponseEntity<?> validateAccountsGeneralDetails(@RequestBody Account account) {
        try {
            return ResponseEntity.ok().body(accountRegistrationService.generalValidation(account));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PostMapping("validate/specific/details")
    public ResponseEntity<?> validateAccountsSpcificDetails(@RequestBody Account account) {
        try {
            return ResponseEntity.ok().body(accountRegistrationService.validateSpecificDetails(account));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }

    }

    @GetMapping("{acid}")
    public ResponseEntity<EntityResponse<Account>> retrieveAccount(@PathVariable("acid") String acid) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(accountService.retrieveAccount(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    @GetMapping("loanLimit/{acid}")
    public  ResponseEntity<EntityResponse<Account>> getLoanLimit(@PathVariable("acid") String acid) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(accountService.getLoanLimit(acid));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @GetMapping("loan/limiter")
    public ResponseEntity<EntityResponse<LoanLimiter>> loanLimit(@RequestParam String customerCode, @RequestParam String productCode) {
        try {
            Double totalLoanGiven = accountRepository.sumTotalLoansByCustomerCodeAndProductCode(customerCode,productCode)*-1;
            Double savingsBalance = accountRepository.sumTotalSavingsByCustomerCodeAndProductCode(customerCode);

            System.out.println("The total current ongoing loan for "+customerCode+" is: "+totalLoanGiven+ " and total savings is: "+savingsBalance);
            System.out.println("loan limit is: "+(((savingsBalance*3) - totalLoanGiven)));

            EntityResponse<LoanLimiter> entityResponse = new EntityResponse<>();
            entityResponse.setStatusCode(HttpStatus.FOUND.value());
            entityResponse.setMessage("Loan limit");
            entityResponse.setEntity(new LoanLimiter(customerCode, productCode, (savingsBalance*3 - totalLoanGiven), new Date()));
            return ResponseEntity.ok().body(entityResponse);

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("loan/mobile_balance")
    public ResponseEntity<?> mobileLoanBalance(@RequestParam String acid){
        try {
            // Get balance in accounts
            String customerCode = accountRepository.getTheCustomerCode(acid);
            Double loanBlAccounts = accountRepository.getMobileLoanBalance(customerCode);
            Double interestAmount = accountRepository.getMobileLoanInterestAmount(customerCode)*-1;
            log.info("The Loan amount: "+loanBlAccounts +" And interest Amount: "+interestAmount);
            System.out.println("Total Loan balance is: "+(loanBlAccounts + interestAmount));
            EntityResponse entityResponse = new EntityResponse<>();
            entityResponse.setStatusCode(HttpStatus.FOUND.value());
            entityResponse.setMessage("Total Loan balance");
            entityResponse.setEntity((loanBlAccounts + interestAmount));
            return ResponseEntity.ok().body(entityResponse);
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    @GetMapping("/check/by/acid/{acid}")
    public ResponseEntity<?> checkByAcid(@PathVariable("acid") String acid) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Account> accountExists  = accountRepository.checkByAcid(acid);
                    System.out.println("Acid: "+acid);
                    if (accountExists.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("true");
                        System.out.println("RESPONSE1: "+response);
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("false");
                        System.out.println("RESPONSE2: "+response);
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception exc) {
            EntityResponse response = new EntityResponse(exc.getLocalizedMessage(), null, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.ok().body(response);
        }
    }



    @GetMapping("bynationalid")
    public ResponseEntity<EntityResponse<Account>> retrieveAccount(@RequestParam String nationalId, @RequestParam String customerCode) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(accountService.retrieveAccount(nationalId, customerCode));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("available-amount/{acid}")
    public ResponseEntity<EntityResponse<Account>> availableAmount(@PathVariable("acid") String acid) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transactionUtils.availableBalanceWithEntity(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("by/id/{id}")
    public ResponseEntity<EntityResponse<Account>> retrieveAccount(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(accountService.retrieveAccount(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("update")
    public ResponseEntity<?> updateAccount(@RequestBody Account account) {
        try {
            return ResponseEntity.ok().body(accountModificationService.modifyAccount(account));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @GetMapping("all")
    public ResponseEntity<EntityResponse<List<Account>>> allAccounts() {
        try {
            return ResponseEntity.ok().body(accountService.listAllUndeletedAccounts());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("find/by/account/type")
    public ResponseEntity<EntityResponse<List<AccountRepository.AccountsByAccountType>>> fetchByAccountType(String accountType) {
        try {
            return ResponseEntity.ok().body(accountService.fetchByAccountType(accountType));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("find/by/glcode")
    public ResponseEntity<EntityResponse<List<AccountRepository.AccountsByAccountType>>> fetchByGlCode(String glCode) {
        try {
            return ResponseEntity.ok().body(accountService.fetchByGlCode(glCode));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("find/tellers/account/")
    public ResponseEntity<EntityResponse<List<Account>>> fetchTellersAccounts() {
        try {
            return ResponseEntity.ok().body(accountService.fetchTellersAccounts());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("find/by/account/type/look/up")
    public ResponseEntity<EntityResponse<List<?>>> fetchByAccountTypeForLookup(String accountType) {
        try {
            return ResponseEntity.ok().body(accountService.fetchByAccountTypeForLookup(accountType));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("get/all/repayment/accounts/look/up")
    public ResponseEntity<EntityResponse<List<?>>> fetchAllRepaymentAccountsForLookUp() {
        try {
            return ResponseEntity.ok().body(accountService.fetchAllRepaymentAccountsForLookUp());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //FIND ACCOUNT IMAGES
    @GetMapping("find/account/images/{acid}")
    public ResponseEntity<?> getAccountImages(@PathVariable String acid) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<Account> account = accountRepository.findByAcid(acid);
            if (account.isPresent()) {
                response = serviceCaller.getCustomerImages(account.get().getCustomerCode());

//                response.setMessage(HttpStatus.OK.getReasonPhrase());
//                response.setStatusCode(HttpStatus.OK.value());
//                List<Customerimages> customerimages = (List<Customerimages>) serviceCaller.getCustomerImages(account.get().getCustomerCode()).getEntity();
//                response.setEntity(customerimages);
                return ResponseEntity.ok().body(response);
            } else {
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //FIND ACCOUNT IMAGES
    @GetMapping("get/account/images/account_id_fk/{sn}")
    public ResponseEntity<?> getAccountImagesByAccountId(@PathVariable Long  sn) {
        try {
            EntityResponse response = accountDocumentService.findById(sn);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("find/account/images/by/id/{id}")
    public ResponseEntity<?> getAccountImagesById(@PathVariable("id") Long id) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<Account> account = accountRepository.findById(id);
            if (account.isPresent()) {
                response = serviceCaller.getCustomerImages(account.get().getCustomerCode());
//                response.setMessage(HttpStatus.OK.getReasonPhrase());
//                response.setStatusCode(HttpStatus.OK.value());
//                List<Customerimages> customerimages = (List<Customerimages>) serviceCaller.getCustomerImages(account.get().getCustomerCode()).getEntity();
//                response.setEntity(customerimages);
                return ResponseEntity.ok().body(response);
            } else {
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //    TODO:ALL RETAIL ACCOUNTS
    @GetMapping("find/all/retail/accounts")
    public ResponseEntity<EntityResponse<List<Account>>> fetchRetailAccounts() {
        try {
            return ResponseEntity.ok().body(accountService.findAllRetailAccounts());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //    @GetMapping("td/all")
//    public ResponseEntity<EntityResponse<List<Account>>> termDeposits() {
//        try {
//            return ResponseEntity.ok().body(accountService.tdAccounts());
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
    @GetMapping("sb/all")
    public ResponseEntity<EntityResponse<List<AccountRepository.AccountsByAccountType>>> savingsAccount() {
        try {
            return ResponseEntity.ok().body(accountService.savingAccounts());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("la/all")
    public ResponseEntity<EntityResponse<List<AccountRepository.AccountsByAccountType>>> loanAccounts() {
        try {
            return ResponseEntity.ok().body(accountService.loanAccounts());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("find/account/by/customer/code/{customerCode}")
    public ResponseEntity<EntityResponse<List<Account>>> customerAccountByCustomerCode(@PathVariable("customerCode") String customerCode) {
        try {
            return ResponseEntity.ok().body(accountService.getByCustomerCode(customerCode));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }

    }

    //applies in goodways only
    @GetMapping("find/operative/account/by/customer/code/{customerCode}")
    public ResponseEntity<EntityResponse<List<Account>>> findCustomerOperativeAccount(@PathVariable("customerCode") String customerCode) {
        try {
            return ResponseEntity.ok().body(accountService.getByCustomerCodeAndProductCode(customerCode, CONSTANTS.ORDINARY_SAVINGS_ACCOUNT_PRODUCT_PERSONAL));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }

    }

    @GetMapping("find/account/by/product/code/{productCode}")
    public ResponseEntity<EntityResponse<List<Account>>> customerAccountByProductCode(@PathVariable("productCode") String productCode) {
        try {
            return ResponseEntity.ok().body(accountService.findByProductCode(productCode));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }

    }

    @GetMapping("product/loan/limits/{productCode}")
    public ResponseEntity<?> getProdLoanLimits(@PathVariable("productCode") String productCode) {
        try {
            return ResponseEntity.ok().body(itemServiceCaller.getLoanLimitsDetails(productCode));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }

    }

    @GetMapping("individual/loan/limits/")
    public ResponseEntity<?> individualLoanLimitValidation(@RequestParam String customerCode,
                                                           @RequestParam Double principalAmt,
                                                           @RequestParam String productCode) {
        try {
            return ResponseEntity.ok().body(validatorService.validateIndividualLoanLimit(customerCode,
                    principalAmt, productCode));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }

    }

    //TODO: GET ALL VERIFIED ACCOUNTS
    @GetMapping("all/verified/accounts")
    public ResponseEntity<EntityResponse<List<Account>>> findAllVerifidAccounts() {
        try {
            return ResponseEntity.ok().body(accountService.findAllVerifidAccounts());
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    //    TODO:ALL UNVERIFIED ACCOUNTS
    @GetMapping("find/all/unverified/accounts")
    public ResponseEntity<EntityResponse<List<Account>>> findAllUnverifiedAccounts() {
        try {
            return ResponseEntity.ok().body(accountService.findAllUnverifiedAccounts());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //TODO: VERIFY
    @PutMapping("/verify")
    public ResponseEntity<EntityResponse<?>> verifyAccount(@RequestParam String Acid) {
        try {
            return ResponseEntity.ok().body(accountService.verifyCreatedAccount(Acid));
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    @PutMapping("/reject/account")
    public ResponseEntity<EntityResponse<?>> rejectAccount(@RequestParam String acid) {
        System.out.println("ACIDD:  "+acid);
        try {
            return ResponseEntity.ok().body(accountService.rejectAccount(acid));
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    @PutMapping("request/acid/approval")
    public ResponseEntity<?> requestAcidApproval(@RequestParam String acid, @RequestParam String approver) throws IOException {
        try {
            return new ResponseEntity<>(accountService.requestAcidApproval(acid, approver), HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("user/accounts/approval/list")
    public ResponseEntity<?> findForApproval() throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            List<AccountLookUpInterface> accountList
                    = accountRepository.findAllByApprovalSentFlagAndApprovalSentTo(UserRequestContext.getCurrentUser());
            if (accountList.size() > 0) {
                response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", You have Been Assigned " + accountList.size() + " Account(s) For Approval.");
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(accountList);
            } else {
                response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", You have Not Been assigned any Approval Tasks. ");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/activate")
    public ResponseEntity<EntityResponse<?>> activateAccount(@RequestParam String Acid) {
        try {
            return ResponseEntity.ok().body(accountService.activateSavingAccount(Acid));
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    //TODO: DELETE
    @PutMapping("temporary/delete")
    public ResponseEntity<EntityResponse<Account>> temporaryDeleteAccount(@RequestParam Long id, @RequestParam String deletedBy) {
        try {
            Optional<Account> account = accountRepository.findById(id);
            if (account.isPresent()) {
                Account existingAccount = account.get();
                if (existingAccount.getDeleteFlag().equals(CONSTANTS.YES)) {
                    return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.ACCOUNT_ALREADY_DELETED, null, HttpStatus.BAD_REQUEST.value()));
                } else {
                    existingAccount.setDeletedBy(deletedBy);
                    existingAccount.setDeleteTime(new Date());
                    existingAccount.setDeleteFlag(CONSTANTS.YES);
                    accountRepository.save(existingAccount);
//                    return new ResponseEntity<>(HttpStatus.OK);
                    return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.RECORD_DELETED, existingAccount, HttpStatus.BAD_REQUEST.value()));
                }

            } else {
                return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.RECORDS_NOT_FOUND, null, HttpStatus.NOT_FOUND.value()));
            }

        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }

    @GetMapping("accounts/by/accounttype/solcode/accountstatus")
    public ResponseEntity<EntityResponse<List<Account>>> fetchByAccountTypeAndCustomerTypeAndSolCodeAndAccountStatus(@RequestParam String accountType, @RequestParam String customerType, @RequestParam String solCode, @RequestParam String accountStatus) {
        try {
            return ResponseEntity.ok().body(accountService.fetchByAccountTypeAndCustomerTypeAndSolCodeAndAccountStatus(accountType, customerType, solCode, accountStatus));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("withdrawable/accounts/by/accounttype/solcode/accountstatus")
    public ResponseEntity<EntityResponse<List<Account>>> fetchByAccountTypeAndCustomerTypeAndSolCodeAndAccountStatusAndIswithdrawalAllowed(@RequestParam String accountType, @RequestParam String customerType, @RequestParam String solCode, @RequestParam String accountStatus) {
        try {
            return ResponseEntity.ok().body(accountService.fetchByAccountTypeAndCustomerTypeAndSolCodeAndAccountStatusAndIsWithdawalAllowed(accountType, customerType, solCode, accountStatus));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("accounts/look-up/one")
    public ResponseEntity<EntityResponse<List<?>>> accountsLookUpOne(@RequestParam(required = false) String accountType,
                                                                           @RequestParam(required = false) String customerType,
                                                                           @RequestParam(required = false) String solCode,
                                                                           @RequestParam(required = false) String accountStatus,
                                                                           @RequestParam(required = false) String customerCode,
                                                                           @RequestParam(required = false) String nationalId,
                                                                           @RequestParam(required = false) String acid,
                                                                           @RequestParam(required = false) String accountName) {
        try {
            return ResponseEntity.ok().body(accountService.generalAccountsLookUp(accountType,
                    customerType,
                    solCode,
                    accountStatus,
                    customerCode,
                    nationalId,
                    acid,
                    accountName));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("accounts/approvallist")
    public ResponseEntity<EntityResponse<List<AccountLookUpInterface>>> getApprovalList(@RequestParam(required = true) String accountType) {
        try {
            return ResponseEntity.ok().body(accountService.getApprovalList(accountType));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("accounts/approvallist/loans")
    public ResponseEntity<EntityResponse<List<AccountLookUpInterface>>> getApprovalListLoan() {
        try {
            return ResponseEntity.ok().body(accountService.getApprovalListLoan());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("accounts/approvallist/nonloan")
    public ResponseEntity<EntityResponse<List<AccountLookUpInterface>>> getNonLoanApprovalList() {
        try {
            return ResponseEntity.ok().body(accountService.getNonLoanApprovalList());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //TODO: GENERAL ACCOUNTS LOOKUP

//    @GetMapping("accounts/general/accounts/look/up")
//    public ResponseEntity<EntityResponse<?>> generalAccountDetailsLookUp(
//            @RequestParam(required = false) String accountType,
//            @RequestParam(required = false) String customerType,
//            @RequestParam(required = false) String solCode,
//            @RequestParam(required = false) String accountStatus,
//            @RequestParam(required = false) String customerCode,
//            @RequestParam(required = false) String nationalId,
//            @RequestParam(required = false) String acid,
//            @RequestParam(required = false) String fromDate,
//            @RequestParam(required = false) String toDate) {
//        try {
//            if (accountType == null) {
//                accountType = "";
//            }
//            if (customerType == null) {
//                customerType = "";
//            }
//            if (solCode == null) {
//                solCode = "";
//            }
//            if (accountStatus == null) {
//                accountStatus = "";
//            }
//            if (customerCode == null) {
//                customerCode = "";
//            }
//            if (nationalId == null) {
//                nationalId = "";
//            }
//            if (acid == null) {
//                acid = "";
//            }
//            if (fromDate == null) {
//                fromDate = "";
//            }
//            if (toDate == null) {
//                toDate = "";
//            }
//
//            return ResponseEntity.ok().body(accountService.generalAccountDetailsLookUp(
//                    accountType,
//                    customerType,
//                    solCode,
//                    accountStatus,
//                    customerCode,
//                    nationalId,
//                    acid,
//                    fromDate,
//                    toDate));
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

    @GetMapping("accounts/general/accounts/look/up")
    public ResponseEntity<EntityResponse<?>> generalAccountDetailsLookUp1(
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) String solCode,
            @RequestParam(required = false) String accountStatus,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) String acid,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        try {
            Date frmD=null;
            Date tDate=null;

            if(accountStatus==null){
                accountStatus="";
            }
            if(customerType==null){
                customerType="";
            }
            if(solCode==null){
                solCode="";
            }

            if(fromDate !=null){
                if(fromDate.trim().isEmpty()){
                    fromDate=null;
                }
            }

            if(toDate !=null){
                if(toDate.trim().isEmpty()){
                    toDate=null;
                }
            }
            if(nationalId==null){
                nationalId="";
            }
            if(accountType==null){
                accountType="";
            }
            if(accountName==null){
                accountName="";
            }
            if(nationalId==null){
                nationalId="";
            }
            if(customerCode==null){
                customerCode="";
            }
            if(acid==null){
                acid="";
            }
            if(fromDate==null){
                frmD=datesCalculator.substractDate(new Date(),10,"YEARS");
            }else {
                LocalDate frmDate= LocalDate.parse(fromDate);
                frmD =datesCalculator.convertLocalDateToDate(frmDate);
            }
            if(toDate==null){
                tDate=new Date();
            }else {
                LocalDate tooDate= LocalDate.parse(fromDate);
                tDate =datesCalculator.convertLocalDateToDate(tooDate);
            }
            EntityResponse res= accountService.lookUpNew1(
                    nationalId,
                     accountType,
                     accountName,
                     customerCode,
                     acid,
                     customerType,
                     solCode,
                     accountStatus,
                    frmD,
                    tDate);

            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    @GetMapping("accounts/by/accounttype/solcode")
    public ResponseEntity<EntityResponse<List<Account>>> findBySolCodeAndAccountType(@RequestParam String solCode, @RequestParam String accountType) {
        try {
            return ResponseEntity.ok().body(accountService.findBySolCodeAndAccountType(solCode, accountType));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("check/if/account/exists/{acid}")
    public ResponseEntity<EntityResponse<?>> checkIfAccountExists(@PathVariable String acid) {
        try {
            return ResponseEntity.ok().body(accountService.checkIfAccountExists(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//    @GetMapping("get/accounts/details/for/lookup")
//    public ResponseEntity<EntityResponse<?>> getAccountDetailsForLookup(@RequestParam(required = false) String acid, @RequestParam(required = false) String nationalId, @RequestParam(required = false) String customerCode) {
//        try {
//            if (acid == null) {
//                acid = "";
//            }
//            if (nationalId == null) {
//                nationalId = "";
//            }
//            if (customerCode == null) {
//                customerCode = "";
//            }
//            return ResponseEntity.ok().body(accountService.getAccountForLookUp(acid, nationalId, customerCode));
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

    @GetMapping("get/accounts/details/for/lookup")
    public ResponseEntity<EntityResponse<?>> getAccountDetailsForLookup1(
            @RequestParam(required = false) String acid,
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String accountName) {
        try {
            String account_type="";
            Date fromDate=null;
            Date toDate=null;

            if(nationalId==null){
                nationalId="";
            }
            if(account_type==null){
                account_type="";
            }
            if(accountName==null){
                accountName="";
            }
            if(nationalId==null){
                nationalId="";
            }
            if(customerCode==null){
                customerCode="";
            }
            if(acid==null){
                acid="";
            }
            if(fromDate==null){
                fromDate=datesCalculator.substractDate(new Date(),10,"YEARS");
            }
            if(toDate==null){
                toDate=new Date();
            }
            EntityResponse res= accountService.lookUpNew( nationalId,
                    account_type,
                    accountName,
                    customerCode,
                    acid,
                    fromDate,
                    toDate);

            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//    @GetMapping("get/accounts/details/for/lookup/by/account/type")
//    public ResponseEntity<EntityResponse<?>> getAccountForLookUpByAccountType1(
//            @RequestParam(required = false) String acid,
//            @RequestParam(required = false) String nationalId,
//            @RequestParam(required = false) String customerCode,
//            @RequestParam String accountType,
//            @RequestParam(required = false) String fromDate,
//            @RequestParam(required = false) String toDate) {
//        try {
//            if (acid == null) {
//                acid = "";
//            }
//            if (nationalId == null) {
//                nationalId = "";
//            }
//            if (customerCode == null) {
//                customerCode = "";
//            }
//            if (fromDate == null) {
//                fromDate = "";
//            }
//            if (toDate == null) {
//                toDate = "";
//            }
//            return ResponseEntity.ok().body(accountService.getAccountForLookUpByAccountType(acid, nationalId, customerCode, accountType, fromDate, toDate));
//        } catch (Exception e) {
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }


    @GetMapping("get/accounts/details/for/lookup/by/account/type")
    public ResponseEntity<EntityResponse<?>> getAccountForLookUpByAccountType(
            @RequestParam(required = false) String acid,
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String accountType,
            @RequestParam (required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String accountName) {
        try {
            System.out.println(nationalId);
            System.out.println(accountType);
            System.out.println(accountName);
            System.out.println(customerCode);
            System.out.println(acid);
            System.out.println(fromDate);
            System.out.println(fromDate.length());
            System.out.println(fromDate == null);
            System.out.println(toDate);
            System.out.println(toDate.length());
            System.out.println(toDate == null);
            Date frmD=null;
            Date tDate=null;

            if(fromDate !=null){
                if(fromDate.trim().isEmpty()){
                    fromDate=null;
                }else {
                    if (fromDate.length() == 4)
                        fromDate = null;
                }
            }

            if(toDate !=null){
                if(toDate.trim().isEmpty()){
                    toDate=null;
                }else {
                    if (toDate.length() == 4)
                        toDate = null;
                }
            }
            if(nationalId==null){
                nationalId="";
            }
            if(accountType==null){
                accountType="";
            }
            if(accountName==null){
                accountName="";
            }
            if(nationalId==null){
                nationalId="";
            }
            if(customerCode==null){
                customerCode="";
            }
            if(acid==null){
                acid="";
            }
            if(fromDate==null){
                 frmD=datesCalculator.substractDate(new Date(),10,"YEARS");
                System.out.println("startDate is null");
                System.out.println(frmD);
            }else {
                System.out.println("startDate is not null");
                LocalDate frmDate= LocalDate.parse(fromDate);
                frmD =datesCalculator.convertLocalDateToDate(frmDate);
            }
            if(toDate==null) {
                System.out.println("todate is null");
                tDate=new Date();
            }else {
                System.out.println("endDate is not null");
                LocalDate tooDate= LocalDate.parse(toDate);
                System.out.println(tooDate);
                tDate =datesCalculator.convertLocalDateToDate(tooDate);
            }
            EntityResponse res= accountService.lookUpNew(nationalId,
                    accountType,
                    accountName,
                    customerCode,
                    acid,
                    frmD,
                    tDate);

            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("get/office/account/specific/details/{acid}")
    public ResponseEntity<EntityResponse<?>> getOfficeAccountSpecificDetails(@PathVariable String acid) {
        try {
            return ResponseEntity.ok().body(accountService.getOfficeAccountSpecificDetails(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("total/accounts/count")
    public ResponseEntity<EntityResponse<?>> accountsCount() {
        try {
            return ResponseEntity.ok().body(accountService.accountsCount());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("accounts/permanent/delete")
    public ResponseEntity<EntityResponse<?>> delete(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(accountService.permanetDelete(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("accounts/type/permanent/delete")
    public ResponseEntity<EntityResponse<?>> deleteByAcType(@RequestParam String acType) {
        try {
            return ResponseEntity.ok().body(accountService.permanetDeleteAcType(acType));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("accounts/LAA/permanent/delete/migration")
    public ResponseEntity<?> del() {
        try {
            return ResponseEntity.ok().body(accountService.deleteAllLoanAccountsForMigration());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("accounts/LAA/permanent/delete/migration/fr")
    public ResponseEntity<?> del2() {
        try {
            return ResponseEntity.ok().body(accountService.deleteAllLoanAccountsForMigrationFr());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @GetMapping("accounts/documents/by/acid/{acid}")
    public ResponseEntity<EntityResponse<List<AccountDocument>>> getAccountDocumentsByAcid(@PathVariable String acid) {
        try {
            return ResponseEntity.ok().body(accountDocumentService.getAccountDocumentsByAcid(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @GetMapping("accounts/by/phone")
    public ResponseEntity<EntityResponse<Account>> findAccountByPhone(@RequestParam String accountType, @RequestParam String phone) {
        try {
            return ResponseEntity.ok().body(accountService.findAccountByPhone(accountType, phone));
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }




    @GetMapping("all/unverified/accounts/list")
    public ResponseEntity<?> findAllUnVerifiedAccounts(
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) throws IOException {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                if (accountType == null || accountType.trim().isEmpty()) {
                    accountType = "LAA";
                }
                if (fromDate == null || toDate == null) {
                    response.setMessage("You must provide a date range !");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    List<AccountLookUpInterface> accountList = accountRepository.findAllByEntityIdAndAccountTypeAndDateRange(EntityRequestContext.getCurrentEntityId(), accountType, fromDate, toDate);
                    if (accountList.size() > 0) {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + accountList.size() + " Account(s) For Approval.");
                        response.setStatusCode(HttpStatus.FOUND.value());
                        response.setEntity(accountList);
                    } else {
                        response.setMessage("Welcome " + UserRequestContext.getCurrentUser() + ", There are " + accountList.size() + " Account(s) For Approval.");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity(accountList);
                    }
                }

            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // TODO: 5/11/2023 account lookup general new

    @GetMapping("accounts/general/look-up/new")
    public ResponseEntity<EntityResponse<?>> accountGenLookupNew(@RequestParam(required = false) String nationalId,
                                                                                     @RequestParam(required = false)  String account_type,
                                                                                     @RequestParam(required = false)  String account_name,
                                                                                     @RequestParam(required = false)  String customer_code,
                                                                                     @RequestParam(required = false)  String acid,
                                                                 @RequestParam(required = false) Date fromDate,
                                                                 @RequestParam(required = false)  Date toDate) {
        try {
            if(nationalId==null){
                nationalId="";
            }
            if(account_type==null){
                account_type="";
            }
            if(account_name==null){
                account_name="";
            }
            if(nationalId==null){
                nationalId="";
            }
            if(customer_code==null){
                customer_code="";
            }
            if(acid==null){
                acid="";
            }
            if(fromDate==null){
                fromDate=datesCalculator.substractDate(new Date(),10,"YEARS");
            }
            if(toDate==null){
                toDate=new Date();
            }
            EntityResponse res= accountService.lookUpNew( nationalId,
                     account_type,
                     account_name,
                     customer_code,
                     acid,
                    fromDate,
                    toDate);
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}