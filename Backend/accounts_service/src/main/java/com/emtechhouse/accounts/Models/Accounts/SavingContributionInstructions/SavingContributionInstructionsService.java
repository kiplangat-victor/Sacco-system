package com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SavingContributionInstructionsService {
    @Autowired
    private TransactionsController transactionsController;
    @Autowired
    private SavingContributionInstructionsRepo savingContributionInstructionsRepo;


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DatesCalculator datesCalculator;


    public void executeSavingInstructions() {
        List<SavingContributionInstructions> savingContributionInstructionsList = savingContributionInstructionsRepo.findAllDue();
        System.out.println(savingContributionInstructionsList.size());
        for (SavingContributionInstructions instructions : savingContributionInstructionsList) {
            executeOne(instructions);
        }
    }

    public EntityResponse executeOne(SavingContributionInstructions instructions, Account operativeAccount) {
        Optional<Account> shareAccount = accountRepository.getAccountByCustomerCodeAndProduct(instructions.customerCode, "S01");
        TransactionHeader transactionHeader = null;
        if (shareAccount.isPresent()) {
            if (shareAccount.get().getAccountBalance() < 5000) {
                transactionHeader = createTransactionHeader("KES", instructions.getDescription(),
                        Math.abs(instructions.amount), operativeAccount.getAcid(), shareAccount.get().getAcid());
            }
        }

        if (transactionHeader == null) {
            Optional<Account> depositsAccount = accountRepository.getAccountByCustomerCodeAndProduct(instructions.customerCode, "S02");
            if (depositsAccount.isPresent()) {
                transactionHeader = createTransactionHeader("KES", instructions.getDescription(),
                        Math.abs(instructions.amount), operativeAccount.getAcid(), depositsAccount.get().getAcid());

            }
        }

        System.out.println(transactionHeader);
        if (transactionHeader != null) {
            EntityResponse transactionRes = transactionsController.systemTransaction1(transactionHeader).getBody();
            if (transactionRes.getStatusCode().equals(HttpStatus.OK.value())) {
                Date date = instructions.getNextCollectionDate();
                instructions.setNextCollectionDate(datesCalculator.addDate(date, 1, "MONTHS"));
                savingContributionInstructionsRepo.save(instructions);
            }
            return transactionRes;
        } else {
            System.out.println("Saving transaction is null");
            EntityResponse response = new EntityResponse();
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setMessage("Saving transaction is null");
            return response;
        }
    }

    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc) {
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
        transactionHeader.setCurrency(loanCurrency);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
        transactionHeader.setTotalAmount(totalAmount);

        PartTran drPartTran = new PartTran();
        drPartTran.setPartTranType(CONSTANTS.DEBITSTRING);
        drPartTran.setTransactionAmount(totalAmount);
        drPartTran.setAcid(drAc);
        drPartTran.setCurrency(loanCurrency);
        drPartTran.setExchangeRate("");
        drPartTran.setParttranIdentity("NORMAL");
        drPartTran.setTransactionDate(new Date());
        drPartTran.setTransactionParticulars(transDesc);
        drPartTran.setIsoFlag(CONSTANTS.YES);

        PartTran crPartTran = new PartTran();
        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
        crPartTran.setTransactionAmount(totalAmount);
        crPartTran.setAcid(crAc);
        crPartTran.setCurrency(loanCurrency);
        crPartTran.setExchangeRate("");
        crPartTran.setParttranIdentity("NORMAL");
        crPartTran.setTransactionDate(new Date());
        crPartTran.setTransactionParticulars(transDesc);
        crPartTran.setIsoFlag(CONSTANTS.YES);

        List<PartTran> partTranList = new ArrayList<>();
        partTranList.add(drPartTran);
        partTranList.add(crPartTran);

        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }

    public void executeOne(SavingContributionInstructions instructions, String account) {
        Optional<Account> account1 = accountRepository.findByAcid(account);
        account1.ifPresent(value -> executeOne(instructions, value));
    }

    public EntityResponse executeOne(SavingContributionInstructions instructions) {
        EntityResponse response = new EntityResponse();
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        Optional<Account> operativeAccount = accountRepository.getOperativeAccount(instructions.customerCode);
        if (!operativeAccount.isPresent()) {
            response.setMessage("Could not find operative account for " + instructions.customerCode);
            System.out.println("Could not find operative account for " + instructions.customerCode);
            return response;
        }
        if (accountRepository.getAvailableBalance(operativeAccount.get().getAcid()) < Math.abs(instructions.amount)) {
            System.out.println("Operative account " + operativeAccount.get().getAcid() + " for " + instructions.customerCode + " does not have enough money");
            response.setMessage("Operative account " + operativeAccount.get().getAcid() + " for " + instructions.customerCode + " does not have enough money");
            return response;
        }
        return executeOne(instructions, operativeAccount.get());
    }

    public void executeOne(String customerCode) {
        Optional<SavingContributionInstructions> instructions = savingContributionInstructionsRepo.findDueByCustomerCode(customerCode, 15);
        instructions.ifPresent(this::executeOne);
    }

    public EntityResponse<?> totalSavingContributionInstructions() {
        try {
            EntityResponse response = new EntityResponse();
            String currentUser = UserRequestContext.getCurrentUser();
            String currentEntityId = EntityRequestContext.getCurrentEntityId();
            if (currentUser.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (currentEntityId.isEmpty()) {
                response.setMessage("User Entity ID not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                Integer totalInstructions = savingContributionInstructionsRepo.countAllSavingContributionInstructions();
                if (totalInstructions > 0) {
                    response.setMessage("Total UnVerified Saving Instructions are " + totalInstructions);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(totalInstructions);
                } else {
                    response.setMessage("Total Unverified Saving Instructions are " + totalInstructions);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> rejectSavingContributionInstructions(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            String currentUser = UserRequestContext.getCurrentUser();
            String currentEntityId = EntityRequestContext.getCurrentEntityId();
            if (currentUser.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (currentEntityId.isEmpty()) {
                response.setMessage("User Entity ID not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                Optional<SavingContributionInstructions> searchId = savingContributionInstructionsRepo.findById(id);
                if (searchId.isPresent()) {
                    SavingContributionInstructions savingContributionInstructions = searchId.get();
                    if (searchId.get().getPostedBy().equalsIgnoreCase(currentUser)) {
                        response.setMessage("Hello " + currentUser + " You can NOT REJECT this Process since you INITIATED!!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else if (savingContributionInstructions.getVerifiedFlag().equals('Y')) {
                        response.setMessage("Savings Contributions with code " + savingContributionInstructions.getSavingCode() + " ALREADY VERIFIED!!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else if (savingContributionInstructions.getRejectedFlag().equals('Y')) {
                        response.setMessage("Savings Contribution Instruction with Code " + savingContributionInstructions.getSavingCode() + " Already REJECTED At " + savingContributionInstructions.getRejectedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else {
                        savingContributionInstructions.setRejectedFlag('Y');
                        savingContributionInstructions.setRejectedTime(new Date());
                        savingContributionInstructions.setRejectedBy(UserRequestContext.getCurrentUser());
                        SavingContributionInstructions rejectInstructions = savingContributionInstructionsRepo.save(savingContributionInstructions);
                        response.setMessage("Savings Contribution Instruction with Code " + rejectInstructions.getSavingCode() + " REJECTED SUCCESSFULLY at " + rejectInstructions.getRejectedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(rejectInstructions);
                    }
                } else {
                    response.setMessage("Savings Contribution Instruction Not REGISTERED WITH OUR DATABASES!!" );
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}