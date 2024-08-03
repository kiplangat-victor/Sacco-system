package emt.sacco.middleware.ATM.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import emt.sacco.middleware.ATM.Dto.AtmDto;
import emt.sacco.middleware.ATM.Dto.AtmGlResponse;
import emt.sacco.middleware.ATM.Model.AtmPayload;
//import emt.sacco.middleware.ATM.Model.GetAtmsPayload;
import emt.sacco.middleware.Utils.Config.UserRequestContext;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.EntityResponse;
import emt.sacco.middleware.Utils.Responses.MessageResponse;
import emt.sacco.middleware.Utils.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class AtmOnboardingService {
    private final RestService restService;
//    private final GetAtmsPayload getAtmsPayload;

    public AtmOnboardingService(RestService restService) {
        this.restService = restService;
        //this.getAtmsPayload = getAtmsPayload;
    }


    public ResponseEntity<?> getAtms(String entityId) {
        EntityResponse res = new EntityResponse();
        String response = restService.getAtms(entityId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode entityNode = jsonNode.path("entity");
            String message = jsonNode.path("message").asText();
            if ("Records Found".equals(message)) {
                res.setMessage("Records Found");
                res.setStatusCode(HttpStatus.OK.value());
                res.setEntity(entityNode);
                return ResponseEntity.ok(res);
            } else {
                res.setMessage("Records not found.");
                res.setEntity(null);
                res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(res);
            }
        } catch (IOException e) {
            res.setMessage("An error occurred while processing the request.");
            res.setEntity(null);
            res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
    public Map<String, Object> modifyAtm(AtmPayload modifyAtmPayload) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            String entityId = EntityRequestContext.getCurrentEntityId();
            String userName = UserRequestContext.getCurrentUser();
            Gson gson = new Gson();
            log.info("ModifyAtm Payload Received - " + gson.toJson(modifyAtmPayload));
            String jsonPayload = gson.toJson(modifyAtmPayload);

            String response = restService.modifyAtms(jsonPayload, entityId);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode jsonResponse = objectMapper.readTree(response);

            String message = jsonResponse.path("message").asText();  // Extracting "message" field
            int statusCode = jsonResponse.path("statusCode").asInt(); // Extracting "statusCode" field

            log.info("Received message: {}, StatusCode: {}", message, statusCode);

            responseMap.put("message", message);
            responseMap.put("statusCode", statusCode);

        } catch (IOException e) {
            log.error("Error processing modifyAtm request or response: {}", e.getMessage());
            responseMap.put("error", "Error processing request or response");
        }

        return responseMap;
    }

    public MessageResponse<JsonNode> verifyAtm(Long id) {
        try {
            String entityId = EntityRequestContext.getCurrentEntityId();
            var response = restService.verifyAtm(entityId, id);
            JsonNode entityNode = response.get("entity");
            if (entityNode != null) {
                String message = response.get("message").asText();
                int statusCode = response.get("statusCode").asInt();
                return new MessageResponse<>(message, statusCode);
            } else {
                return new MessageResponse<>(null, "", 400); // Return null and default values
            }
        } catch (Exception e) {
            log.error("Error occurred while adding atms : {}", e.getMessage());
            return new MessageResponse<>(null, "Error occurred while verifying an Atm", 500);
        }
    }


    public MessageResponse<JsonNode> deleteAtmMachineById(Long Id) {
        try {
            String entityId = EntityRequestContext.getCurrentEntityId();
            var response = restService.deleteAtmMachineById(Id, entityId);
            JsonNode entityNode = response.get("entity");
            if (entityNode != null) {
                String message = response.get("message").asText();
                int statusCode = response.get("statusCode").asInt();
                return new MessageResponse<>(message, statusCode);
            } else {
                return new MessageResponse<>(null, "", -1); // Return null and default values
            }
        } catch (Exception e) {
            log.error("Error occurred while adding atms : {}", e.getMessage());
            return new MessageResponse<>(null, "Error occurred while deleting an Atm!", -1);
        }
    }



    public ResponseEntity<?> addAtm(AtmPayload atmPayload, String entityid) {
        EntityResponse res = new EntityResponse();
        try {

            Gson gson = new Gson();
            log.info("AddAtm Payload Received - " + gson.toJson(atmPayload));
            String jsonPayload = gson.toJson(atmPayload);
            var response = restService.addAtm(jsonPayload, entityid);
            JsonNode entityNode = response.get("entity");
            if (entityNode != null) {
                String message = response.get("message").asText();
                int statusCode = response.get("statusCode").asInt();

                res.setMessage(message);
                res.setEntity("");
                res.setStatusCode(statusCode);
                return ResponseEntity.ok(res);
            } else {
                res.setMessage("AError occurred while adding atms ");
                res.setEntity("");
                res.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.ok(res);
            }
        } catch (Exception e) {
            log.error("Error occurred while adding atms : {}", e.getMessage());
            res.setMessage("Error occurred while adding atms ");
            res.setEntity("");
            res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.ok(res);
        }
    }

}




