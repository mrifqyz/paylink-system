package net.ryzen.paylinksystem.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class SignatureUtils {

    private final ObjectMapper objectMapper;
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HASH_ALGORITHM = "SHA-256";

    public String generateSignature(String clientId, String requestId, String timestamp,
                                   String requestBody, String sharedKey) {
        try {
            log.info("Original Request Body Length: {} bytes", requestBody.length());

            String minifiedBody = minifyJson(requestBody);
            String bodyDigest = generateSHA256Digest(minifiedBody);

            log.debug("Component Signature\nClient-Id: {}\n" +
                    "Request-Id: {}\n" +
                    "Shared-Key: {}\n" +
                    "Timestamp: {}\n" +
                    "Digest: {}", clientId, requestId, sharedKey, timestamp, bodyDigest);

            String dataToSign = clientId + requestId + timestamp + bodyDigest;

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                sharedKey.getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
            );
            mac.init(secretKeySpec);

            byte[] signature = mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(signature);

        } catch (Exception e) {
            log.error("Error generating signature: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    public boolean verifySignature(String clientId, String requestId, String timestamp,
                                  String requestBody, String sharedKey, String providedSignature) {
        try {
            String expectedSignature = generateSignature(clientId, requestId, timestamp, requestBody, sharedKey);
            log.debug("Sent Signature: {}", providedSignature);
            log.debug("Expected signature: {}", expectedSignature);
            return MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                providedSignature.getBytes(StandardCharsets.UTF_8)
            );

        } catch (Exception e) {
            log.error("Error verifying signature: {}", e.getMessage(), e);
            return false;
        }
    }

    public String generateSHA256Digest(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate digest", e);
        }
    }

    private String minifyJson(String json) {
        try {
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return objectMapper.writeValueAsString(jsonObject);
        } catch (Exception e) {
            log.warn("Failed to minify JSON, using original: {}", e.getMessage());
            return json;
        }
    }
}
