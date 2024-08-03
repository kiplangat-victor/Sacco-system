package co.ke.emtechhouse.AmountSlabs;

import co.ke.emtechhouse.Utils.CONSTANTS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmountSlabService {
    @Autowired
    private AmountSlabRepository amountSlabRepository;

    public List<AmountSlab> saveAmountSlabs(List<AmountSlab> amountSlabs){
        amountSlabs.forEach(amountSlab -> amountSlab.setVersion(1));
        return amountSlabRepository.saveAll(amountSlabs);
    }

    public List<AmountSlab> updateAmountSlabs(List<AmountSlab> amountSlabs){
        amountSlabs.forEach(amountSlab -> amountSlab.setVersion(amountSlab.getVersion()+1));
        return amountSlabRepository.saveAll(amountSlabs);
    }

    public List<AmountSlab> getAmountSlabs(String interestCode){
        return amountSlabRepository.findByDeletedFlagAndInterestCode(CONSTANTS.NO, interestCode);
    }
}
