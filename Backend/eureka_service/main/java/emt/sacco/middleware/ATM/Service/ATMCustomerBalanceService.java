package emt.sacco.middleware.ATM.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import emt.sacco.middleware.ATM.Dto.AccountCustomerBalanceResponse;
import emt.sacco.middleware.Utils.RestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Slf4j
@RequiredArgsConstructor
public class ATMCustomerBalanceService {
    private final RestService restService;

    public AccountCustomerBalanceResponse getAccountBalance(String acid) {

        try {
            // Call the REST service to get the ATM balance information for the provided terminal ID and entity ID
            String response = restService.getAtmCustomerBalance(acid );

            // Parse the JSON response and map it to AtmBalanceResponse object
            ObjectMapper objectMapper = new ObjectMapper();
            AccountCustomerBalanceResponse accountBalanceResponse = objectMapper.readValue(response, AccountCustomerBalanceResponse.class);
            return accountBalanceResponse;
        } catch (IOException e) {
            log.error("Error parsing JSON response: {}", e.getMessage());
            throw new RuntimeException("Error parsing JSON response");
        } catch (Exception e) {
            log.error("Error occurred while retrieving ATM balance: {}", e.getMessage());
            throw new RuntimeException("Error occurred while retrieving ATM balance");
        }
    }
    }

