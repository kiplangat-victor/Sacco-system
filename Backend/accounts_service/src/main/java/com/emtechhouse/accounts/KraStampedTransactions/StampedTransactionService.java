package com.emtechhouse.accounts.KraStampedTransactions;

import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class StampedTransactionService {
    private final StampedTransactionRepo stampedTransactionRepo;

    public StampedTransactionService(StampedTransactionRepo stampedTransactionRepo) {
        this.stampedTransactionRepo = stampedTransactionRepo;
    }

    public EntityResponse add(StampedTransaction stampedTransaction) {
        try {
            EntityResponse response = new EntityResponse();
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(stampedTransactionRepo.save(stampedTransaction));
            return response;

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse findAll() {
        try {
            EntityResponse response = new EntityResponse();
            if (stampedTransactionRepo.findAll().size()==0 || stampedTransactionRepo.findAll().isEmpty()){
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }else{
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(stampedTransactionRepo.findAll());
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
            if (stampedTransactionRepo.findById(id).isPresent()){
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(stampedTransactionRepo.findById(id));
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

    public EntityResponse update(StampedTransaction stampedTransaction) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<StampedTransaction> st = stampedTransactionRepo.findById(stampedTransaction.getId());
            if (st.isPresent()){
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                stampedTransaction.setPostedBy(st.get().getPostedBy());
                stampedTransaction.setPostedTime(st.get().getPostedTime());
                response.setEntity(stampedTransactionRepo.save(stampedTransaction));
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

    public EntityResponse<?> delete(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<StampedTransaction> st = stampedTransactionRepo.findById(id);
            if (st.isPresent()){
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                StampedTransaction stampedTransaction = st.get();
                response.setEntity(stampedTransactionRepo.save(stampedTransaction));
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
            Optional<StampedTransaction> st = stampedTransactionRepo.findById(id);
            if (st.isPresent()) {
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                StampedTransaction stampedTransaction = st.get();
                response.setEntity(stampedTransactionRepo.save(stampedTransaction));
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
}
