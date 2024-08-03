package co.ke.emtechhouse.CustomerProducts.AutoAddedAccounts;

import co.ke.emtechhouse.CustomerProducts.Product;
import co.ke.emtechhouse.CustomerProducts.ProductRepo;
import co.ke.emtechhouse.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AutoAddedAccountService {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private AutoAddedAccountRepo autoAddedAccountRepo;

    public EntityResponse findByProduct(String productCode){
        EntityResponse res = new EntityResponse<>();
        try {
            Optional<Product> product= productRepo.findByProductCodeAndDeletedFlag(productCode, 'N');
            if(product.isPresent()){
                List<AutoAddedAccount> l =autoAddedAccountRepo.findByProduct(product.get());
                if(l.size()>0){
                    res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    res.setStatusCode(HttpStatus.FOUND.value());
                    res.setEntity(l);
                }else {
                    res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }else {
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("Product not available");
            }
            return res;
        }catch (Exception e){
            log.info("*********************error");
            log.info("Caught Error {}"+e.getMessage());

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return response;
        }
    }
}
