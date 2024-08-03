package com.emtechhouse.System.ChargeParams.EventId;

import com.emtechhouse.System.ChargeParams.EventId.TieredCharges.Tieredcharges;
import com.emtechhouse.System.ChargeParams.EventId.TieredCharges.TieredchargesRepo;
import com.emtechhouse.System.DTO.IncomingChargeCollectionReq;
import com.emtechhouse.System.DTO.PartTran;
import com.emtechhouse.System.Utils.CONSTANTS;
import com.emtechhouse.System.Utils.DataNotFoundException;
import com.emtechhouse.System.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventIdService {
    @Autowired
    private EventIdRepo eventIdRepo;
    @Autowired
    private TieredchargesRepo tieredchargesRepo;


    public EventId addEventId(EventId eventId) {
        try {
            return eventIdRepo.save(eventId);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<EventId> findAllEventIds() {
        try {
            return eventIdRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EventId findById(Long id) {
        try {
            return eventIdRepo.findById(id).orElseThrow(() -> new DataNotFoundException("Data " + id + "was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EventId findByEventIdCode(String eventIdCode) {
        try {
            return eventIdRepo.findByEventIdCode(eventIdCode).orElseThrow(() -> new DataNotFoundException("Data " + eventIdCode + "was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse collectChargesOld(List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = new EntityResponse();
            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.FOUND.value());
            List<PartTran> parttrans = new ArrayList<>();
            for (int i = 0; i < incomingChargeCollectionReqs.size(); i++) {
                String chargeCode = incomingChargeCollectionReqs.get(i).getChargeCode();
                Double transactionAmt = incomingChargeCollectionReqs.get(i).getTransactionAmount();
                String debitAc = incomingChargeCollectionReqs.get(i).getDebitAc();
                String transParticular = incomingChargeCollectionReqs.get(i).getTransParticulars();
                Optional<EventId> eventId = eventIdRepo.findByEventIdCodeAndDeletedFlag(chargeCode, 'N');
                if (eventId.isPresent()) {
                    String amt_derivation_type = eventId.get().getAmt_derivation_type();
                    Double chrgAmount = 0.00;
                    Double monthlyFee = 0.00;
                    String chrgCollAcc = eventId.get().getAc_placeholder();
                    String chrgCollCurr = eventId.get().getChrg_coll_crncy();
                    String exciseDutyCollAc = eventId.get().getExciseDutyCollAc();
                    monthlyFee = eventId.get().getMonthlyFee();
                    if (amt_derivation_type.equalsIgnoreCase("FIXED")) {
                        chrgAmount = eventId.get().getAmt();
                    } else if (amt_derivation_type.equalsIgnoreCase("PCNT")) {
                        chrgAmount = (eventId.get().getPercentage() / 100) * transactionAmt;
                        if (chrgAmount < eventId.get().getMin_amt()) {
                            chrgAmount = eventId.get().getMin_amt();
                        } else if (chrgAmount > eventId.get().getMax_amt()) {
                            chrgAmount = eventId.get().getMax_amt();
                        }
                    } else if (amt_derivation_type.equalsIgnoreCase("TC")) {
                        Optional<Tieredcharges> tieredchargesCheck = tieredchargesRepo.findBetweenRange(String.valueOf(transactionAmt), eventId.get().getId());
                        if (tieredchargesCheck.isPresent()) {
                            Tieredcharges tieredcharges = tieredchargesCheck.get();
                            if (tieredcharges.getUsePercentage() == 'Y') {
                                chrgAmount = (tieredcharges.getPercentage() / 100) * transactionAmt;
                            } else {
                                chrgAmount = tieredcharges.getChargeAmount();
                            }
                        } else {
                            Optional<Tieredcharges> tieredchargesMax = tieredchargesRepo.findMaxByEventId(eventId.get().getId());
                            if (tieredchargesMax.isPresent()) {
                                chrgAmount = tieredchargesMax.get().getChargeAmount();
                            } else {
                                chrgAmount = 0.00;
                            }
                        }
                    }
                    if (chrgAmount > transactionAmt) {
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setMessage("Insufficient transaction amount to deduct withdrawal fee of " + chrgAmount);
                    } else {
                        Double excisDutyAmount = 0.00;
//                    collect Excise Duty
                        if (eventId.get().getHas_exercise_duty().equalsIgnoreCase("Y")) {
                            excisDutyAmount = (eventId.get().getExercise_duty_percentage() / 100) * chrgAmount;
//                        build a parttran
                            PartTran partTranDebit = new PartTran();
                            partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                            partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                            partTranDebit.setMonthlyFee(monthlyFee);
                            partTranDebit.setTransactionAmount(excisDutyAmount);
                            partTranDebit.setAcid(debitAc);
                            partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranDebit);

                            PartTran partTranCredit = new PartTran();
                            partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                            partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                            partTranCredit.setMonthlyFee(monthlyFee);
                            partTranCredit.setTransactionAmount(excisDutyAmount);
                            partTranCredit.setAcid(exciseDutyCollAc);
                            partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranCredit);
                        }
                        PartTran partTranDebit = new PartTran();
                        partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                        partTranDebit.setParttranIdentity(CONSTANTS.Fee);
                        partTranDebit.setMonthlyFee(monthlyFee);
                        partTranDebit.setTransactionAmount(chrgAmount);
                        partTranDebit.setAcid(debitAc);
                        partTranDebit.setCurrency(chrgCollCurr);
                        partTranDebit.setTransactionParticulars(eventId.get().getTran_particulars());
                        parttrans.add(partTranDebit);

                        PartTran partTranCredit = new PartTran();
                        partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                        partTranCredit.setParttranIdentity(CONSTANTS.Fee);
                        partTranCredit.setMonthlyFee(monthlyFee);
                        partTranCredit.setTransactionAmount(chrgAmount);
                        partTranCredit.setAcid(chrgCollAcc);
                        partTranCredit.setCurrency(chrgCollCurr);
                        partTranCredit.setTransactionParticulars(eventId.get().getTran_particulars());
                        parttrans.add(partTranCredit);
                        response.setEntity(parttrans);
                    }
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse collectCharges(List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = new EntityResponse();
            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.FOUND.value());
            List<PartTran> parttrans = new ArrayList<>();
            for (int i = 0; i < incomingChargeCollectionReqs.size(); i++) {
                String chargeCode = incomingChargeCollectionReqs.get(i).getChargeCode();
                Double transactionAmt = incomingChargeCollectionReqs.get(i).getTransactionAmount();
                String debitAc = incomingChargeCollectionReqs.get(i).getDebitAc();
                Optional<EventId> eventId = eventIdRepo.findByEventIdCodeAndDeletedFlag(chargeCode, 'N');
                String transParticular = eventId.get().getTran_particulars();
                if (eventId.isPresent()) {
                    String amt_derivation_type = eventId.get().getAmt_derivation_type();
                    Double chrgAmount = 0.00;
                    Double monthlyFee = 0.00;
                    String chrgCollAcc = eventId.get().getAc_placeholder();
                    String chrgCollCurr = eventId.get().getChrg_coll_crncy();
                    String exciseDutyCollAc = eventId.get().getExciseDutyCollAc();
                    monthlyFee = eventId.get().getMonthlyFee();
                    if (amt_derivation_type.equalsIgnoreCase("FIXED")) {
                        chrgAmount = eventId.get().getAmt();
                        System.out.println("Charge CHARGE Amount: "+chrgAmount);
                    } else if (amt_derivation_type.equalsIgnoreCase("PCNT")) {
                        chrgAmount = (eventId.get().getPercentage() / 100) * transactionAmt;
                        if (chrgAmount < eventId.get().getMin_amt()) {
                            chrgAmount = eventId.get().getMin_amt();
                        } else if (chrgAmount > eventId.get().getMax_amt()) {
                            chrgAmount = eventId.get().getMax_amt();
                        }
                    } else if (amt_derivation_type.equalsIgnoreCase("TC")) {
                        Optional<Tieredcharges> tieredchargesCheck = tieredchargesRepo.findBetweenRange(String.valueOf(transactionAmt), eventId.get().getId());
                        if (tieredchargesCheck.isPresent()) {
                            Tieredcharges tieredcharges = tieredchargesCheck.get();
                            if (tieredcharges.getUsePercentage() == 'Y') {
                                chrgAmount = (tieredcharges.getPercentage() / 100) * transactionAmt;
                            } else {
                                chrgAmount = tieredcharges.getChargeAmount();
                                System.out.println("HERE WITH CHARGE Amount: "+chrgAmount);
                            }
                        } else {
                            Optional<Tieredcharges> tieredchargesMax = tieredchargesRepo.findMaxByEventId(eventId.get().getId());
                            if (tieredchargesMax.isPresent()) {
                                chrgAmount = tieredchargesMax.get().getChargeAmount();
                                System.out.println("HERE WITH CHArge MAXIMUM Amount: "+chrgAmount);
                            } else {
                                chrgAmount = 0.00;
                            }
                        }
                    }
                    if (chrgAmount > transactionAmt) {
                        log.info("charge amount :: " + chrgAmount);
                        log.info("transaction amt :: " + transactionAmt);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setMessage("Insufficient transaction amount to deduct withdrawal fee of " + chrgAmount);
                    } else {
                        Double excisDutyAmount = 0.00;
//                    collect Excise Duty
                        log.info("Checking the exercise duty " + eventId.get());
                        if (eventId.get().getHas_exercise_duty().equalsIgnoreCase("Y")) {
                            log.info("Checking the exercise duty -Y");
                            if (eventId.get().getExcise_duty_derivation().equalsIgnoreCase("FIXED")) {
                                log.info("Checking the exercise duty -FIXED");
                                excisDutyAmount = eventId.get().getExercise_duty_fixed_amt();
//                        build a parttran
                                PartTran partTranDebit = new PartTran();
                                partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                                partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                                partTranDebit.setMonthlyFee(monthlyFee);
                                partTranDebit.setTransactionAmount(excisDutyAmount);
                                partTranDebit.setAcid(debitAc);
                                partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                                partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                                parttrans.add(partTranDebit);

                                PartTran partTranCredit = new PartTran();
                                partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                                partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                                partTranCredit.setMonthlyFee(monthlyFee);
                                partTranCredit.setTransactionAmount(excisDutyAmount);
                                partTranCredit.setAcid(exciseDutyCollAc);
                                partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                                partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                                parttrans.add(partTranCredit);
                            } else if (eventId.get().getExcise_duty_derivation().equalsIgnoreCase("PCNT")) {

                                log.info("Checking the exercise duty-percent");

                                excisDutyAmount = (eventId.get().getExercise_duty_percentage() / 100) * chrgAmount;
//                        build a parttran
                                PartTran partTranDebit = new PartTran();
                                partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                                partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                                partTranDebit.setMonthlyFee(monthlyFee);
                                partTranDebit.setTransactionAmount(excisDutyAmount);
                                partTranDebit.setAcid(debitAc);
                                partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                                partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                                parttrans.add(partTranDebit);

                                PartTran partTranCredit = new PartTran();
                                partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                                partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                                partTranCredit.setMonthlyFee(monthlyFee);
                                partTranCredit.setTransactionAmount(excisDutyAmount);
                                partTranCredit.setAcid(exciseDutyCollAc);
                                partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                                partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                                parttrans.add(partTranCredit);
                            } else {

                            }
                        }

                        log.info("Checking the exercise duty - no exercise duty");

                        PartTran partTranDebit = new PartTran();
                        partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                        partTranDebit.setParttranIdentity(CONSTANTS.Fee);
                        log.info("Monthly fee :: " + monthlyFee);
                        partTranDebit.setMonthlyFee(monthlyFee);
                        log.info("Charge amt FOR THE THE Q :: " + chrgAmount);
                        partTranDebit.setTransactionAmount(chrgAmount);
                        log.info("Debit acid :: " + debitAc);
                        partTranDebit.setAcid(debitAc);
                        partTranDebit.setCurrency(chrgCollCurr);
                        partTranDebit.setTransactionParticulars(transParticular);
                        parttrans.add(partTranDebit);

                        log.info("Creating partran header");

                        PartTran partTranCredit = new PartTran();
                        partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                        partTranCredit.setParttranIdentity(CONSTANTS.Fee);
                        partTranCredit.setMonthlyFee(monthlyFee);
                        partTranCredit.setTransactionAmount(chrgAmount);
                        partTranCredit.setAcid(chrgCollAcc);
                        log.info("Charge collection account: " + chrgCollAcc);
                        partTranCredit.setCurrency(chrgCollCurr);
                        partTranCredit.setTransactionParticulars(transParticular);
                        parttrans.add(partTranCredit);
                        response.setEntity(parttrans);
                    }
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//

    public EntityResponse collectAccountActivationFees(List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = new EntityResponse();
            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.FOUND.value());
            List<PartTran> parttrans = new ArrayList<>();

            for (int i = 0; i < incomingChargeCollectionReqs.size(); i++) {

                String chargeCode = incomingChargeCollectionReqs.get(i).getChargeCode();
                Optional<EventId> eventId = eventIdRepo.findByEventIdCodeAndDeletedFlag(chargeCode, 'N');
                String transParticular = eventId.get().getTran_particulars();
                String debitAc = incomingChargeCollectionReqs.get(i).getDebitAc();
                if (eventId.isPresent()) {
                    // Fixed activation fee amount
                    Double chrgAmount = 300.00;

                    // Excise duty details
                    String chrgCollAcc = eventId.get().getAc_placeholder();
                    String chrgCollCurr = eventId.get().getChrg_coll_crncy();
                    String exciseDutyCollAc = eventId.get().getExciseDutyCollAc();
                    Double excisDutyAmount = 0.00;
                    Double monthlyFee = 0.00;
                    log.info("Checking the exercise duty " + eventId.get());
                    if (eventId.get().getHas_exercise_duty().equalsIgnoreCase("Y")) {
                        log.info("Checking the exercise duty -Y");
                        if (eventId.get().getExcise_duty_derivation().equalsIgnoreCase("FIXED")) {
                            log.info("Checking the exercise duty -FIXED");
                            excisDutyAmount = eventId.get().getExercise_duty_fixed_amt();
//                        build a parttran
                            PartTran partTranDebit = new PartTran();
                            partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                            partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                            partTranDebit.setMonthlyFee(monthlyFee);
                            partTranDebit.setTransactionAmount(excisDutyAmount);
                            partTranDebit.setAcid(debitAc);
                            partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranDebit);

                            PartTran partTranCredit = new PartTran();
                            partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                            partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                            partTranCredit.setMonthlyFee(monthlyFee);
                            partTranCredit.setTransactionAmount(excisDutyAmount);
                            partTranCredit.setAcid(exciseDutyCollAc);
                            partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranCredit);
                        } else if (eventId.get().getExcise_duty_derivation().equalsIgnoreCase("PCNT")) {

                            log.info("Checking the exercise duty-percent");

                            excisDutyAmount = (eventId.get().getExercise_duty_percentage() / 100) * chrgAmount;
//                        build a parttran
                            PartTran partTranDebit = new PartTran();
                            partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                            partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                            partTranDebit.setMonthlyFee(monthlyFee);
                            partTranDebit.setTransactionAmount(excisDutyAmount);
                            partTranDebit.setAcid(debitAc);
                            partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranDebit);

                            PartTran partTranCredit = new PartTran();
                            partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                            partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                            partTranCredit.setMonthlyFee(monthlyFee);
                            partTranCredit.setTransactionAmount(excisDutyAmount);
                            partTranCredit.setAcid(exciseDutyCollAc);
                            partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranCredit);
                        }
                    }

                    log.info("Checking the exercise duty - no exercise duty");

                    PartTran partTranDebit = new PartTran();
                    partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                    partTranDebit.setParttranIdentity(CONSTANTS.Fee);
                    log.info("Monthly fee :: " + monthlyFee);
                    partTranDebit.setMonthlyFee(monthlyFee);
                    log.info("Charge amt :: " + chrgAmount);
                    partTranDebit.setTransactionAmount(chrgAmount);
                    log.info("Debit acid :: " + debitAc);
                    partTranDebit.setAcid(debitAc);
                    partTranDebit.setCurrency(chrgCollCurr);
                    partTranDebit.setTransactionParticulars(transParticular);
                    parttrans.add(partTranDebit);

                    log.info("Creating partran header");

                    PartTran partTranCredit = new PartTran();
                    partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                    partTranCredit.setParttranIdentity(CONSTANTS.Fee);
                    partTranCredit.setMonthlyFee(monthlyFee);
                    partTranCredit.setTransactionAmount(chrgAmount);
                    partTranCredit.setAcid(chrgCollAcc);
                    partTranCredit.setCurrency(chrgCollCurr);
                    partTranCredit.setTransactionParticulars(transParticular);
                    parttrans.add(partTranCredit);
                    response.setEntity(parttrans);
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }

                return response;

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse collectMoneyTransferCharges(List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = new EntityResponse();
            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.FOUND.value());
            List<PartTran> parttrans = new ArrayList<>();

            for (int i = 0; i < incomingChargeCollectionReqs.size(); i++) {

                String chargeCode = incomingChargeCollectionReqs.get(i).getChargeCode();
                Double transactionAmt = incomingChargeCollectionReqs.get(i).getTransactionAmount();
                System.out.println("Transaction Amount: "+transactionAmt);
//                Double transactionAmt = 1.00;
                System.out.println("Charge Code: "+chargeCode);
                Optional<EventId> eventId = eventIdRepo.findByEventIdCodeAndDeletedFlag(chargeCode, 'N');
                String transParticular = eventId.get().getTran_particulars();
                double chargeAmount = eventId.get().getAmt();
                System.out.println("Charge Amount: "+chargeAmount);
                String debitAc = incomingChargeCollectionReqs.get(i).getDebitAc();
                if (eventId.isPresent()) {
                    String amt_derivation_type = eventId.get().getAmt_derivation_type();
                    Double chrgAmount = 0.00;
                    Double monthlyFee = 0.00;
                    String chrgCollAcc = eventId.get().getAc_placeholder();
                    String chrgCollCurr = eventId.get().getChrg_coll_crncy();
                    String exciseDutyCollAc = eventId.get().getExciseDutyCollAc();
                    monthlyFee = eventId.get().getMonthlyFee();
                    if (amt_derivation_type.equalsIgnoreCase("FIXED")) {
                        chrgAmount = eventId.get().getAmt();
                        System.out.println("Charge CHARGE Amount: "+chrgAmount);
                    } else if (amt_derivation_type.equalsIgnoreCase("PCNT")) {
                        chrgAmount = (eventId.get().getPercentage() / 100) * transactionAmt;
                        if (chrgAmount < eventId.get().getMin_amt()) {
                            chrgAmount = eventId.get().getMin_amt();
                        } else if (chrgAmount > eventId.get().getMax_amt()) {
                            chrgAmount = eventId.get().getMax_amt();
                        }
                    } else if (amt_derivation_type.equalsIgnoreCase("TC")) {
                        Optional<Tieredcharges> tieredchargesCheck = tieredchargesRepo.findBetweenRange(String.valueOf(transactionAmt), eventId.get().getId());
                        if (tieredchargesCheck.isPresent()) {
                            Tieredcharges tieredcharges = tieredchargesCheck.get();
                            if (tieredcharges.getUsePercentage() == 'Y') {
                                chrgAmount = (tieredcharges.getPercentage() / 100) * transactionAmt;
                            } else {
                                chrgAmount = tieredcharges.getChargeAmount();
                                System.out.println("HERE WITH CHARGE Amount: "+chrgAmount);
                            }
                        } else {
                            Optional<Tieredcharges> tieredchargesMax = tieredchargesRepo.findMaxByEventId(eventId.get().getId());
                            if (tieredchargesMax.isPresent()) {
                                chrgAmount = tieredchargesMax.get().getChargeAmount();
                                System.out.println("HERE WITH CHArge MAXIMUM Amount: "+chrgAmount);
                            } else {
                                chrgAmount = 0.00;
                            }
                        }
                    }
                    if (chrgAmount > transactionAmt) {
                        log.info("charge amount :: " + chrgAmount);
                        log.info("transaction amt :: " + transactionAmt);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setMessage("Insufficient transaction amount to deduct withdrawal fee of " + chrgAmount);
                    } else
                    {
                        Double excisDutyAmount = 0.00;
//                    collect Excise Duty
                        log.info("Checking the exercise duty " + eventId.get());
                        if (eventId.get().getHas_exercise_duty().equalsIgnoreCase("Y")) {
                            log.info("Checking the exercise duty -Y");
                            if (eventId.get().getExcise_duty_derivation().equalsIgnoreCase("FIXED")) {
                                log.info("Checking the exercise duty -FIXED");
                                excisDutyAmount = eventId.get().getExercise_duty_fixed_amt();
//                        build a parttran
                                PartTran partTranDebit = new PartTran();
                                partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                                partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                                partTranDebit.setMonthlyFee(monthlyFee);
                                partTranDebit.setTransactionAmount(excisDutyAmount);
                                partTranDebit.setAcid(debitAc);
                                partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                                partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                                parttrans.add(partTranDebit);

                                PartTran partTranCredit = new PartTran();
                                partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                                partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                                partTranCredit.setMonthlyFee(monthlyFee);
                                partTranCredit.setTransactionAmount(excisDutyAmount);
                                partTranCredit.setAcid(exciseDutyCollAc);
                                partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                                partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                                parttrans.add(partTranCredit);
                            } else if (eventId.get().getExcise_duty_derivation().equalsIgnoreCase("PCNT")) {

                                log.info("Checking the exercise duty-percent");

                                excisDutyAmount = (eventId.get().getExercise_duty_percentage() / 100) * chrgAmount;
                                System.out.println("EXCISE DUTY: "+excisDutyAmount);
//                        build a parttran
                                PartTran partTranDebit = new PartTran();
                                partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                                partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                                partTranDebit.setMonthlyFee(monthlyFee);
                                partTranDebit.setTransactionAmount(excisDutyAmount);
                                partTranDebit.setAcid(debitAc);
                                partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                                partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                                parttrans.add(partTranDebit);

                                PartTran partTranCredit = new PartTran();
                                partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                                partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                                partTranCredit.setMonthlyFee(monthlyFee);
                                partTranCredit.setTransactionAmount(excisDutyAmount);
                                partTranCredit.setAcid(exciseDutyCollAc);
                                partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                                partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                                parttrans.add(partTranCredit);
                            } else {

                            }
                        }

                        log.info("Checking the exercise duty - no exercise duty");

                        PartTran partTranDebit = new PartTran();
                        partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                        partTranDebit.setParttranIdentity(CONSTANTS.Fee);
                        log.info("Monthly fee :: " + monthlyFee);
                        partTranDebit.setMonthlyFee(monthlyFee);
                        log.info("Charge amt FOR THE THE Q :: " + chrgAmount);
                        partTranDebit.setTransactionAmount(chrgAmount);
                        log.info("Debit acid :: " + debitAc);
                        partTranDebit.setAcid(debitAc);
                        partTranDebit.setCurrency(chrgCollCurr);
                        partTranDebit.setTransactionParticulars(transParticular);
                        parttrans.add(partTranDebit);

                        log.info("Creating partran header");

                        PartTran partTranCredit = new PartTran();
                        partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                        partTranCredit.setParttranIdentity(CONSTANTS.Fee);
                        partTranCredit.setMonthlyFee(monthlyFee);
                        partTranCredit.setTransactionAmount(chrgAmount);
                        partTranCredit.setAcid(chrgCollAcc);
                        log.info("Charge collection account: " + chrgCollAcc);
                        partTranCredit.setCurrency(chrgCollCurr);
                        partTranCredit.setTransactionParticulars(transParticular);
                        parttrans.add(partTranCredit);
                        response.setEntity(parttrans);
                    }
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }

            return response;

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }



    public EntityResponse balanceEnquiryCharges(List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = new EntityResponse();
            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.FOUND.value());
            List<PartTran> parttrans = new ArrayList<>();

            for (int i = 0; i < incomingChargeCollectionReqs.size(); i++) {

                String chargeCode = incomingChargeCollectionReqs.get(i).getChargeCode();
                System.out.println("Charge Code: "+chargeCode);
                Optional<EventId> eventId = eventIdRepo.findByEventIdCodeAndDeletedFlag(chargeCode, 'N');
                String transParticular = eventId.get().getTran_particulars();
                double chargeAmounts = eventId.get().getAmt();
                System.out.println("Charge Amount: "+chargeAmounts);
                String debitAc = incomingChargeCollectionReqs.get(i).getDebitAc();
                if (eventId.isPresent()) {
                    // Fixed activation fee amount
                    Double chrgAmount = 0.00;

                    // Excise duty details
                    String chrgCollAcc = eventId.get().getAc_placeholder();
                    String chrgCollCurr = eventId.get().getChrg_coll_crncy();
                    String exciseDutyCollAc = eventId.get().getExciseDutyCollAc();
                    Double excisDutyAmount = 0.00;
                    Double monthlyFee = 0.00;
                    log.info("Checking the exercise duty " + eventId.get());
                    if (eventId.get().getHas_exercise_duty().equalsIgnoreCase("Y")) {
                        log.info("Checking the exercise duty -Y");
                        if (eventId.get().getExcise_duty_derivation().equalsIgnoreCase("FIXED")) {
                            log.info("Checking the exercise duty -FIXED");
                            excisDutyAmount = eventId.get().getExercise_duty_fixed_amt();
//                        build a parttran
                            PartTran partTranDebit = new PartTran();
                            partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                            partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                            partTranDebit.setMonthlyFee(monthlyFee);
                            partTranDebit.setTransactionAmount(excisDutyAmount);
                            partTranDebit.setAcid(debitAc);
                            partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranDebit);

                            PartTran partTranCredit = new PartTran();
                            partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                            partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                            partTranCredit.setMonthlyFee(monthlyFee);
                            partTranCredit.setTransactionAmount(excisDutyAmount);
                            partTranCredit.setAcid(exciseDutyCollAc);
                            partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranCredit);
                        } else if (eventId.get().getExcise_duty_derivation().equalsIgnoreCase("PCNT")) {

                            log.info("Checking the exercise duty-percent");

                            excisDutyAmount = (eventId.get().getExercise_duty_percentage() / 100) * chargeAmounts;
//                        build a parttran
                            PartTran partTranDebit = new PartTran();
                            partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                            partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                            partTranDebit.setMonthlyFee(monthlyFee);
                            partTranDebit.setTransactionAmount(excisDutyAmount);
                            partTranDebit.setAcid(debitAc);
                            partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranDebit);

                            PartTran partTranCredit = new PartTran();
                            partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                            partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                            partTranCredit.setMonthlyFee(monthlyFee);
                            partTranCredit.setTransactionAmount(excisDutyAmount);
                            partTranCredit.setAcid(exciseDutyCollAc);
                            partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranCredit);
                        }
                    }

                    log.info("Checking the exercise duty - no exercise duty");

                    PartTran partTranDebit = new PartTran();
                    partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                    partTranDebit.setParttranIdentity(CONSTANTS.Fee);
                    log.info("Monthly fee :: " + monthlyFee);
                    partTranDebit.setMonthlyFee(monthlyFee);
                    log.info("Charge amt :: " + chargeAmounts);
                    partTranDebit.setTransactionAmount(chargeAmounts);
                    log.info("Debit acid :: " + debitAc);
                    partTranDebit.setAcid(debitAc);
                    partTranDebit.setCurrency(chrgCollCurr);
                    partTranDebit.setTransactionParticulars(transParticular);
                    parttrans.add(partTranDebit);

                    log.info("Creating partran header");

                    PartTran partTranCredit = new PartTran();
                    partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                    partTranCredit.setParttranIdentity(CONSTANTS.Fee);
                    partTranCredit.setMonthlyFee(monthlyFee);
                    partTranCredit.setTransactionAmount(chargeAmounts);
                    partTranCredit.setAcid(chrgCollAcc);
                    partTranCredit.setCurrency(chrgCollCurr);
                    partTranCredit.setTransactionParticulars(transParticular);
                    parttrans.add(partTranCredit);
                    response.setEntity(parttrans);
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }

            return response;

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }




    public EntityResponse smsCharges(List<IncomingChargeCollectionReq> incomingChargeCollectionReqs) {
        try {
            EntityResponse response = new EntityResponse();
            response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.FOUND.value());
            List<PartTran> parttrans = new ArrayList<>();

            for (int i = 0; i < incomingChargeCollectionReqs.size(); i++) {

                String chargeCode = incomingChargeCollectionReqs.get(i).getChargeCode();
                System.out.println("Charge Code: "+chargeCode);
                Optional<EventId> eventId = eventIdRepo.findByEventIdCodeAndDeletedFlag(chargeCode, 'N');
                String transParticular = eventId.get().getTran_particulars();
                double chargeAmounts = eventId.get().getAmt();
                System.out.println("Charge Amount: "+chargeAmounts);
                String debitAc = incomingChargeCollectionReqs.get(i).getDebitAc();
                if (eventId.isPresent()) {
                    // Fixed activation fee amount
                    Double chrgAmount = 0.00;

                    // Excise duty details
                    String chrgCollAcc = eventId.get().getAc_placeholder();
                    String chrgCollCurr = eventId.get().getChrg_coll_crncy();
                    String exciseDutyCollAc = eventId.get().getExciseDutyCollAc();
                    Double excisDutyAmount = 0.00;
                    Double monthlyFee = 0.00;
                    log.info("Checking the exercise duty " + eventId.get());
                    if (eventId.get().getHas_exercise_duty().equalsIgnoreCase("Y")) {
                        log.info("Checking the exercise duty -Y");
                        if (eventId.get().getExcise_duty_derivation().equalsIgnoreCase("FIXED")) {
                            log.info("Checking the exercise duty -FIXED");
                            excisDutyAmount = eventId.get().getExercise_duty_fixed_amt();
//                        build a parttran
                            PartTran partTranDebit = new PartTran();
                            partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                            partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                            partTranDebit.setMonthlyFee(monthlyFee);
                            partTranDebit.setTransactionAmount(excisDutyAmount);
                            partTranDebit.setAcid(debitAc);
                            partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranDebit);

                            PartTran partTranCredit = new PartTran();
                            partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                            partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                            partTranCredit.setMonthlyFee(monthlyFee);
                            partTranCredit.setTransactionAmount(excisDutyAmount);
                            partTranCredit.setAcid(exciseDutyCollAc);
                            partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranCredit);
                        } else if (eventId.get().getExcise_duty_derivation().equalsIgnoreCase("PCNT")) {

                            log.info("Checking the exercise duty-percent");

                            excisDutyAmount = (eventId.get().getExercise_duty_percentage() / 100) * chargeAmounts;
//                        build a parttran
                            PartTran partTranDebit = new PartTran();
                            partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                            partTranDebit.setParttranIdentity(CONSTANTS.Tax);
                            partTranDebit.setMonthlyFee(monthlyFee);
                            partTranDebit.setTransactionAmount(excisDutyAmount);
                            partTranDebit.setAcid(debitAc);
                            partTranDebit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranDebit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranDebit);

                            PartTran partTranCredit = new PartTran();
                            partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                            partTranCredit.setParttranIdentity(CONSTANTS.Tax);
                            partTranCredit.setMonthlyFee(monthlyFee);
                            partTranCredit.setTransactionAmount(excisDutyAmount);
                            partTranCredit.setAcid(exciseDutyCollAc);
                            partTranCredit.setCurrency(eventId.get().getExciseDutyCollAc());
                            partTranCredit.setTransactionParticulars("Excise duty for  " + transParticular);
                            parttrans.add(partTranCredit);
                        }
                    }

                    log.info("Checking the exercise duty - no exercise duty");

                    PartTran partTranDebit = new PartTran();
                    partTranDebit.setPartTranType(CONSTANTS.DEBIT);
                    partTranDebit.setParttranIdentity(CONSTANTS.Fee);
                    log.info("Monthly fee :: " + monthlyFee);
                    partTranDebit.setMonthlyFee(monthlyFee);
                    log.info("Charge amt :: " + chargeAmounts);
                    partTranDebit.setTransactionAmount(chargeAmounts);
                    log.info("Debit acid :: " + debitAc);
                    partTranDebit.setAcid(debitAc);
                    partTranDebit.setCurrency(chrgCollCurr);
                    partTranDebit.setTransactionParticulars(transParticular);
                    parttrans.add(partTranDebit);

                    log.info("Creating partran header");

                    PartTran partTranCredit = new PartTran();
                    partTranCredit.setPartTranType(CONSTANTS.CREDIT);
                    partTranCredit.setParttranIdentity(CONSTANTS.Fee);
                    partTranCredit.setMonthlyFee(monthlyFee);
                    partTranCredit.setTransactionAmount(chargeAmounts);
                    partTranCredit.setAcid(chrgCollAcc);
                    partTranCredit.setCurrency(chrgCollCurr);
                    partTranCredit.setTransactionParticulars(transParticular);
                    parttrans.add(partTranCredit);
                    response.setEntity(parttrans);
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }

            return response;

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }





    //
    public EventId updateEventId(EventId eventId) {
        try {
            return eventIdRepo.save(eventId);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteEventId(Long id) {
        try {
            eventIdRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}

