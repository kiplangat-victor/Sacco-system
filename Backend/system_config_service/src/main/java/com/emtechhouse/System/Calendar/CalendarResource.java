package com.emtechhouse.System.Calendar;

import com.emtechhouse.System.Calendar.Requests.CalendarInquiry;
import com.emtechhouse.System.Utils.EntityResponse;
import com.emtechhouse.System.Utils.Responses.RESPONSEMESSAGES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

//@CrossOrigin
@RequestMapping("calendar")
@RestController
public class CalendarResource {
    @Autowired
    private CalendarService service;

    @GetMapping("inquire")
    public ResponseEntity<EntityResponse<Calendar>> inquireCalendar(CalendarInquiry calendarInquiry){
        Calendar sCalendar = service.retrieveCalendar(calendarInquiry);
        if(Objects.isNull(sCalendar)){
            return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.RECORD_NOT_FOUND, null, HttpStatus.NOT_FOUND.value()));
        }
        return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.RECORDS_RETRIEVED_SUCCESSFULLY, sCalendar, HttpStatus.OK.value()));
    }

    @PostMapping("add")
    public ResponseEntity<EntityResponse<Calendar>> addCalendar(Calendar calendar){
        Calendar sCalendar = service.saveCalendar(calendar);
        return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.CALENDAR_SAVED, sCalendar, HttpStatus.CREATED.value()));
    }

    @PutMapping("update")
    public ResponseEntity<EntityResponse<Calendar>> updateCalendar(Calendar calendar){
        Calendar uCalendar = service.updateCalendar(calendar);
        return ResponseEntity.ok().body(new EntityResponse<>(RESPONSEMESSAGES.CALENDAR_UPDATED, uCalendar, HttpStatus.OK.value()));
    }
}
