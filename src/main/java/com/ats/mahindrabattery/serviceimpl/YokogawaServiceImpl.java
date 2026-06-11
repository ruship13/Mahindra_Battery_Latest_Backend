package com.ats.mahindrabattery.serviceimpl;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class YokogawaServiceImpl {

    private final String BASE_URL = "http://10.194.250.53:8080/";
    private final String REQUEST_URI = "api/readcalc/";
    private final String SECRET_KEY = "EDX@Yokogawa4ATS";

    public boolean checkConnection() {
        try {
            String message = "GET" + REQUEST_URI;
            String signature = generateHmac(message, SECRET_KEY);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", signature);
            headers.set("Timestamp", timestamp);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(
                    BASE_URL + REQUEST_URI,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // ✅ If API responds (200 OK)
            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            // ❌ API not reachable / error
            return false;
        }
    }

    private String generateHmac(String message, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(key);
        byte[] rawHmac = mac.doFinal(message.getBytes());

        StringBuilder hex = new StringBuilder();
        for (byte b : rawHmac) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}