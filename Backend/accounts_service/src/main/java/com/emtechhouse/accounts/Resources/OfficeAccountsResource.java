package com.emtechhouse.accounts.Resources;//package com.emtechhouse.accounts.Models.Guardians.Resources;
//
//import com.emtechhouse.accounts.Models.OfficeAccounts.OfficeAccount;
//import com.emtechhouse.accounts.Models.OfficeAccounts.OfficeAccountRegistrationService;
//import com.emtechhouse.accounts.Models.OfficeAccounts.OfficeAccountService;
//import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
////@CrossOrigin(origins = "*", maxAge = 3600)
//@Slf4j
//@RequestMapping("office")
//@RestController
//public class OfficeAccountsResource {
//    @Autowired
//    private OfficeAccountRegistrationService officeAccountRegistrationService;
//    @Autowired
//    private OfficeAccountService officeAccountService;
//
//
//    @PostMapping("open")
//    public ResponseEntity<?> openAccount(@RequestBody OfficeAccount officeAccount, HttpServletRequest request){
//        try{
//            return ResponseEntity.status(HttpStatus.CREATED).body(officeAccountRegistrationService.postOfficeAccount(officeAccount));
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @GetMapping("find/by/acid/{acid}")
//    public ResponseEntity<EntityResponse<OfficeAccount>> retrieveAccount(@PathVariable("acid") String acid){
//        try{
//            return ResponseEntity.status(HttpStatus.OK).body( officeAccountService.findByAcid(acid));
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @GetMapping("office/account/all")
//    public ResponseEntity<EntityResponse<List<OfficeAccount>>> allAccounts(){
//        try {
//            return ResponseEntity.ok().body(officeAccountService.listAllUndeletedAccounts());
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @PutMapping("/verify")
//    public ResponseEntity<EntityResponse<?>> verifyAccount(@RequestParam String Acid){
//        try{
//            return ResponseEntity.ok().body(officeAccountService.verifyCreatedAccount(Acid));
//        } catch (Exception e) {
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }
//}
