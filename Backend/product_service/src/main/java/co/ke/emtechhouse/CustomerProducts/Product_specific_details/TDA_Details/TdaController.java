package co.ke.emtechhouse.CustomerProducts.Product_specific_details.TDA_Details;

import co.ke.emtechhouse.Utils.Responses.EntityResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Api(value = "/product")
@RequestMapping("/api/v1/product/tda")
public class TdaController {

    @Autowired
    private TdaService tdaService;

    @GetMapping("get/tda/details")
    public ResponseEntity<?> getTdaDetails(@RequestParam String productCode) {
        try {
            EntityResponse res=tdaService.getTdaDetails(productCode);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
