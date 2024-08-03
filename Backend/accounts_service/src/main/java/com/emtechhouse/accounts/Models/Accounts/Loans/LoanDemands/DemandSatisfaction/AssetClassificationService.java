package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction;

import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class AssetClassificationService {
    @Autowired
    private LoanDemandRepository loanDemandRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private DatesCalculator datesCalculator;

    @Autowired
    private AccountRepository accountRepository;

    public String getLoanAssetClassification(String acid) {
        String assetClassification = "PERFORMING";
        try {
// Check the number of installments for the loan
            String numberOfInstallments = loanRepository.getNumberOfInstallments(acid);
//            int totalBalance = loanRepository.getTotalBalance(acid);
            LocalDate demandDate;

            if ("1".equals(numberOfInstallments)) {

// Pick the maturity date as the demand date
           demandDate = datesCalculator.convertDateToLocalDate(loanRepository.getMaturityDate(acid));
            } else {
// Pick the demand date from the loan demand table
                Optional<LoanDemand> firstUnsatisfiedDemand = loanDemandRepository.getFirstUnsatisfiedDemand(acid);

                if (firstUnsatisfiedDemand.isPresent()) {
                    demandDate = datesCalculator.convertDateToLocalDate(firstUnsatisfiedDemand.get().getDemandDate());
                } else {
// If there are no unsatisfied demands, set demand date as today
                    demandDate = LocalDate.now();
                }
            }

// Today's date
            LocalDate now = LocalDate.now();
            log.info("demand date " + demandDate.toString());
            log.info("now: " + now.toString());
            Long daysDifference = datesCalculator.getDaysDifference(demandDate, now);
            log.info(" days difference:" + daysDifference.toString());

            if (daysDifference < 0) {
                // If daysDifference is negative, set as Performing
                assetClassification = CONSTANTS.PERFORMING;
            } else if (daysDifference == 0) {
                // Handle the case when daysDifference is exactly 0
                assetClassification = CONSTANTS.PERFORMING;
            } else if (daysDifference <= 30) {
                assetClassification = CONSTANTS.WATCH;
            } else if (daysDifference <= 180) {
                assetClassification = CONSTANTS.SUB_STANDARD;
            } else if (daysDifference <= 360) {
                assetClassification = CONSTANTS.DOUBTFUL;
            } else {
                assetClassification = CONSTANTS.LOSS;
            }

        } catch (Exception e) {
            log.info("Caught Error {} " + e);
        }
        return assetClassification;
    }
    public EntityResponse classifyLoan(String acid) {
        try {
            String assetClassification = getLoanAssetClassification(acid);
            Loan loan = accountRepository.findByAcid(acid).get().getLoan();
            System.out.println("Classification: " + assetClassification);
            loanRepository.updateLoanAssetClassification(assetClassification, loan.getSn());

            EntityResponse entityResponse = new EntityResponse<>();
            entityResponse.setMessage(assetClassification);
            entityResponse.setStatusCode(HttpStatus.OK.value());

            return entityResponse;
        } catch (Exception e) {
            log.info("Caught Error {} " + e);
            return null;
        }
    }
}
