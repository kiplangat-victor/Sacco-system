package com.emtechhouse.usersservice.utils;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Service
public class ImageToBase64 {

//    public static void main(String[] args) {
//        String imagePath = "logo.png"; // Path to your image in the resources folder
//        String base64Data = convertImageToBase64(imagePath);
//
//        // Now you can store 'base64Data' in your database
//        System.out.println("Base64 Data: " + base64Data);
//    }

    public String convertImageToBase64(String imagePath) {
        try {
            // Use ClassLoader to load the image from the resources folder
            InputStream inputStream = ImageToBase64.class.getClassLoader().getResourceAsStream(imagePath);
            if (inputStream != null) {
                // Read the image content into a byte array
                byte[] imageBytes = inputStream.readAllBytes();
                // Convert the byte array to Base64
                String base64Data = Base64.getEncoder().encodeToString(imageBytes);
                // Close the InputStream
                inputStream.close();
                return base64Data;
            } else {
                System.out.println("Image not found: " + imagePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
