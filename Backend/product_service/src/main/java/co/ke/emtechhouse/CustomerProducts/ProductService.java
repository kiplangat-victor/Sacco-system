package co.ke.emtechhouse.CustomerProducts;

import co.ke.emtechhouse.CustomerProducts.Dtos.LookupDto;
import co.ke.emtechhouse.CustomerProducts.Dtos.ProdCodeDto;
import co.ke.emtechhouse.CustomerProducts.Dtos.ProductEventDto;
import co.ke.emtechhouse.CustomerProducts.Product_specific_details.TDA_Details.TDA_Details;
import co.ke.emtechhouse.CustomerProducts.product_gls.Productgls;
import co.ke.emtechhouse.DTO.IncomingChargeCollectionReq;
import co.ke.emtechhouse.Utils.CONSTANTS;
import co.ke.emtechhouse.Utils.Responses.EntityResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final ObjectMapper objectMapper;

    public ProductService(ProductRepo productRepo, ObjectMapper objectMapper) {
        this.productRepo = productRepo;
        this.objectMapper = objectMapper;
    }




    public Product add(Product product) {
        try {
            List<Productgls> productgls = product.getGlsubheads();
            List<Productgls> newGls = new ArrayList<>();
            for (int i = 0; i<productgls.size(); i++){
                Productgls productgls1 = productgls.get(i);
                if (i==0){
                    productgls1.setGl_subhead_deafault("Yes");
                }else {
                    productgls1.setGl_subhead_deafault("No");
                }
                newGls.add(productgls1);
            }
            product.setGlsubheads(newGls);
            return productRepo.save(product);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }

    }

    public Product findById(Long id) {
        try {
            return productRepo.findById(id).orElseThrow(() -> new RuntimeException("Data " + id + "was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getWithdrawalCharge(IncomingChargeCollectionReq incomingChargeCollectionReq) {
        try {
//                Optional<Product> findByProductCodeAndDeletedFlag(String productCode, Character flag);
            EntityResponse response = new EntityResponse();
            List<IncomingChargeCollectionReq> incomingChargeCollectionReqs = new ArrayList<>();
            Optional<Product> product = productRepo.findByProductCodeAndDeletedFlag(incomingChargeCollectionReq.getProductCode(), 'N');
            if (product.isPresent()) {

            } else {
                response.setMessage("Product with code " + incomingChargeCollectionReq.getProductCode() + " not found");
                response.setStatusCode(HttpStatus.NO_CONTENT.value());
            }

//            private String debitAc;
//            private Double transactionAmount;
//            private String chargeCode;
//            private String transParticulars;

            return response;
//            return productRepo.findById(id).orElseThrow(()-> new RuntimeException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public Product update(Product product) {
        try {
            System.out.println("Got called");
            System.out.println(product);
            return productRepo.save(product);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void delete(Long id) {
        try {
            productRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public EntityResponse getProductLedgerEvents(List<ProdCodeDto> prodCode){
        try{
            EntityResponse res = new EntityResponse<>();
            List<ProductEventDto> prod= new ArrayList<>();

            prodCode.forEach(pc->{
                Optional<ProductEventDto> prodEvent= productRepo.findProductLedgerDetails(pc.getProdCode());
                if(prodEvent.isPresent()){
                    ProductEventDto ped=prodEvent.get();
                    prod.add(prodEvent.get());
                }
            });

            if(prod.size()>0){
                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.FOUND.value());
                res.setEntity(prod);
            }else {
                res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return res;
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public EntityResponse productLookup(String productType, String productCode) {
        try {
            EntityResponse res = new EntityResponse<>();
            res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            res.setStatusCode(HttpStatus.NOT_FOUND.value());

            if (productCode != null) {
                if (!productCode.trim().isEmpty()) {
                    List<LookupDto> lookupDtoList = productRepo.findProdDtoByProductCode(productType);
                    if (lookupDtoList.size() > 0) {
                        res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                        res.setStatusCode(HttpStatus.FOUND.value());
                        res.setEntity(lookupDtoList);
                    } else {
                        res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        res.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                } else {
                    if (productType != null) {
                        if (!productType.trim().isEmpty()) {
                            List<LookupDto> lookupDtoList = productRepo.findProdDtoByProductType(productType);
                            if (lookupDtoList.size() > 0) {
                                res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                                res.setStatusCode(HttpStatus.FOUND.value());
                                res.setEntity(lookupDtoList);
                            } else {
                                res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                            }
                        } else {
                            res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                            res.setStatusCode(HttpStatus.NOT_FOUND.value());
                        }
                    }
                }
            } else {
                if (productType != null) {
                    if (!productType.trim().isEmpty()) {
                        List<LookupDto> lookupDtoList = productRepo.findProdDtoByProductType(productType);
                        if (lookupDtoList.size() > 0) {
                            res.setMessage(HttpStatus.FOUND.getReasonPhrase());
                            res.setStatusCode(HttpStatus.FOUND.value());
                            res.setEntity(lookupDtoList);
                        } else {
                            res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                            res.setStatusCode(HttpStatus.NOT_FOUND.value());
                        }
                    } else {
                        res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        res.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                } else {
                    res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return res;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


}

