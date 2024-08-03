package emt.sacco.middleware.Utils;

import javax.net.ssl.*;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Utils {
    //Disable Verification (SSL)
    public void disableSSlVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (String hostname, SSLSession session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
        }
    }


    public static String generateTimestamp() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return currentDateTime.format(formatter);
    }

    public static String generateOriginatorConversationID() {
        String randomString = "T" + new BigInteger(64, new SecureRandom()).toString(16);
        return randomString.substring(0, 16);
    }
    public static int generateVmtReference(int len) {
        SecureRandom secureRandom = new SecureRandom();
        int randomWithSecureRandom = secureRandom.nextInt();
       int max = 30;
       int min = 15;
        int randomWithSecureRandomWithinARange = secureRandom.nextInt(max - min) + min;
        return randomWithSecureRandomWithinARange;
    }

    public static StringBuilder generateReceiptNumber(int len) {
        String chars = "F01K23U456P89T";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        return sb;
    }
    public static StringBuilder generateTransactionId(int len) {
        String chars = "P01X23U456B89T";
        Random rnd = new Random();
        StringBuilder trid = new StringBuilder(len);
        for (int i = 0; i < 10; i++)
            trid.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        return  trid;
    }


}