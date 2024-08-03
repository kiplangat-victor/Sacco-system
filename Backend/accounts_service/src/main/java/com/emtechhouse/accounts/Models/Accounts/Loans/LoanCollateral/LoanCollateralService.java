package com.emtechhouse.accounts.Models.Accounts.Loans.LoanCollateral;

import com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor.LoanGuarantor;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.CollateralDto.CollateralDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoanCollateralService {
    @Autowired
    private LoanCollateralRepo loanCollateralRepo;
    @Autowired
    private ItemServiceCaller itemServiceCaller;

    public void deleteLoanCollaterals(List<LoanCollateral> loanCollaterals){
        try{
            loanCollaterals.forEach(l -> {
                log.info("deleting collaterals");
                l.setLoan(null);
                loanCollateralRepo.deleteById(l.getId());
            });
        }catch (Exception e){
            log.error("Catched exception {} "+e);
        }
    }

    // TODO: 3/20/2023 validate all loan collaterals 
    public EntityResponse validateCollateral(List<LoanCollateral> loanCollaterals){
        try{
            EntityResponse res = new EntityResponse<>();
            List<LoanCollateral> loanCollateralList=new ArrayList<>();
            if(loanCollaterals.size()>0){
                if(!checkDuplicates(loanCollaterals)){
                    for(Integer i=0;i<loanCollaterals.size();i++){
                        res=validateCollateral(loanCollaterals.get(i));
                        if(res.getStatusCode() != HttpStatus.OK.value()){
                            break;
                        }else {
                            loanCollateralList.add((LoanCollateral) res.getEntity());
                            continue;
                        }
                    }
                    res.setEntity(loanCollateralList);
                    return res;
                }else {
                    EntityResponse response= new EntityResponse<>();
                    response.setMessage("Seems there are duplicate collaterals");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return response;
                }
            }else {
                EntityResponse response= new EntityResponse<>();
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                return response;
            }
        }catch (Exception e){
            log.error("Catched exception {} "+e);
            return null;
        }
    }

    // TODO: 3/20/2023 collateral validation -- loop through all collaterals anf verify if they are existing

    public EntityResponse validateCollateral(LoanCollateral loanCollateral){
        try{
            EntityResponse res = new EntityResponse<>();

            String collateralCode =loanCollateral.getCollateralSerial();
            res= itemServiceCaller.getCollateralDetails(collateralCode);
            if(res.getStatusCode()== HttpStatus.OK.value()){
                //proceed
                CollateralDto c = (CollateralDto) res.getEntity();
                Double value = c.getCollateralValue();
                String desc = c.getDescription();
                String type =c.getCollateralType();

                loanCollateral.setCollateralName(desc);
                loanCollateral.setCollateralType(type);
                loanCollateral.setCollateralValue(value);

                res.setStatusCode(HttpStatus.OK.value());
                res.setEntity(loanCollateral);
                res.setMessage(HttpStatus.OK.getReasonPhrase());
            }
            return res;
        }catch (Exception e){
            log.error("Catched exception {} "+e);
            return null;
        }
    }

    public EntityResponse validateCollateral(String collateralCode){
        try{
            EntityResponse res = new EntityResponse<>();

            res= itemServiceCaller.getCollateralDetails(collateralCode);
            if(res.getStatusCode()== HttpStatus.OK.value()){
                //proceed
                CollateralDto c = (CollateralDto) res.getEntity();
                Double value = c.getCollateralValue();
                String desc = c.getDescription();
                String type =c.getCollateralType();

                if(c.getVerifiedFlag().equals(CONSTANTS.YES)){
                    LoanCollateral loanCollateral= new LoanCollateral();
                    loanCollateral.setCollateralName(desc);
                    loanCollateral.setCollateralType(type);
                    loanCollateral.setCollateralValue(value);
                    loanCollateral.setCollateralSerial(collateralCode);

                    res.setStatusCode(HttpStatus.OK.value());
                    res.setEntity(loanCollateral);
                    res.setMessage(HttpStatus.OK.getReasonPhrase());
                }else {
                    res.setStatusCode(HttpStatus.OK.value());
                    res.setEntity(null);
                    res.setMessage("Seems like collateral is not verified");
                }

            }
            return res;
        }catch (Exception e){
            log.error("Catched exception {} "+e);
            return null;
        }
    }

    // TODO: 3/21/2023 send collateral verification code
    public EntityResponse sendCollateralVerificationCode(String collateralCode){
        try {
            EntityResponse response = new EntityResponse<>();
            response=validateCollateral(collateralCode);

            if(response.getStatusCode() == HttpStatus.OK.value()){
                CollateralDto c = (CollateralDto) itemServiceCaller.getCollateralDetails(collateralCode).getEntity();
                if(c.getEmailAddress() != null){
                    response=itemServiceCaller.sendCollateralOtp(collateralCode, c.getEmailAddress());
                    return response;
                }else {
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setMessage("Could not find attached email");
                    response.setEntity(null);
                    return response;
                }

            }else {
                return response;
            }
        }catch (Exception e){
            log.error("Catched exception {} "+e);
            return null;
        }
    }

    // TODO: 3/21/2023 verify collateral verification otp
    public EntityResponse collateralOtpVerification(String collateralCode,Integer otp){
        try {
            return itemServiceCaller.verifyCollateralOtp(collateralCode, otp);
        }catch (Exception e){
            log.error("Catched exception {} "+e);
            return null;
        }
    }

    // TODO: 3/20/2023 Check whether there are duplicates collaterals
    public Boolean checkDuplicates(List<LoanCollateral> l){
        List<String> ls = l.stream().map(LoanCollateral :: getCollateralSerial).collect(Collectors.toList());
        Set<String> set1 = new HashSet<String>(ls);
        if(set1.size()<ls.size()){
            return true;
        }else {
            return false;
        }
    }



    // TODO: 3/21/2023 send collateral verification code


}
