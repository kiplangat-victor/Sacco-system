//package com.emtechhouse.accounts.Utils;
//
//import com.emtechhouse.accounts.JsonSchemas.Interest;
//import org.joda.time.DateTime;
//import org.joda.time.Days;
//import org.joda.time.Years;
//import org.springframework.stereotype.Component;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.concurrent.atomic.AtomicReference;
//
//@Component
//public class CalculateInterest {
//
//    /*
//     * Cash interest is interest that is calculated upfront during loan disbursement
//     * Repayment Durations are in months
//     * Author @Wilfred Mwangi
//     */
//
//    public Double loanProductInterest(Integer repaymentPeriod, Double principle, Interest interestCode, Character interestBase){
//        Double interest = 0.00;
//        Double period = repaymentPeriod.doubleValue();
//        Date today = new Date();
//        AtomicReference<Double> rate = new AtomicReference<>(0.00);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(today);
//        calendar.add(Calendar.MONTH, repaymentPeriod);
//        Date dueDate = calendar.getTime();
//        DateTime currentDateTime = new DateTime(new Date().getTime());
//        DateTime dueDateTime = new DateTime(dueDate.getTime());
//        if (interestBase.equals(CONSTANTS.DAILY)) {
//            period = (double)  Days.daysBetween(currentDateTime, dueDateTime).getDays();
//        } else if (interestBase.equals(CONSTANTS.WEEKLY)) {
//            period = Math.ceil( (double) Days.daysBetween(currentDateTime, dueDateTime).getDays() / 7);
//        } else if (interestBase.equals(CONSTANTS.MONTHLY)) {
//            period = Math.ceil(repaymentPeriod);
//        } else if (interestBase.equals(CONSTANTS.ANNUAL)){
//            period = (double) repaymentPeriod/12;
//        }
//
//        if(interestCode.getFullDiff().equals(CONSTANTS.FULL)){
//            interestCode.getAmountSlabs().forEach(amountSlab -> {
//                if(amountSlab.getDrCr().equals(CONSTANTS.DEBIT)){
//                    rate.set(amountSlab.getRate());
//                }
//            });
//        }else{
//            interestCode.getAmountSlabs().forEach(amountSlab -> {
//                if(amountSlab.getDrCr().equals(CONSTANTS.DEBIT)){
//                    if(principle >= amountSlab.getFromAmount() && principle  <= amountSlab.getFromAmount()){
//                        rate.set(amountSlab.getRate());
//                    }
//                }
//            });
//        }
//
//        interest = principle * (rate.get()/100) * period;
//        return interest;
//    }
//}
