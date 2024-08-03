package com.emtechhouse.accounts.Models.Accounts.Loans.InterestReversal;


import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandGenerationService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.DemandSatisfactionService;
import com.emtechhouse.accounts.Models.Accounts.Loans.InterestReversal.IntReversalItem.IntReversalItem;
import com.emtechhouse.accounts.Models.Accounts.Loans.InterestReversal.IntReversalItem.IntReversalItemRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanService;
import com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions.SavingContributionInstructionsRepo;
import com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions.SavingContributionInstructionsService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.*;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DataNotFoundException;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InterestReversalService {
    private final IntReversalItemRepo intReversalItemRepo;
    private final InterestReversalRepo interestReversalRepo;
    private final TranHeaderService tranHeaderService;
    private final TranHeaderRepository tranHeaderRepository;
    private final NewTransactionService newTransactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoanService loanService;

    @Autowired
    private TransactionsController transactionsController;

    @Autowired
    private DemandGenerationService demandGenerationService;
    //    @Autowired
//    private DemandGenerationService demandGenerationService;
    @Autowired
    private SavingContributionInstructionsService savingContributionInstructionsService;

    @Autowired
    private SavingContributionInstructionsRepo savingContributionInstructionsRepo;
    @Autowired
    private DemandSatisfactionService demandSatisfactionService;
    @Autowired
    private TranHeaderRepository tranrepo;

    public InterestReversalService(InterestReversalRepo interestReversalRepo, TranHeaderService tranHeaderService, TranHeaderRepository tranHeaderRepository, NewTransactionService newTransactionService,
                                   IntReversalItemRepo intReversalItemRepo) {
        this.interestReversalRepo = interestReversalRepo;
        this.tranHeaderService = tranHeaderService;
        this.tranHeaderRepository = tranHeaderRepository;
        this.newTransactionService = newTransactionService;
        this.intReversalItemRepo = intReversalItemRepo;
    }

    public InterestReversal addInterestReversalupload(InterestReversal interestReversal) {
        try {
            interestReversal.setInterestReversalCode("M" + tranHeaderService.generateRandomCode(5).toUpperCase());
            interestReversal.setEnteredBy(UserRequestContext.getCurrentUser());
            interestReversal.setEnteredFlag(CONSTANTS.YES);
            interestReversal.setEnteredTime(new Date());
            return interestReversalRepo.save(interestReversal);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<InterestReversal> findAllInterestReversaluploads() {
        try {
            return interestReversalRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public InterestReversal findById(Long id) {
        try {
            return interestReversalRepo.findById(id).orElseThrow(() -> new DataNotFoundException("Data " + id + "was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public InterestReversal updateInterestReversalupload(InterestReversal interestReversal) {
        try {
            return interestReversalRepo.save(interestReversal);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteInterestReversalupload(Long id) {
        try {
            interestReversalRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public Integer checkAccountExistance(String acid) {
        Integer isExist = tranHeaderRepository.checkAccountExistance(acid);
        return isExist;
    }

    public List<InterestReversal> filterBydate(String fromDate, String toDate, String entityId) {
        List<InterestReversal> interestReversalList = new ArrayList<>();
            interestReversalList = interestReversalRepo.loadAll(fromDate, toDate, entityId);
        return interestReversalList;
    }

    public String validateInterestReversal(InterestReversal interestReversal) {
        String errors = "";


        Double totalAmount = 0.0;

        for (int i = 0; i < interestReversal.getIntReversalItems().size(); i++) {
            System.out.println("Loop through recei");
            IntReversalItem intReversalItem = interestReversal.getIntReversalItems().get(i);

//            System.out.println(employeedetails);
            Optional<Account> accountOptional1 = accountRepository.findByAcid(intReversalItem.getLoanAccount());
            if (!accountOptional1.isPresent()) {
                errors += " " + "Account: " + intReversalItem.getLoanAccount() + " does not exist.";
                continue;
            } else {
//                System.out.println("Account: " + intReversalItem.getLoanAccount() + " exists. ");
            }

            Account account = accountOptional1.get();
            if (account.getAccountStatus().equalsIgnoreCase("CLOSED")) {
                errors += " " + "Account: " + intReversalItem.getLoanAccount() + " status is " + account.getAccountStatus() + ".";
            }

            if (!account.getAccountType().equalsIgnoreCase("LAA")) {
                errors += " " + intReversalItem.getLoanAccount() + " is not a loan account. The account is " + account.getAccountType() + ".";
            }

            intReversalItem.setAccountName(account.getAccountName());
            intReversalItem.setMemberNumber(account.getCustomerCode());
        }


        return errors;
    }

    // TODO: 4/6/2023
    public EntityResponse verify(String interestReversalUploadCode) {
        try {
            EntityResponse res = new EntityResponse<>();
            Optional<InterestReversal> interestReversalUpload = interestReversalRepo.findByEntityIdAndInterestReversalUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), interestReversalUploadCode, 'N');
            if (interestReversalUpload.isPresent()) {
                InterestReversal pInterestReversalUpload = interestReversalUpload.get();
                if (pInterestReversalUpload.getVerifiedFlag().equals('N')) {
                    pInterestReversalUpload.setVerifiedBy(UserRequestContext.getCurrentUser());
                    pInterestReversalUpload.setVerifiedTime(new Date());
                    pInterestReversalUpload.setVerifiedFlag('Y');
                    pInterestReversalUpload = interestReversalRepo.save(pInterestReversalUpload);
                    res.setStatusCode(HttpStatus.OK.value());
                    res.setMessage("Verified successfully");
                    res.setEntity(pInterestReversalUpload);
                } else if (pInterestReversalUpload.getVerifiedFlag_2().equals('N')) {
                    pInterestReversalUpload.setVerifiedBy_2(UserRequestContext.getCurrentUser());
                    pInterestReversalUpload.setVerifiedTime_2(new Date());
                    pInterestReversalUpload.setVerifiedFlag_2('Y');
                    pInterestReversalUpload = interestReversalRepo.save(pInterestReversalUpload);
                    res.setStatusCode(HttpStatus.OK.value());
                    res.setMessage("Verified successfully");
                    res.setEntity(pInterestReversalUpload);
                } else {
                    res.setMessage("InterestReversal already verified");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }

            } else {
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                res.setMessage("InterestReversal upload not found");
            }
            return res;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public PartTran createPartran(String crAcid, Double amount, String transactipnParticulars) {
        PartTran partTranCr = new PartTran();
        partTranCr.setAcid(crAcid);
        partTranCr.setIsoFlag('Y');
        partTranCr.setExchangeRate("1");
        partTranCr.setParttranIdentity(CONSTANTS.Normal);
        partTranCr.setPartTranType(CONSTANTS.Credit);
        partTranCr.setTransactionAmount(amount);
        partTranCr.setTransactionDate(new Date());
        partTranCr.setTransactionParticulars(transactipnParticulars);

        return partTranCr;
    }


    public EntityResponse post(String interestReversalUploadCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();

            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<InterestReversal> interestReversalUpload = interestReversalRepo
                        .findByEntityIdAndInterestReversalUploadCodeAndDeletedFlag(entityId, interestReversalUploadCode, 'N');

                if (interestReversalUpload.isPresent()) {
                    for (IntReversalItem intReversalItem : interestReversalUpload.get().getIntReversalItems()) {
                        EntityResponse entityResponse = loanService.massiveDemandReversals(
                                intReversalItem.getLoanAccount(), intReversalItem.getReversalStartDate(), intReversalItem.getReversalStartDate(),false);

                        // Log the result of each interest reversal operation
                        log.info("Loan {} reversal result: {}", intReversalItem.getLoanAccount(), entityResponse);

                        // Optionally, you can handle errors more granularly here
                        if (!entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                            // Handle error (e.g., log, notify, etc.)
                            log.error("Failed to reverse interest for loan {}: {}", intReversalItem.getLoanAccount(), entityResponse.getMessage());
                        }
                    }
                }
            }

            // Consider returning a more informative response based on the result of interest reversal operations
            response.setMessage("Interest reversal process completed");
            response.setStatusCode(HttpStatus.OK.value());
            return response;

        } catch (Exception e) {
            // Log the exception
            log.error("Error in processing interest reversal: {}", e.getMessage(), e);

            // Return an informative response about the failure
            EntityResponse res = new EntityResponse<>();
            res.setMessage("Failed to process interest reversal");
            res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return res;
        }
    }


    public EntityResponse<?> interestReversalUploadsCount() {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                Integer totalInterestReversalUploads = interestReversalRepo.countAllInterestReversalUploads();
                if (totalInterestReversalUploads > 0) {
                    response.setMessage("Total Unverified Transactions is : " + totalInterestReversalUploads);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(totalInterestReversalUploads);
                } else {
                    response.setMessage("Total Unverified Transactions is : " + totalInterestReversalUploads);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(totalInterestReversalUploads);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse rejectInterestReversalUpload(String interestReversalUploadCode) {
        try {
            EntityResponse response = new EntityResponse<>();
            Optional<InterestReversal> checkInterestReversalUploadCode = interestReversalRepo.findByEntityIdAndInterestReversalUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), interestReversalUploadCode, 'N');
            if (checkInterestReversalUploadCode.isPresent()) {
                InterestReversal interestReversal = checkInterestReversalUploadCode.get();
                if (interestReversal.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                    response.setMessage("Hello " + UserRequestContext.getCurrentUser() + ", You Can NOT REJECT Transaction That You Created!!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (interestReversal.getRejectedFlag().equals('Y')) {
                    response.setMessage("Transaction InterestReversal Upload With Code :" + checkInterestReversalUploadCode.get().getInterestReversalCode() + " ALREADY REJECTED");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (interestReversal.getVerifiedFlag_2().equals('Y')) {
                    response.setMessage("Transaction InterestReversal Upload ALREADY VERIFIED");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    interestReversal.setRejectedBy(UserRequestContext.getCurrentUser());
                    interestReversal.setRejectedTime(new Date());
                    interestReversal.setRejectedFlag('Y');
                    InterestReversal rejectInterestReversalUpLoad = interestReversalRepo.save(interestReversal);
                    response.setMessage("Transaction InterestReversal Upload With Code :" + rejectInterestReversalUpLoad.getInterestReversalCode() + " REJECTED Successfully At " + rejectInterestReversalUpLoad.getRejectedTime());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(rejectInterestReversalUpLoad);
                }
            } else {
                response.setMessage("No InterestReversal Upload Found:");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}