package com.emtechhouse.accounts.TransactionService.EODProcess.ManageUsers;

import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("api/v1/eodManageUsers")
public class ManageUsersController {
    @Autowired
    private ManageUsersService manageUsersService;

    EntityResponse response = new EntityResponse<>();

    //Enable User
    @GetMapping("/enableAccount")
    public ResponseEntity<?> enableUserAccountInEOD(@RequestParam("username") String userName)
    {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response,HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response,HttpStatus.OK);
                } else {
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(manageUsersService.enableUserAccountInEOD(userName));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR WHEN ENABLING USER ACCOUNT DURING EOD PROCESS :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }


    //Enable All Accounts During EOD
    @GetMapping("/enableAllAccounts")
    public ResponseEntity<?> enableAllUserAccountsInEOD()
    {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response,HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response,HttpStatus.OK);
                } else {
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    String entityId = EntityRequestContext.getCurrentEntityId();
                    response.setEntity(manageUsersService.enableAllUserAccountsDuringEOD(entityId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR WHEN ENABLING USER ACCOUNTS DURING EOD PROCESS :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    //Disable User
    @GetMapping("/disableAccount")
    public ResponseEntity<?> disableOneUserAccountInEOD(@RequestParam("username") String userName)
    {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response,HttpStatus.OK);
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response,HttpStatus.OK);
                } else {
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(manageUsersService.disableUserAccountDuringEOD(userName));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR WHEN DISBLING USER ACCOUNT DURING EOD PROCESS :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }
}
