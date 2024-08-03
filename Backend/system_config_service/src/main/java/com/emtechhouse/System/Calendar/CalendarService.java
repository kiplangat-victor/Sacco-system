package com.emtechhouse.System.Calendar;

import com.emtechhouse.System.Calendar.Requests.CalendarInquiry;
import com.emtechhouse.System.Utils.CONSTANTS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CalendarService {
    @Autowired private CalendarRepository repository;

    public Calendar saveCalendar(Calendar calendar){
        Calendar rCalendar = repository.findByYearAndMonthAndVerifiedFlag(calendar.getYear(), calendar.getMonth(), CONSTANTS.NO).orElse(null);
        if(Objects.isNull(rCalendar)){
            return repository.save(calendar);
        }
        return null;
    }

    public Calendar updateCalendar(Calendar calendar){
        return repository.save(calendar);
    }

    public Calendar retrieveCalendar(CalendarInquiry inquiry){
        return repository.findByYearAndMonthAndVerifiedFlag(inquiry.getYear(), inquiry.getMonth(), CONSTANTS.NO).orElse(null);
    }

}
