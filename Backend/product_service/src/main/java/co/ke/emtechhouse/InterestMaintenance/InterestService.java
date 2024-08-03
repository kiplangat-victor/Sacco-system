package co.ke.emtechhouse.InterestMaintenance;

import co.ke.emtechhouse.Utils.CONSTANTS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestService {
    @Autowired
    private InterestRepository interestRepository;

    public Interest saveInterest(Interest interest){
        return interestRepository.save(interest);
    }

    public Interest updateInterest(Interest interest){
        return interestRepository.save(interest);
    }

    public List<Interest> retrieveInterests(){
        return interestRepository.findByDeletedFlag(CONSTANTS.NO);
    }

    public Interest retrieveInterest(String interestCode){
        return interestRepository.findByDeletedFlagAndInterestCode(CONSTANTS.NO, interestCode).orElse(null);
    }

    public Interest recentVersion(String interestCode){
        return interestRepository.mostRecentVersion(interestCode).getInterest();
    }

}
