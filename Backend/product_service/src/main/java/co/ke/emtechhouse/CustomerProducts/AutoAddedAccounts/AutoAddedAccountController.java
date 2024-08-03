package co.ke.emtechhouse.CustomerProducts.AutoAddedAccounts;

import co.ke.emtechhouse.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/auto-added-accounts")
public class AutoAddedAccountController {

    @Autowired
    private AutoAddedAccountService autoAddedAccountService;


    @GetMapping("find/by/product-code/{productCode}")
    public ResponseEntity<EntityResponse<?>> findByProductCode(@PathVariable("productCode") String productCode){
        try {
            return ResponseEntity.ok().body(autoAddedAccountService.findByProduct(productCode));
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
