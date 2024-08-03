package emt.sacco.middleware.Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignatureGenerator {
    public String generateSignature(String username, String password, String originatorConversationID, String security_key) throws NoSuchAlgorithmException, InvalidKeyException {
        String hashedPassword = hashString(password, "SHA-256");
        String msg = username + hashedPassword + originatorConversationID;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(security_key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] hmac = mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hmac);
    }

    private String hashString(String value, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    String base64Encode(String userName, String password, String apiKey, String vmtMessageId) throws Exception {
        try{
            String msg = userName + ":" + apiKey + ":" + password + ":" + vmtMessageId;
            return java.util.Base64.getEncoder().encodeToString(msg.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e){
            throw new Exception("Error while encoding to base64:" + e.getMessage());
        }
    }
}
