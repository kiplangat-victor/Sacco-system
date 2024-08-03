package com.emtechhouse.accounts.TransactionService.EODProcess.SystemCalendar;

import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin
@RequestMapping("api/v1/sysCalendar")
public class SystemCalendarController {
    @Autowired
    private SystemCalendarService service;

    EntityResponse response = new EntityResponse();

    //Add
    @PostMapping("/add")
    public ResponseEntity<?> addSystemDate(@RequestBody SystemCalendar calendar)
    {
        response = service.addNewSystemDate(calendar);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Modify
    @PutMapping("/update")
    public ResponseEntity<?> modifySystemDate(@RequestBody SystemCalendar calendar)
    {
        response = service.modifySystemDate(calendar);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Delete
    @PutMapping("/delete/{id}")
    public ResponseEntity<?> deleteSystemDate(@PathVariable("id") Long id)
    {
        response = service.deleteSystemDate(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Fetch All
    @GetMapping("/all")
    public ResponseEntity<?> fetchAllSystemDates()
    {
        response = service.getAllSystemDates();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Fetch By Id
    @GetMapping("/find/{id}")
    public ResponseEntity<?> fetchSystemDateById(@PathVariable("id") Long id)
    {
        response = service.getSystemDateById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Fetch By Entity Id
    @GetMapping("/findone")
    public ResponseEntity<?> fetchSystemDateById()
    {
        response = service.getSystemDateByEntityId();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
