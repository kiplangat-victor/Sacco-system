package com.emtechhouse.accounts.Resources;


import com.emtechhouse.accounts.Responses.BatchSalaryCheques;
import com.emtechhouse.accounts.TransactionService.BatchTransaction.BatchtransactionRepository;
import com.emtechhouse.accounts.TransactionService.ChequeProcessing.ChequeProcessingRepo;
import com.emtechhouse.accounts.TransactionService.SalaryUploads.SalaryuploadRepo;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("accounts")
@RestController
public class CommonResource {
    @Autowired
    ChequeProcessingRepo chequeProcessingRepo;
    @Autowired
    BatchtransactionRepository batchtransactionRepository;
    @Autowired
    SalaryuploadRepo salaryuploadRepo;

    @GetMapping("batchsalarycheque")
    public ResponseEntity<?> fetchAll() {
        try {
            EntityResponse response = new EntityResponse<>();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                log.info("User Name not present in the Request Header");
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                log.info("Entity not present in the Request Header");
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else {

                log.info("fetching salary upload details...");
                List<BatchSalaryCheques> allRough = new ArrayList<>();
                // and (verified_flag = 'N' OR (verified_flag = 'Y' and verified_by <> :username ))
                List<BatchSalaryCheques> allRoughCheque = chequeProcessingRepo.getRoughApprovalList(UserRequestContext.getCurrentUser());
                List<BatchSalaryCheques> allRoughBatch = batchtransactionRepository.getRoughApprovalList(UserRequestContext.getCurrentUser());
                List<BatchSalaryCheques> allRoughSalary = salaryuploadRepo.getRoughApprovalList(UserRequestContext.getCurrentUser());
                allRough.addAll(allRoughBatch);
                allRough.addAll(allRoughSalary);
                allRough.addAll(allRoughCheque);
                if (allRough.size() > 0) {
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(allRough);
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } else if (allRough.isEmpty()) {
                    response.setEntity(allRough);
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NO_CONTENT.value());
                }

            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
