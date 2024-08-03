package emt.sacco.middleware.AgencyBanking.AgencyOnboarding.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import emt.sacco.middleware.AgencyBanking.AgencyOnboarding.Model.AgencyPayload;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.RestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AgentService {
    RestService restService;
    ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(AgentService.class);


    public ResponseEntity<String> addAgent(AgencyPayload agencyPayload,String entityId) {
        try {
//            String entityId = EntityRequestContext.getCurrentEntityId();
            String requestBody = objectMapper.writeValueAsString(agencyPayload);

            String response = restService.addAgent(requestBody, entityId);
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON", e);
            return ResponseEntity.badRequest().body("Error processing JSON");
        } catch (Exception e) {
            log.error("Error adding agent", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding agent: " + e.getMessage());
        }
    }

    private String getCurrentEntityId() {
        return EntityRequestContext.getCurrentEntityId();
    }



    public ResponseEntity<List<AgencyPayload>> getAllAgents(String entityId) {
        try {
            String response = restService.getAllAgents(entityId);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode entityNode = jsonResponse.get("entity");
            if (entityNode != null && entityNode.isArray()) {
                List<AgencyPayload> agencyList = objectMapper.convertValue(
                        entityNode, new TypeReference<List<AgencyPayload>>() {});
                return ResponseEntity.ok(agencyList);
            } else {
                return ResponseEntity.ok(List.of());
            }
        } catch (IOException e) {
            log.error("Error parsing JSON response: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        } catch (Exception e) {
            log.error("Error occurred while retrieving agents: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }




    public ResponseEntity<?> deleteAgent(String entityId,Long Id) {
        try {
           // String entityId = getCurrentEntityId();
            String response = restService.deleteAgent(Id, entityId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting agent", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting agent: " + e.getMessage());
        }
    }



    public ResponseEntity<?> getAllUnverifiedAgency(String entityId) {
        try {
            //String entityId = getCurrentEntityId();
            String response = restService.getAllUnverifiedAgencies(entityId);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode entityNode = jsonResponse.get("entity");
            if (entityNode != null && entityNode.isArray()) {
                List<AgencyPayload> unverifiedAgencies = objectMapper.convertValue(
                        entityNode, new TypeReference<List<AgencyPayload>>() {});
                return ResponseEntity.ok(unverifiedAgencies);
            } else {
                return ResponseEntity.ok(List.of());
            }
        } catch (IOException e) {
            logger.error("Error parsing JSON response for unverified agencies", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing JSON response for unverified agencies");
        } catch (Exception e) {
            logger.error("Error occurred while retrieving unverified agencies", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving unverified agencies: " + e.getMessage());
        }
    }

    public ResponseEntity<String> modifyAgency(AgencyPayload agencyPayload) {
        try {
            String entityId = getCurrentEntityId();
            String requestBody = objectMapper.writeValueAsString(agencyPayload);
            String response = restService.modifyAgency(requestBody,entityId);
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON");
        } catch (Exception e) {
            log.error("Error modifying agent", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error modifying agent: " + e.getMessage());
        }
    }



    public ResponseEntity<?> verify(Long Id) {
        try {
            String entityId = getCurrentEntityId();
            String response = restService.verifyAgent(Id, entityId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error verifying agent", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying agent: " + e.getMessage());
        }
    }
    //todo status
    public ResponseEntity<?> getAllAgentsStatus(String status) {
        try {
            //String entityId = getCurrentEntityId();
            String response = restService.getAllAgentsStatus(status);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode entityNode = jsonResponse.get("entity");
            if (entityNode != null && entityNode.isArray()) {
                List<AgencyPayload> unverifiedAgencies = objectMapper.convertValue(
                        entityNode, new TypeReference<List<AgencyPayload>>() {});
                return ResponseEntity.ok(unverifiedAgencies);
            } else {
                return ResponseEntity.ok(List.of());
            }
        } catch (IOException e) {
            logger.error("Error parsing JSON response for get all Agent Status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing JSON response for unverified agencies");
        } catch (Exception e) {
            logger.error("Error occurred while retrieving agent ststus", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving unverified agencies: " + e.getMessage());
        }
    }



}




