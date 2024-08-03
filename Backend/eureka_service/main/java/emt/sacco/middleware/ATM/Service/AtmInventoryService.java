package emt.sacco.middleware.ATM.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import emt.sacco.middleware.ATM.Model.AtmInventory;
import emt.sacco.middleware.Utils.RestService;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;


@Service

public class AtmInventoryService {
    @Getter
    private final AtmInventory atmInventory;
    private final RestService restService;

    public AtmInventoryService(AtmInventory atmInventory, RestService restService) {
        this.atmInventory = atmInventory;
        this.restService = restService;
    }


    public String acceptRequest(AtmInventory atmInventory) {
        try {// Set request status and batch ID
            atmInventory.setRequestStatus(cardProcessing());
            atmInventory.setBatchId(UUID.randomUUID().toString());

            // Serialize the AtmInventory object to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(atmInventory);

            // Make the REST call with the JSON payload
            String response = restService.acceptRequest(requestBody);
            return response;
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle JSON processing exception
            return "Error occurred while processing the request";
        }
    }

    public String cardProcessing() {
        // Simulate card processing status
        return new Random().nextBoolean() ? "success" : "failed";
    }


    public String rejectRequest(AtmInventory atmInventory) {
        try {
            atmInventory.setRequestStatus(cardProcessing());
            atmInventory.setRequestStatus("Cards application Rejected");
            // Serialize the AtmInventory object to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(atmInventory);

            // Make the REST call with the JSON payload
            String response = restService.rejectRequest(requestBody);
            return response;

        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle JSON processing exception
            return "Error occurred while processing the request";
        }

    }

    public String disburseCards(AtmInventory atmInventory) {
        try {
            atmInventory.setRequestStatus("Cards disbursed successfully");
            // Serialize the AtmInventory object to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(atmInventory);

            // Make the REST call with the JSON payload
            String response = restService.disburseCards(requestBody);
            return response;
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle JSON processing exception
            return "Error occurred while processing the request";
        }

    }
}

