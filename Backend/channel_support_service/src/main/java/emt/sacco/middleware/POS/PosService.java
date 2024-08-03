package emt.sacco.middleware.POS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import emt.sacco.middleware.ATM.Dto.AtmDto;
import emt.sacco.middleware.ATM.Dto.AtmGlResponse;
import emt.sacco.middleware.Utils.Config.UserRequestContext;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.EntityResponse;
import emt.sacco.middleware.Utils.RestService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class PosService {
        private final RestService restService;
        private final PosDto posDto;


        @Autowired
    public PosService(RestService restService, PosDto posDto) {
        this.restService = restService;
        this.posDto = posDto;
    }

    ObjectMapper objectMapper = new ObjectMapper();


    public List<AccountLookupDto> getAll (String entityId) {
        try {
            log.info("getPos Payload Received - " + entityId);
            String response = restService.getAllPos(entityId);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode entityNode = jsonResponse.get("entity");
            if (entityNode != null && entityNode.isArray()) {
                List<AccountLookupDto> pos = objectMapper.convertValue(entityNode, new TypeReference<List<AccountLookupDto>>() {
                });
                return pos;
            } else {
                return Collections.emptyList();
            }
        } catch (IOException e) {
            log.error("Error parsing JSON response: {}", e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error occurred while retrieving pos : {}", e.getMessage());
            return Collections.emptyList();
        }
    }


    public Map<String, Object> getById (String entityId, Long id) {
        try {
            log.info("getPos Payload Received - " + entityId);
            String response = restService.getPosById(entityId, id);
            JsonNode jsonResponse = objectMapper.readTree(response);
            Map<String, Object> responseMap = objectMapper.convertValue(jsonResponse, new TypeReference<Map<String, Object>>() {});

            // If the response contains the 'entity' field, return the entire response
            if (responseMap.containsKey("entity")) {
                return responseMap;
            } else {
                // If the response does not contain the 'entity' field, return an empty map indicating failure
                return Collections.emptyMap();
            }
        } catch (IOException e) {
            log.error("Error parsing JSON response for Pos verification: {}", e.getMessage());
            return Collections.emptyMap();
        } catch (Exception e) {
            log.error("Error occurred during Pos verification: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }


    public List<AccountLookupDto> findAllUnverifiedPos (String entityId) {
        try {
            log.info("unverified pos Payload Received - " + entityId);
            String response = restService.unverifiedPos(entityId);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode entityNode = jsonResponse.get("entity");
            if (entityNode != null && entityNode.isArray()) {
                List<AccountLookupDto> pos = objectMapper.convertValue(entityNode, new TypeReference<List<AccountLookupDto>>() {
                });
                return pos;
            } else {
                return Collections.emptyList();
            }
        } catch (IOException e) {
            log.error("Error parsing JSON response: {}", e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error occurred while retrieving pos : {}", e.getMessage());
            return Collections.emptyList();
        }
    }


    public Map<String, Object> modify(PosDto posDto) {
        String entityId=EntityRequestContext.getCurrentEntityId();
        Map<String, Object> responseMap = new HashMap<>();

        try {
            //String entityId = posDto.getEntityId();
            Gson gson = new Gson();
            log.info("Modify Pos Payload Received - " + gson.toJson(posDto));
            String jsonPayload = gson.toJson(posDto);

            String response = restService.modifyPos(jsonPayload, entityId);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode jsonResponse = objectMapper.readTree(response);

            String message = jsonResponse.path("message").asText();  // Extracting "message" field
            int statusCode = jsonResponse.path("statusCode").asInt(); // Extracting "statusCode" field

            log.info("Received message: {}, StatusCode: {}", message, statusCode);

            responseMap.put("message", message);
            responseMap.put("statusCode", statusCode);

        } catch (IOException e) {
            log.error("Error processing modify Pos request or response: {}", e.getMessage());
            responseMap.put("error", "Error processing request or response");
        }

        return responseMap;
    }


//    public Map<String, Object> verify(String entityId, Long id) {
//        try {
//            // Call the backend service to verify POS details
//            String response = restService.verifyPos(entityId, id);
//
//            // Parse the JSON response and construct the response map
//            JsonNode jsonResponse = objectMapper.readTree(response);
//            Map<String, Object> responseMap = objectMapper.convertValue(jsonResponse, new TypeReference<Map<String, Object>>() {});
//
//            // If the response contains the 'entity' field, return the entire response
//            if (responseMap.containsKey("entity")) {
//                return responseMap;
//            } else {
//                // If the response does not contain the 'entity' field, return an empty map indicating failure
//                return Collections.emptyMap();
//            }
//        } catch (IOException e) {
//            log.error("Error parsing JSON response for Pos verification: {}", e.getMessage());
//            return Collections.emptyMap();
//        } catch (Exception e) {
//            log.error("Error occurred during Pos verification: {}", e.getMessage());
//            return Collections.emptyMap();
//        }
//    }


//    public String deleteByTerminalId(String terminalId) {
//        String response;
//        try {
//            response = restService.deletePos(terminalId);
//            JsonNode jsonResponse = objectMapper.readTree(response);
//            String getCustomerCode = String.valueOf(jsonResponse.get("statusCode"));
//            JsonNode message = jsonResponse.get("message");
//            int customerStatusCode = Integer.parseInt(getCustomerCode);
//
//            if (customerStatusCode == HttpStatus.NOT_FOUND.value()) {
//                log.info("No Pos records found for Terminal ID:" + terminalId);
//                String failMessage = "No Pos records found for Terminal ID:" + terminalId;
//                return failMessage;
//            } else {
//                return "message " + message;
//            }
//        } catch (Exception e) {
//            log.error("Error occurred while deleting Pos: {}", e.getMessage());
//            return "An error occurred while processing the request.";
//        }
//    }

    public Map<String, Object> deleteByTerminalId(String terminalId) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            String response = restService.deletePos(terminalId);
            JsonNode jsonResponse = objectMapper.readTree(response);
            String getCustomerCode = String.valueOf(jsonResponse.get("statusCode"));
            JsonNode message = jsonResponse.get("message");
            int statusCode = jsonResponse.path("statusCode").asInt();
            int customerStatusCode = Integer.parseInt(getCustomerCode);

            if (customerStatusCode == HttpStatus.NOT_FOUND.value()) {
                log.info("No Pos records found for Terminal ID:" + terminalId);
                String failMessage = "No Pos records found for Terminal ID:" + terminalId;
                responseMap.put("message", failMessage);
            } else {
                responseMap.put("statusCode", statusCode);
                responseMap.put("message", message);
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting Pos: {}", e.getMessage());
            responseMap.put("error", "An error occurred while processing the request.");
        }
        return responseMap;
    }

    public ResponseEntity<?> addPos(PosDto posDto, String entityid) {
        EntityResponse res = new EntityResponse();
        try {
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(posDto);
            log.info("Add Payload Received - " + jsonPayload);
//            String entityId = posDto.getEntityId();
            JsonNode response = restService.addPos(jsonPayload, entityid);
            JsonNode entityNode = response.get("entity");
            if (entityNode != null) {
                String message = response.get("message").asText();
                int statusCode = response.get("statusCode").asInt();

                res.setMessage(message);
                res.setEntity(entityNode);
                res.setStatusCode(statusCode);
                return ResponseEntity.ok(res);
            } else {
                res.setMessage("AError occurred while adding pos ");
                res.setEntity("");
                res.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.ok(res);
            }
        } catch (Exception e) {
            log.error("Error occurred while adding pos : {}", e.getMessage());
            res.setMessage("Error occurred while adding pos ");
            res.setEntity("");
            res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.ok(res);
        }
    }


    public ResponseEntity<List<AccountLookupDto>> getMerchantAcid() {
        String entityId = EntityRequestContext.getCurrentEntityId();
        String userName = UserRequestContext.getCurrentUser();

        EntityResponse<List<AccountLookupDto>> response = new EntityResponse<>();
        try {
            String fetchedAccounts = restService.findMerchantAcid(userName, entityId);
            log.info("Receiving list of accounts from the remote server", fetchedAccounts);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            try {
                JsonNode jsonNode = objectMapper.readTree(fetchedAccounts);
                String message = jsonNode.path("message").asText();
                JsonNode entityNode = jsonNode.path("entity");
                if ("Records Found".equals(message)) {
                    List<AccountLookupDto> accountsFound = convertToAccounts(entityNode);

                  //  response.setMessage("Records Found");
                  //  response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(accountsFound);

                    String atmAccount = extractAccountInfo(entityNode);


                    // Now you can pass the ATM account information to the atmOnboarding method
                    return ResponseEntity.ok().body(response.getEntity());
                } else {
                    // Handle the case when records are not found
                    return ResponseEntity.ok(response.getEntity());
                    // return ResponseEntity.status(HttpStatus.NOT_FOUND)
                          //  .body(new EntityResponse<>());
                }
            } catch (IOException e) {
                response.setMessage("An error occurred while fetching accounts from the remote server");
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseEntity.ok(response.getEntity());

                //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                 //       .body(new EntityResponse<>());
            } catch (Exception e) {
                response.setMessage("An error occurred while fetching accounts from the remote server");
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseEntity.ok(response.getEntity());

                //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                //        .body(new EntityResponse<>());
            }
        } finally {
        }
    }

    @NotNull
    private List<AccountLookupDto> convertToAccounts (JsonNode entityNode) {
        String currentEntityId = EntityRequestContext.getCurrentEntityId();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<AccountLookupDto> result = new ArrayList<>();
        if (entityNode.isArray()) {
            for (JsonNode accountNode : entityNode) {
                if (accountNode.has("accountType") && accountNode.path("accountType").isTextual()) {
                    String accountType = accountNode.path("accountType").asText();
                    String entityId = accountNode.path("entityId").asText();
                    if (accountType.contains("SBA") && entityId.equals(currentEntityId)) {
                        AccountLookupDto accountLookupDto = new AccountLookupDto();
                        accountLookupDto.setAcid(accountNode.path("acid").asText());
                        accountLookupDto.setAccountName(accountNode.path("accountName").asText());
                        accountLookupDto.setAccountStatus(accountNode.path("accountStatus").asText());
                        accountLookupDto.setSolCode(accountNode.path("solCode").asText());
                        accountLookupDto.setVerifiedFlag(accountNode.path("verifiedFlag").asText());
                        result.add(accountLookupDto);
                    }
                }
            }
        } else {
            if (entityNode.has("accountType") && entityNode.path("accountType").isTextual()) {
                String accountType = entityNode.path("accountType").asText();
                if (accountType.contains("SBA")) {
                    AccountLookupDto accountLookupDto = new AccountLookupDto();
                    accountLookupDto.setAcid(entityNode.path("acid").asText());
                    accountLookupDto.setAccountName(entityNode.path("accountName").asText());
                    result.add(accountLookupDto);
                }
            }
        }
        return result;
    }


    private String extractAccountInfo(JsonNode entityNode) {
        // Replace "atm_account" with the actual key in your JSON response
        return entityNode.path("acid").asText();
    }    @Transactional

    public List<Object[]> posGetAllTransactions(String entity_id) {
        try {
            String response = restService.findPosTransactions(entity_id);
            log.info("Response from core banking system: {}", response); // Log the response

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});

            // Extract the list of POS transaction objects from the response
            List<Map<String, Object>> posTransactionsMap = (List<Map<String, Object>>) responseMap.get("entity");

            // Transform the list of maps into a list of arrays
            List<Object[]> posTransactions = new ArrayList<>();
            for (Map<String, Object> transactionMap : posTransactionsMap) {
                Object[] transactionArray = new Object[7]; // Assuming there are 7 fields in each transaction
                transactionArray[0] = transactionMap.get("merchantName");
                transactionArray[1] = transactionMap.get("merchantAcid");
                transactionArray[2] = transactionMap.get("merchantLocation");
                transactionArray[3] = transactionMap.get("transactionCode");
                transactionArray[4] = transactionMap.get("totalAmount");
                transactionArray[5] = transactionMap.get("postedTime");
                posTransactions.add(transactionArray);
            }

            log.info("Transformed POS transactions: {}", posTransactions); // Log the transformed data

            return posTransactions;
        } catch (IOException e) {
            // Handle JSON parsing exception
            log.error("Error occurred while parsing POS transactions data", e);
            throw new RuntimeException("Error occurred while parsing POS transactions data", e);
        }
    }

    public Map<String, Object> verify(String entityId, Long id) {
        try {
            String response = restService.verifyPos(entityId, id);

            JsonNode jsonResponse = objectMapper.readTree(response);
            Map<String, Object> responseMap = objectMapper.convertValue(jsonResponse, new TypeReference<Map<String, Object>>() {});

            String message = jsonResponse.path("message").asText();
            int statusCode = jsonResponse.path("statusCode").asInt();

            log.info("Received message: {}, StatusCode: {}", message, statusCode);

            responseMap.put("message", message);
            responseMap.put("statusCode", statusCode);

            if (responseMap.containsKey("entity")) {
                return responseMap;
            } else {
                // If the response does not contain the 'entity' field, return an empty map indicating failure
                return Collections.emptyMap();
            }
        } catch (IOException e) {
            log.error("Error parsing JSON response for Pos verification: {}", e.getMessage());
            return Collections.emptyMap();
        } catch (Exception e) {
            log.error("Error occurred during Pos verification: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }


}





