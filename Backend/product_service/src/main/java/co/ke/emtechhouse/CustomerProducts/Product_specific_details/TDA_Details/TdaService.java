package co.ke.emtechhouse.CustomerProducts.Product_specific_details.TDA_Details;

import co.ke.emtechhouse.CustomerProducts.Product;
import co.ke.emtechhouse.CustomerProducts.ProductRepo;
import co.ke.emtechhouse.Utils.CONSTANTS;
import co.ke.emtechhouse.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TdaService {
    @Autowired
    private TDA_Repo tda_repo;
    @Autowired
    private ProductRepo productRepo;

    public EntityResponse getTdaDetails(String prodCode){
        try{
            EntityResponse res = new EntityResponse<>();
            Optional<Product> product= productRepo.findByProductCodeAndDeletedFlag(prodCode, CONSTANTS.NO);
            if(product.isPresent()){
                Product p= product.get();
                TDA_Details t= p.getTdaDetails();
                if(t != null){
                    res.setStatusCode(HttpStatus.FOUND.value());
                    res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    res.setEntity(t);
                }else {
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    res.setMessage("Could not find the TDA details");
                }
            }else {
                res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.setMessage("Could not find the product");
            }
            return res;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
}
