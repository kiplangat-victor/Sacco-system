package com.emtechhouse.accounts.TransactionService.ChequeProcessing;

import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.NewTransactionService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionProcessing;
import com.emtechhouse.accounts.Utils.AuditDetails;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ChequeProcessingService {
    private final ChequeProcessingRepo chequeProcessingRepo;
    private final AuditDetails auditDetails;
    private final TransactionProcessing transactionProcessing;
    private final NewTransactionService newTransactionService;

    public ChequeProcessingService(ChequeProcessingRepo chequeProcessingRepo, AuditDetails auditDetails, TransactionProcessing transactionProcessing, NewTransactionService newTransactionService) {
        this.chequeProcessingRepo = chequeProcessingRepo;
        this.auditDetails = auditDetails;
        this.transactionProcessing = transactionProcessing;
        this.newTransactionService = newTransactionService;
    }

    public static String generateChequeCode(int len) {
        String chars = "0123456789";
        Random rnd = new Random();
        String M = "C";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        return M + sb;
    }

    public EntityResponse enter(ChequeProcessing chequeProcessing) {
        try {
            EntityResponse response = new EntityResponse();
            if (chequeProcessing.getChequeNumber() == null || chequeProcessing.getChequeNumber().trim().isEmpty()) {
                response.setMessage("Cheque No is required!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (chequeProcessing.getBankName() == null || chequeProcessing.getBankName().trim().isEmpty()) {
                response.setMessage("Bank Name is required!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (chequeProcessing.getDebitOABAccount() == null || chequeProcessing.getDebitOABAccount().trim().isEmpty()) {
                response.setMessage("Cheque Clearance Office Account  is required!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (chequeProcessing.getMaturityDate() == null) {
                response.setMessage("Cheque Maturity Date is required!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (chequeProcessing.getAmount() == null || chequeProcessing.getAmount() < 1) {
                response.setMessage("Cheque Amount is required!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (chequeProcessing.getNameAsPerCheque() == null || chequeProcessing.getNameAsPerCheque().trim().isEmpty()) {
                response.setMessage("Name as per cheque No is required!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (chequeProcessing.getCreditCustOperativeAccount() == null || chequeProcessing.getCreditCustOperativeAccount().trim().isEmpty()) {
                response.setMessage("Cheque Credit Member Operative Account is required!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                response = transactionProcessing.accountValidator(chequeProcessing.getCreditCustOperativeAccount(), "Cheque Processing");
                if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                    if (auditDetails.checkUserAndEntity().getStatusCode() == HttpStatus.OK.value()) {
                        String user = UserRequestContext.getCurrentUser();
                        String entityId = EntityRequestContext.getCurrentEntityId();
                        chequeProcessing.setEntityId(entityId);
                        chequeProcessing.setEnteredBy(user);
                        chequeProcessing.setEnteredFlag('Y');
                        chequeProcessing.setEnteredTime(new Date());
                        chequeProcessing.setStatus(CONSTANTS.ENTERED);
                        chequeProcessing.setChequeRandCode(generateChequeCode(6).toUpperCase());
                        response.setMessage("Cheque Saved Successfully!");
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(chequeProcessingRepo.save(chequeProcessing));
                    } else {
                        return response;
                    }

                } else {
                    response = response;
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse modify(ChequeProcessing chequeProcessing) {
        try {
            EntityResponse response = new EntityResponse();
            if (auditDetails.checkUserAndEntity().getStatusCode() == HttpStatus.OK.value()) {
                Optional<ChequeProcessing> check = chequeProcessingRepo.findById(chequeProcessing.getId());
                if (check.isPresent()) {
                    if (check.get().getClearedFlag() == 'Y' || check.get().getBouncedFlag() == 'Y') {
                        response.setMessage("You can not modify cleared or bounced cheque!");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    } else {
                        String user = UserRequestContext.getCurrentUser();
                        String entityId = EntityRequestContext.getCurrentEntityId();
                        chequeProcessing.setEntityId(entityId);
                        chequeProcessing.setEnteredTime(new Date());
                        chequeProcessing.setEnteredBy(user);
                        chequeProcessing.setModifiedFlag('Y');
                        chequeProcessing.setVerifiedFlag('N');
                        chequeProcessing.setStatus(CONSTANTS.MODIFIED);
                        chequeProcessing.setStatus("Modified");
                        response.setMessage("Cheque Saved Successfully!");
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(chequeProcessingRepo.save(chequeProcessing));
                    }
                } else {
                    response.setMessage("Cheque Not Found!");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            } else {
                return response;
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse verify(String chequeRandCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (auditDetails.checkUserAndEntity().getStatusCode() == HttpStatus.OK.value()) {
                Optional<ChequeProcessing> chequeProcessing = chequeProcessingRepo.findByChequeRandCode(chequeRandCode);
                if (chequeProcessing.isPresent()) {
                    String user = UserRequestContext.getCurrentUser();
                    if (chequeProcessing.get().getVerifiedFlag() == 'N') {
                        ChequeProcessing chequeProcessing1 = chequeProcessing.get();
                        chequeProcessing1.setVerifiedFlag('Y');
                        chequeProcessing1.setVerifiedBy(user);
                        chequeProcessing1.setStatus(CONSTANTS.VERIFIED);
                        chequeProcessing1.setVerifiedTime(new Date());
                        chequeProcessingRepo.save(chequeProcessing1);
                        response.setMessage("First Verification is successful.");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(chequeProcessing1);
                    } else {
//                Second Verifier
                        if (chequeProcessing.get().getVerifiedFlag_2() == 'N') {
                            if (chequeProcessing.get().getVerifiedBy().equalsIgnoreCase(user)) {
                                response.setMessage("You can not do both verification! hint: The system support two verification first and second. Kindly check  with the second verifier if you verify first");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            } else {
                                ChequeProcessing chequeProcessing1 = chequeProcessing.get();
                                chequeProcessing1.setVerifiedFlag_2('Y');
                                chequeProcessing1.setVerifiedBy_2(user);
                                chequeProcessing1.setStatus(CONSTANTS.VERIFIED);
                                chequeProcessing1.setStatus("Verified");
                                chequeProcessing1.setVerifiedTime_2(new Date());
                                chequeProcessingRepo.save(chequeProcessing1);
                                response.setMessage("Second Verification is successful.");
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(chequeProcessing1);
                            }
                        } else {
                            response.setMessage("Transaction with transaction code " + chequeRandCode + " is already verified twice");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }
                } else {
                    response.setMessage("Transaction with transaction code " + chequeRandCode + " Not found");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            } else {
                return response;
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse bounce(String chequeRandCode, String penaltyCollAc, Double penaltyAmount) {
        try {
            EntityResponse response = new EntityResponse();
            if (chequeRandCode == null || chequeRandCode.isEmpty() || chequeRandCode.trim().isEmpty()) {
                response.setMessage("Cheque Random Code is Required");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (penaltyCollAc == null || penaltyCollAc.isEmpty() || penaltyCollAc.trim().isEmpty()) {
                response.setMessage("Penalty Collection Account is Required");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (penaltyAmount == null) {
                response.setMessage("Penalty Amount is Required");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                if (auditDetails.checkUserAndEntity().getStatusCode() == HttpStatus.OK.value()) {
                    Optional<ChequeProcessing> check = chequeProcessingRepo.findByChequeRandCode(chequeRandCode);
                    if (check.isPresent()) {
                        ChequeProcessing chequeProcessing = check.get();
                        if (chequeProcessing.getVerifiedFlag() == 'Y' && chequeProcessing.getVerifiedFlag_2() == 'Y') {
                            if (chequeProcessing.getClearedFlag() == 'N') {
                                if (chequeProcessing.getBouncedFlag() == 'N') {
                                    String user = UserRequestContext.getCurrentUser();
//                    post transaction
                                    String drAcid = chequeProcessing.getDebitOABAccount();
                                    String crAcid = chequeProcessing.getCreditCustOperativeAccount();
                                    Double amount = chequeProcessing.getAmount();
                                    String transactipnParticulars = "Cheque BOUNCE for " + chequeProcessing.getChequeNumber() + "and check system code " + chequeProcessing.getChequeRandCode();
                                    String transactionType = CONSTANTS.CHEQUE_BOUNCE;
                                    response = postTransaction(drAcid, crAcid, amount, transactipnParticulars, transactionType, null);
                                    if (response.getStatusCode() == HttpStatus.CREATED.value()) {
                                        JSONObject obj = new JSONObject(response.getEntity());
                                        String transactionCode = obj.get("transactionCode").toString();
                                        String transactionDate = obj.get("transactionDate").toString();
                                        Double charge = 0.00;
                                        JSONArray ja = obj.getJSONArray("partTrans");
                                        for (Object o : ja) {
                                            JSONObject ob = new JSONObject(o.toString());
                                            String parttranIdentity = ob.get("parttranIdentity").toString();
                                            if (parttranIdentity.equalsIgnoreCase(CONSTANTS.Fee)) {
                                                charge = ob.getDouble("transactionAmount");
                                            } else {
                                                charge = 0.00;
                                            }
                                        }
                                        chequeProcessing.setTransactionCode(transactionCode);
                                        chequeProcessing.setChargeAmount(charge);
                                        chequeProcessing.setTransactionDate(transactionDate);
                                        chequeProcessing.setClearedTime(new Date());
                                        chequeProcessing.setClearedBy(user);
                                        chequeProcessing.setStatus(CONSTANTS.BOUNCED);
                                        chequeProcessing.setBouncedBy(UserRequestContext.getCurrentUser());
                                        chequeProcessing.setClearedFlag('N');
                                        chequeProcessing.setBouncedFlag('Y');
                                        chequeProcessing.setStatus("Bounced");
                                        chequeProcessing = chequeProcessingRepo.save(chequeProcessing);
                                        response.setMessage("Cheque with code " + chequeRandCode + " bounced Successfully at! " + chequeProcessing.getClearedTime() + " Transaction Code is: " + transactionCode);
                                        response.setStatusCode(HttpStatus.CREATED.value());
                                        response.setEntity(chequeProcessing);
                                    } else {
                                        response = response;
                                    }
                                } else {
                                    response.setMessage("Cheque already bounced!");
                                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                                }
                            } else {
                                response.setMessage("Cheque already cleared!");
                                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            }
                        } else {
                            response.setMessage("Cheque Not verified!");
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        }
                    } else {
                        response.setMessage("Cheque Not Found!");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                } else {
                    return response;
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse clear(String chequeRandCode) {
        try {
            System.out.println("INSIDE Clearing now");
            EntityResponse response = new EntityResponse();
            if (auditDetails.checkUserAndEntity().getStatusCode() == HttpStatus.OK.value()) {
                Optional<ChequeProcessing> cheque = chequeProcessingRepo.findByChequeRandCode(chequeRandCode);
                if (cheque.isPresent()) {
                    ChequeProcessing chequeProcessing = cheque.get();
                    if (chequeProcessing.getVerifiedFlag() == 'Y' && chequeProcessing.getVerifiedFlag_2() == 'Y') {
                        if (chequeProcessing.getClearedFlag() == 'N') {
                            if (chequeProcessing.getBouncedFlag() == 'N') {
                                String user = UserRequestContext.getCurrentUser();

//                    post transaction
                                String drAcid = chequeProcessing.getDebitOABAccount();
                                String crAcid = chequeProcessing.getCreditCustOperativeAccount();
                                Double amount = chequeProcessing.getAmount();
                                Optional<ChequeProcessingRepo.ChequeCharge> chequeType = chequeProcessingRepo.getChequeCharges(chequeProcessing.getChargeCode());
                                String eventId = null;
                                if (chequeType.isPresent()) {
                                    System.out.println("Found cheque charge");
//                                    System.out.println(chequeType.get());
                                    System.out.println("Cheque Type");
                                    if (chequeType.get().getCollect_charges() == 'Y') {
                                        eventId = chequeType.get().getEvent_id();
                                    }
                                } else {
                                    System.out.println("Did not find cheque charge");
                                }
                                String transactipnParticulars = "Cheque Clearance for " + chequeProcessing.getChequeNumber() + "and check system code " + chequeProcessing.getChequeRandCode();
                                String transactionType = CONSTANTS.CHEQUE_CLEARENCE;
                                response = postTransaction(drAcid, crAcid, amount, transactipnParticulars, transactionType, eventId);
//                                System.out.println(response);
                                System.out.println("post transaction");
                                if (response.getStatusCode() == HttpStatus.CREATED.value()) {
                                    JSONObject obj = new JSONObject(response.getEntity());
                                    System.out.println("After JSON");
                                    String transactionCode = obj.get("transactionCode").toString();
                                    String transactionDate = obj.get("transactionDate").toString();
                                    Double charge = 0.00;
                                    JSONArray ja = obj.getJSONArray("partTrans");
                                    for (Object o : ja) {
                                        JSONObject ob = new JSONObject(o.toString());
                                        String parttranIdentity = ob.get("parttranIdentity").toString();
                                        if (parttranIdentity.equalsIgnoreCase(CONSTANTS.Fee)) {
                                            charge = ob.getDouble("transactionAmount");
                                        } else {
                                            charge = 0.00;
                                        }
                                    }
                                    chequeProcessing.setTransactionCode(transactionCode);
                                    chequeProcessing.setChargeAmount(charge);
                                    chequeProcessing.setTransactionDate(transactionDate);
                                    chequeProcessing.setClearedTime(new Date());
                                    chequeProcessing.setClearedBy(user);
                                    chequeProcessing.setStatus(CONSTANTS.CLEARED);
                                    chequeProcessing.setClearedFlag('Y');
                                    chequeProcessing.setStatus("Cleared");
                                    chequeProcessing = chequeProcessingRepo.save(chequeProcessing);
                                    response.setMessage("Cheque with code " + chequeRandCode + " cleared Successfully at! " + chequeProcessing.getClearedTime() + " Transaction Code is: " + transactionCode);
                                    response.setStatusCode(HttpStatus.CREATED.value());
                                    response.setEntity(chequeProcessing);
                                } else {
                                    response = response;
                                }
                            } else {
                                response.setMessage("Cheque already bounced!");
                                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            }
                        } else {
                            response.setMessage("Cheque already cleared!");
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        }
                    } else {
                        response.setMessage("Cheque Not verified!");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                } else {
                    response.setMessage("Cheque Not Found!");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            } else {
                return response;
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse delete(String chequeRandCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (auditDetails.checkUserAndEntity().getStatusCode() == HttpStatus.OK.value()) {
                Optional<ChequeProcessing> check = chequeProcessingRepo.findByChequeRandCode(chequeRandCode);
                if (check.isPresent()) {
                    ChequeProcessing chequeProcessing = check.get();
                    if (chequeProcessing.getPostedFlag() == 'Y') {
                        response.setMessage("Already posted");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    }
                    String user = UserRequestContext.getCurrentUser();
                    chequeProcessing.setDeletedTime(new Date());
                    chequeProcessing.setDeletedBy(user);
                    chequeProcessing.setDeletedFlag('Y');
                    chequeProcessing.setStatus(CONSTANTS.DELETED);
                    chequeProcessing.setStatus("Deleted");
                    response.setMessage("Cheque Deleted Successfully!");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(chequeProcessingRepo.save(chequeProcessing));
                } else {
                    response.setMessage("Cheque Not Found!");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            } else {
                return response;
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse fetchAll() {
        try {
            EntityResponse response = new EntityResponse();
            List<ChequeProcessingRepo.CheckProcessingAll> allCheques = chequeProcessingRepo.getAllCheques();
            if (allCheques.size() > 0) {
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(allCheques);
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

    public EntityResponse findAllUnVerifiedCheques() {
        try {
            EntityResponse response = new EntityResponse();
            List<ChequeProcessingRepo.CheckProcessingAll> allCheques = chequeProcessingRepo.getAllUnVerifiedCheques();
            if (allCheques.size() > 0) {
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(allCheques);
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

    public EntityResponse findByChequeRandCode(String chequeRandCode) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<ChequeProcessing> chequeProcessing = chequeProcessingRepo.findByChequeRandCode(chequeRandCode);
            if (chequeProcessing.isPresent()) {
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(chequeProcessing.get());
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

    public void deleteChequeProcessing(Long id) {
        try {
            chequeProcessingRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public EntityResponse postTransaction(String drAcid, String crAcid, Double amount, String transactipnParticulars, String transactionType, String chargeEventId) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            TransactionHeader transactionHeader = new TransactionHeader();
            transactionHeader.setChargeEventId(chargeEventId);
            transactionHeader.setVerifiedFlag('Y');
            transactionHeader.setVerifiedBy(user);
            transactionHeader.setVerifiedTime(new Date());
            transactionHeader.setVerifiedFlag_2('Y');
            transactionHeader.setVerifiedBy_2(user);
            transactionHeader.setVerifiedTime_2(new Date());
            transactionHeader.setTransactionType(transactionType);
            List<PartTran> partTranList = new ArrayList<>();
            PartTran partTranDr = new PartTran();
            partTranDr.setAcid(drAcid);
            partTranDr.setIsoFlag('Y');
            partTranDr.setExchangeRate("1");
            partTranDr.setParttranIdentity(CONSTANTS.Normal);
            partTranDr.setPartTranType(CONSTANTS.Debit);
            partTranDr.setTransactionAmount(amount);
            partTranDr.setTransactionDate(new Date());
            partTranDr.setTransactionParticulars(CONSTANTS.Debit + " " + transactipnParticulars);
            partTranList.add(partTranDr);

            PartTran partTranCr = new PartTran();
            partTranCr.setAcid(crAcid);
            partTranCr.setIsoFlag('Y');
            partTranCr.setExchangeRate("1");
            partTranDr.setParttranIdentity(CONSTANTS.Normal);
            partTranCr.setPartTranType(CONSTANTS.Credit);
            partTranCr.setTransactionAmount(amount);
            partTranCr.setTransactionDate(new Date());
            partTranCr.setTransactionParticulars(CONSTANTS.Credit + " " + transactipnParticulars);
            partTranList.add(partTranCr);
            transactionHeader.setPartTrans(partTranList);
            response = newTransactionService.enter(transactionHeader);
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse post(String chequeRandCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<ChequeProcessing> checkCheque = chequeProcessingRepo.findByChequeRandCode(chequeRandCode);
                if (checkCheque.isPresent()) {
                    ChequeProcessing chequeProcessing = checkCheque.get();
                    if (chequeProcessing.getPostedFlag() == 'N') {
                        if (chequeProcessing.getClearedFlag() == 'Y' || chequeProcessing.getBouncedFlag() == 'Y') {
                            response = newTransactionService.post(chequeProcessing.getTransactionCode());
                            if (response.getStatusCode() == HttpStatus.OK.value()) {
                                chequeProcessing.setPostedTime(new Date());
                                chequeProcessing.setPostedBy(user);
                                chequeProcessing.setPostedFlag('Y');
                                chequeProcessingRepo.save(chequeProcessing);
                                response.setMessage("Cheque has been posted successfully!");
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(chequeProcessing);
                            } else {
                                response = response;
                            }
                        } else {
                            response.setMessage("Cheque has not been cleared or bounced!");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    } else {
                        response.setMessage("Cheque has already been posted!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }

                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            log.error(e.getMessage());
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }

    public EntityResponse getTotalChequeForProcessing() {
        try {
            EntityResponse response = new EntityResponse<>();
            Integer totalChequesForProcessing = chequeProcessingRepo.countAllAccountsChequesForProcessing(EntityRequestContext.getCurrentEntityId());
            if (totalChequesForProcessing > 0) {
                response.setMessage("Total Unverified Cheques for Processing is:- " + totalChequesForProcessing);
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(totalChequesForProcessing);
            } else {
                response.setMessage("Total Unverified Cheques for Processing is:- " + totalChequesForProcessing);
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(totalChequesForProcessing);
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse rejectChequeProcessing(String chequeRandCode) {
        try {
            log.info("check one");
            EntityResponse response = new EntityResponse<>();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                } else {
                    Optional<ChequeProcessing> checkChequeRandCode = chequeProcessingRepo.findByChequeRandCode(chequeRandCode);
                    if (checkChequeRandCode.isPresent()) {
                        ChequeProcessing chequeProcessing = checkChequeRandCode.get();
                        if (chequeProcessing.getEnteredBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                            response.setMessage("Hello " + UserRequestContext.getCurrentUser() + ", You Can NOT REJECT Transaction That You Created!!");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        } else if (chequeProcessing.getVerifiedBy_2().equals('Y')) {
//                            response.setMessage("Cheque Processing with code " + chequeProcessing.getChequeRandCode() + " ALREADY VERIFIED: !!");
//                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        }
                        }
                        else if (chequeProcessing.getRejectedFlag().equals('Y')) {
                            response.setMessage("Cheque Processing ALREADY REJECTED");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        } else {
                            chequeProcessing.setRejectedBy(UserRequestContext.getCurrentUser());
                            chequeProcessing.setRejectedTime(new Date());
                            chequeProcessing.setRejectedFlag('Y');
                            chequeProcessing.setVerifiedFlag_2('N');
                            chequeProcessing.setVerifiedFlag('N');
                            chequeProcessing.setStatus("REJECTED");
                            ChequeProcessing rejectCheque = chequeProcessingRepo.save(chequeProcessing);
                            response.setMessage("Cheque Processing with code " + rejectCheque.getChequeRandCode() + " REJECTED Successfully at " + rejectCheque.getRejectedTime());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(rejectCheque);
                        }
                    } else {
                        response.setMessage("No Cheque Processing with the code : " + chequeRandCode + " Found in our DATABASES");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                }
            }


            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
