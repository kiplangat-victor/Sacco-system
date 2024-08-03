//package emt.sacco.middleware.ATM.ResetPin;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.regex.Pattern;
//
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/ATMpin")
//@RequiredArgsConstructor
//@Slf4j
//public class ResetAtmCardPinController {
//    private final ChangePinService ChangePinService;
//
//    @PostMapping("/reset-pin")
//    public ResponseEntity<String> resetPin(
////            @RequestParam String entityId,
//            @RequestParam String cardNumber,
//            @RequestParam String previousPin,
//            @RequestParam String newPin,
//            @RequestParam String confirmNewPin
//    ) {
//        // Check if new PIN and confirm new PIN match
//        if (!newPin.equals(confirmNewPin)) {
//            return ResponseEntity.badRequest().body("New PIN and confirm new PIN do not match.");
//        }
//
//        // Check if new PIN is different from the previous PIN
//        if (newPin.equals(previousPin)) {
//            return ResponseEntity.badRequest().body("New PIN should be different from the previous PIN.");
//        }
//
//        // Validate new PIN format
//        if (!isValidPinFormat(newPin)) {
//            return ResponseEntity.badRequest().body("New PIN should be 4-digit numeric.");
//        }
//
//        try {
//            // Delegate the reset pin logic to the service
//            String serviceResponse = ChangePinService.resetPin(cardNumber, previousPin, newPin, confirmNewPin);
//            return ResponseEntity.ok(serviceResponse);
//        } catch (PreviousPinIncorrectException e) {
//            return ResponseEntity.badRequest().body("Previous PIN is incorrect.");
//        } catch (CardNotRecognizedException e) {
//            return ResponseEntity.badRequest().body("ATM card not recognized.");
//        } catch (Exception e) {
//            log.error("Error during PIN reset", e);
//            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
//        }
//    }
//
//
//    private boolean isValidPinFormat(String pin) {
//        return Pattern.matches("\\d{4}", pin);
//    }
//}
//
