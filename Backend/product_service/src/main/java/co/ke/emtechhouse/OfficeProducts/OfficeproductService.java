package co.ke.emtechhouse.OfficeProducts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OfficeproductService {
    private final OfficeproductRepo officeproductRepo;

    public OfficeproductService(OfficeproductRepo officeproductRepo) {
        this.officeproductRepo = officeproductRepo;
    }

    public Officeproduct add(Officeproduct officeproduct){
        try{
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
        return officeproductRepo.save(officeproduct);
    }
    public Officeproduct findById(Long id){
        try{
            return officeproductRepo.findById(id).orElseThrow(()-> new RuntimeException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }
    public Officeproduct update(Officeproduct product){
        try{
            return officeproductRepo.save(product);
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public void delete(Long id) {
        try {
            officeproductRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}
