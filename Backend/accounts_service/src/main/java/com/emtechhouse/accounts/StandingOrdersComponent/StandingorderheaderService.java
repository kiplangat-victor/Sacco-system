package com.emtechhouse.accounts.StandingOrdersComponent;

import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StandingorderheaderService {
    private final StandingorderheaderRepo standingorderheaderRepo;

    public StandingorderheaderService(StandingorderheaderRepo standingorderheaderRepo) {
        this.standingorderheaderRepo = standingorderheaderRepo;
    }


    public EntityResponse add(Standingorderheader standingorderheader) {
        try {
            EntityResponse response = new EntityResponse();
            if (standingorderheaderRepo.existsByStandingOrderCode(standingorderheader.getStandingOrderCode())) {
                response.setMessage("The code provided already exists!");
            } else {
                response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(standingorderheaderRepo.save(standingorderheader));
            }
            return response;

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse findAll() {
        try {
            EntityResponse response = new EntityResponse();
            if (standingorderheaderRepo.findAll().size()==0 || standingorderheaderRepo.findAll().isEmpty()){
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }else{
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(standingorderheaderRepo.findAll());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse findById(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            if (standingorderheaderRepo.findById(id).isPresent()){
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(standingorderheaderRepo.findById(id));
            }else{
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse update(Standingorderheader standingorderheader) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<Standingorderheader> st = standingorderheaderRepo.findById(standingorderheader.getId());
            if (st.isPresent()) {
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                standingorderheader.setPostedBy(st.get().getPostedBy());
                standingorderheader.setPostedTime(st.get().getPostedTime());
                standingorderheader.setModifiedBy(UserRequestContext.getCurrentUser());
                standingorderheader.setModifiedTime(new Date());
                standingorderheader.setVerifiedFlag('N');
                standingorderheader.setVerifiedBy(st.get().getVerifiedBy());
                response.setEntity(standingorderheaderRepo.save(standingorderheader));
            } else {
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> delete(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<Standingorderheader> st = standingorderheaderRepo.findById(id);
            if (st.isPresent()){
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());

                Standingorderheader standingorderheader = st.get();
                standingorderheader.setDeletedFlag('Y');
                standingorderheader.setDeletedTime(new Date());
                standingorderheader.setDeletedBy(UserRequestContext.getCurrentUser());
                response.setEntity(standingorderheaderRepo.save(standingorderheader));
            }else{
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
        return null;
    }

    public EntityResponse<?> verify(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<Standingorderheader> st = standingorderheaderRepo.findById(id);
            if (st.isPresent()) {
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                Standingorderheader standingorderheader = st.get();
                standingorderheader.setVerifiedFlag('Y');
                standingorderheader.setVerifiedTime(new Date());
                standingorderheader.setVerifiedBy(UserRequestContext.getCurrentUser());
                response.setEntity(standingorderheaderRepo.save(standingorderheader));
            }else{
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
        return null;
    }
    public EntityResponse findAllForApproval() {
        try {
            EntityResponse response = new EntityResponse();
            List<Standingorderheader> standingorderheaderList = standingorderheaderRepo.findAllByPostedFlagAndDeletedFlagAndVerifiedFlag('Y','N', 'N');
            if (standingorderheaderList.size()>0){
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(standingorderheaderList);
            }else{
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
