package emt.sacco.middleware.ATM.ResetPin;

import com.fasterxml.jackson.databind.ObjectMapper;
import emt.sacco.middleware.Utils.RestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangePinService {
    private final RestService restService;

    public String resetPin(String cardNumber, String previousPin, String newPin, String confirmNewPin) {
        try{
            String response = restService.resetPin(cardNumber,previousPin,newPin,confirmNewPin);
            log.info("Reset PIN Response: {}", response);
          return response;

        }
        catch (Exception e) {
            log.error("Error occurred while resetting PIN: {}", e.getMessage());
            // Handle the error, you can throw an exception or return an error message
            throw new RuntimeException("Error occurred while resetting PIN");
        }
    }



}

