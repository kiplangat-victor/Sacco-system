package com.emtechhouse.System.SystemParameters;

import com.emtechhouse.System.Utils.Responses.MessageResponse;
import com.emtechhouse.System.Utils.Responses.RESPONSEMESSAGES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("system")
//@CrossOrigin
public class SystemParametersResource {

    @Autowired
    private SystemParametersService systemParametersService;

    @GetMapping(path = "get")
    public ResponseEntity<?> retrieveParameters(){
        SystemParameters parameters = systemParametersService.findSystemParameters();
        if(Objects.isNull(parameters)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(RESPONSEMESSAGES.PARAMETERS_NOT_FOUND));
        }
        return ResponseEntity.ok().body(parameters);
    }

    @PostMapping("parameters/add")
    public ResponseEntity<?> addParameter(){
        return ResponseEntity.ok().body(new MessageResponse(RESPONSEMESSAGES.PARAMETERISATION_SUCCESSFUL));
    }
}
