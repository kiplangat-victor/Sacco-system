package com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LoanBookingRepo extends JpaRepository<LoanBooking, Long> {
    List<LoanBooking> findByAcid(String acid);

    @Query(value = "select * from loan_booking where date(booked_on) =:bookedOn", nativeQuery = true)
    List<LoanBooking> getBookedInterestByDate(LocalDate bookedOn);

    @Query(value = "select count(*) from loan_booking where date(booked_on)=:bookedOn", nativeQuery = true)
    Integer getCountBookedLoansOnACertainDate(LocalDate bookedOn);

    @Query(value = "select a.id,a.acid from loan l JOIN accounts a ON l.account_id_fk=a.id where l.loan_status='DISBURSED' and l.interest_calculation_start_date<=:bookingDate and l.sn NOT IN (SELECT lb.loan_sn FROM loan_booking lb WHERE date(lb.booked_on)=:bookingDate)", nativeQuery = true)
    List<AccountsIds> getAllAcidsNotBookedOnACertainDate(LocalDate bookingDate);
    interface AccountsIds{
        Long getId();
        String getAcid();
    }

    @Query(value = "SELECT COALESCE(SUM(`amount_booked`),0) FROM loan_booking WHERE `interest_type`=:interestType AND `acid`=:acid", nativeQuery = true)
    Double getSumBookedInterestByInterestType(String interestType, String acid);
}
