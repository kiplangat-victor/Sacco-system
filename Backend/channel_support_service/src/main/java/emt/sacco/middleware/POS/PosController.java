package emt.sacco.middleware.POS;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import emt.sacco.middleware.ATM.Dto.AtmDto;
import emt.sacco.middleware.ATM.Dto.AtmGlResponse;
import emt.sacco.middleware.Utils.Config.UserRequestContext;
import emt.sacco.middleware.Utils.EntityRequestContext;
import emt.sacco.middleware.Utils.EntityResponse;
import emt.sacco.middleware.Utils.Responses.MessageResponse;
import emt.sacco.middleware.Utils.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pos")
@Slf4j
public class PosController {
    @Autowired
    private PosService posService;

    @Autowired
    private RestService restService;


    @GetMapping(path = "/get/all", produces = "application/json")
    public ResponseEntity<?> getAllPos(@RequestHeader String entityId) {
        if (entityId != null) {
            try {
                return ResponseEntity.ok(posService.getAll(entityId));
            } catch (Exception e) {
                log.error("Error occurred while retrieving pos: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while retrieving pos");
            }
        } else {
            log.error("Invalid Payload: {}", entityId);
            return ResponseEntity.badRequest().body("Invalid Payload");
        }
    }

    @GetMapping(path = "/get/by/{id}", produces = "application/json")
    public ResponseEntity<?> getById(@RequestHeader String entityId, @RequestParam Long id) {
        if (entityId != null) {
            try {
                return ResponseEntity.ok(posService.getById(entityId, id));
            } catch (Exception e) {
                log.error("Error occurred while retrieving pos: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while retrieving pos");
            }
        } else {
            log.error("Invalid Payload: {}", entityId);
            return ResponseEntity.badRequest().body("Invalid Payload");
        }
    }

    @GetMapping(path = "find/all/unverified", produces = "application/json")
    public ResponseEntity<?> findAllUnverifiedPos(@RequestHeader String entityId) {
        if (entityId != null) {
            try {
                return ResponseEntity.ok(posService.findAllUnverifiedPos(entityId));
            } catch (Exception e) {
                log.error("Error occurred while retrieving pos: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while retrieving pos");
            }
        } else {
            log.error("Invalid Payload: {}", entityId);
            return ResponseEntity.badRequest().body("Invalid Payload");
        }
    }

    @PutMapping(path = "/modify", produces = "application/json")
    public ResponseEntity<?> modify(@RequestBody PosDto posDto) {
        log.info("Received ModifyPosPayload: {}", posDto);

        if (posDto != null) {
            try {

                return ResponseEntity.ok(posService.modify(posDto));
            } catch (Exception e) {
                String errorMessage = "400: " + e.getMessage();
                log.error("Error occurred while modifying Pos:", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"" + errorMessage + "\"}");
            }
        } else {
            // Handle the case where modifyAtmPayload is null
            log.error("Received null ModifyPosPayload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"ModifyPosPayload cannot be null\"}");
        }
    }


//    @PutMapping(path = "/verify/{id}", produces = "application/json")
//    public ResponseEntity<?> verify(@PathVariable Long id) {
//        String entityId = EntityRequestContext.getCurrentEntityId();
//        log.info("Verifying Pos details for ID: {} and Entity ID: {}", id, entityId);
//
//        try {
//            // Call the service method passing both the entityId and id
//            Map<String, Object> responseMap = posService.verify(entityId, id);
//
//            // If the verification is successful, return the response with status 200
//            if (responseMap.containsKey("entity")) {
//                responseMap.put("message", "Pos details verified successfully");
//                responseMap.put("statusCode", 200);
//                return ResponseEntity.ok(responseMap);
//            } else {
//                // If the verification fails, return the response with status 400
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
//            }
//        } catch (Exception e) {
//            // If an exception occurs, return an internal server error response
//            String errorMessage = "500: " + e.getMessage();
//            log.error("Error occurred during verification:", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("{\"error\":\"" + errorMessage + "\"}");
//        }
//    }

    @DeleteMapping("/deleteByTerminalId/{terminalId}")
    public ResponseEntity<?>  delete(@PathVariable String terminalId) {
        try {
            return ResponseEntity.ok(posService.deleteByTerminalId(terminalId));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @PostMapping("/add")
    public ResponseEntity<?> addPos(@RequestBody PosDto posDto) {
        String entityid = EntityRequestContext.getCurrentEntityId();
        try {
            if (entityid != null) {
                return posService.addPos(posDto, entityid);
            } else {
                log.error("Invalid Payload: {}", entityid);
                return ResponseEntity.badRequest().body(new MessageResponse<>(null, "Invalid addPayload", 400));
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse<>(null, "Error occurred while adding an pos machine", 500));
        }
    }


    @GetMapping("/get/merchant/acid")
    public ResponseEntity<?> getMerchantAcid() {
        try {
            return ResponseEntity.ok(posService.getMerchantAcid());
        } catch (Exception e) {
            log.error("Error occurred while retrieving merchant account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while retrieving merchant account:");
        }
    }


    @GetMapping(path ="/postransactions", produces="application/json")
    public EntityResponse<?> getAllPosTransactions() {
        try {
            log.info("Received request to get all POS transactions");

            String entityId = EntityRequestContext.getCurrentEntityId();
            List<Object[]> posTransactions = posService.posGetAllTransactions(entityId);

            EntityResponse<List<PosTransactionDto>> response = new EntityResponse<>();

            if (posTransactions == null || posTransactions.isEmpty()) {
                response.setMessage("No Records Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }

            List<PosTransactionDto> posTransactionDtoList = posTransactions.stream()
                    .map(this::transformToPosTransactionDto)
                    .collect(Collectors.toList());

            response.setMessage("Records Found");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(posTransactionDtoList);

            log.info("Successfully retrieved all POS transactions");

            return response;
        } catch (Exception e) {
            log.error("Error occurred while fetching POS transactions", e);
            log.error("Error details: {}", e.getMessage());
            EntityResponse response = new EntityResponse();
            response.setMessage("An error occurred while fetching transactions.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return response;
        }
    }
private PosTransactionDto transformToPosTransactionDto(Object[] transaction) {
    PosTransactionDto posTransactionDto = new PosTransactionDto();
    posTransactionDto.setMerchantName((String) transaction[0]);
    posTransactionDto.setMerchantAcid((String) transaction[1]);
    posTransactionDto.setMerchantLocation((String) transaction[2]);
    posTransactionDto.setTransactionCode((String) transaction[3]);
    posTransactionDto.setTotalAmount((Double) transaction[4]);

    Object dateObject = transaction[5];
    if (dateObject != null) {
        String dateString = dateObject.toString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = formatter.parse(dateString);
            posTransactionDto.setPostedTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
     
    }
  return posTransactionDto;
}


    @PutMapping(path = "/verify/{id}", produces = "application/json")
    public ResponseEntity<?> verify(@PathVariable Long id) {
        try {
            String entityId = EntityRequestContext.getCurrentEntityId();
            return ResponseEntity.ok(posService.verify(entityId, id));
        }
        catch (Exception e) {
            String errorMessage = "400: " + e.getMessage();
            log.error("Error occurred during verification:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + errorMessage + "\"}");
        }
    }


}