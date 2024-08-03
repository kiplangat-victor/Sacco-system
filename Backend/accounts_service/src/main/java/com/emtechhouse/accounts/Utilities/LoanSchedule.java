//package com.emtechhouse.accounts.Utilities;
//
//import org.springframework.stereotype.Component;
//
//import java.util.Calendar;
//import java.util.Date;
//
//@Component
//public class LoanSchedule {
//
//    public Date getRepaymentDate(String repaymentBasis, Date nextRepaymentDate) {
//        Calendar calendar = Calendar.getInstance();
//        if (repaymentBasis.equals("D")) {
//            calendar.setTime(nextRepaymentDate);
//            calendar.add(Calendar.DATE, 1);
//            nextRepaymentDate = calendar.getTime();
//        } else if (repaymentBasis.equals("W")) {
//            calendar.setTime(nextRepaymentDate);
//            calendar.add(Calendar.DATE, 7);
//            nextRepaymentDate = calendar.getTime();
//        } else if (repaymentBasis.equals("A")) {
//            calendar.setTime(nextRepaymentDate);
//            calendar.add(Calendar.YEAR, 1);
//            nextRepaymentDate = calendar.getTime();
//        } else if (repaymentBasis.equals("M")) {
//            calendar.setTime(nextRepaymentDate);
//            calendar.add(Calendar.MONTH, 1);
//            nextRepaymentDate = calendar.getTime();
//        }
//        return nextRepaymentDate;
//    }
//}