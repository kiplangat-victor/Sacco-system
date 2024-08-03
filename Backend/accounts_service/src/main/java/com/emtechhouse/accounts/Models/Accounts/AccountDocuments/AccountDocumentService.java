package com.emtechhouse.accounts.Models.Accounts.AccountDocuments;

import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class AccountDocumentService {
    @Autowired
    private AccountDocumentRepository accountDocumentRepository;
    @Autowired
    private ValidatorService validatorsService;

    public EntityResponse<List<AccountDocument>>getAccountDocumentsByAcid(String acid){
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<AccountDocument> accountDocumentList =accountDocumentRepository.findAccountDocumentByAcid(acid);
                EntityResponse listChecker = validatorsService.listLengthChecker(accountDocumentList);
                return listChecker;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?>findById(Long sn){
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                Optional<AccountDocument> accountDocument =accountDocumentRepository.findById(sn);
                if(accountDocument.isPresent()){
                    EntityResponse res= new EntityResponse<>();
                    res.setMessage("FOUND");
                    res.setEntity(accountDocument.get());
                    res.setStatusCode(HttpStatus.FOUND.value());

                    return res;
                }else {
                    EntityResponse res= new EntityResponse<>();
                    res.setMessage("NOT_FOUND");
                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return res;
                }
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
