package emt.sacco.middleware.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import emt.sacco.middleware.ATM.Dto.TranRequest;
import emt.sacco.middleware.Iso8583Proxy.FundTransfer.FundsTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@SpringBootApplication
@RestController
@Slf4j
public class RequestChecker {
    @Autowired
    private FundsTransferService fundsTransferService;

    public ResponseEntity<TranRequest> processRequest(String request) {
        TranRequest response = new TranRequest();
        if (isJson(request)) {
            try {
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(request).getAsJsonObject();
                String acid = jsonObject.get("acid").getAsString();
                double amount = jsonObject.get("amount").getAsDouble();
                response.setAcid(acid);
                response.setAmount(String.valueOf(amount));
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else if (isXml(request)) {


        } else if (isIso8583(request)) {
            try {
               EntityResponse<TranRequest> isoResponse = fundsTransferService.fundsTransfers(request);
                    return ResponseEntity.ok(isoResponse.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
                   return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(response);
    }
    private boolean isJson(String request) {
        return request.startsWith("{") && request.endsWith("}");
    }
    public boolean isXml(String request) {
        String xmlPattern = "<\\s*\\S+\\s*[^>]*>(.*?)<\\/\\s*\\S+\\s*>";
        // Check if the request matches the XML pattern
        return Pattern.compile(xmlPattern, Pattern.DOTALL).matcher(request).find();
    }
    private boolean isIso8583(String request) {
        int mtiLength = 4;
        // Minimum length required for an ISO8583 message (MTI + other fields)
        int minLength = mtiLength + 1; // Assuming at least one character after MTI
        if (request.length() < minLength) {
            // ISO8583 message must be at least mtiLength + 1 characters long
            return false;
        }
        // Extract the first mtiLength characters from the request
        String mtiCandidate = request.substring(0, mtiLength);

        // Check if the extracted substring contains only digits (indicating MTI)
        if (!mtiCandidate.matches("\\d+")) {
            // If the substring contains non-digit characters, it's not an ISO8583 message
            return false;
        }
        return true;
    }
}
