package net.ryzen.paylinksystem.config.interceptor;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.utils.EncryptorUtils;
import net.ryzen.paylinksystem.common.utils.SignatureUtils;
import net.ryzen.paylinksystem.config.filter.CachedBodyHttpServletRequest;
import net.ryzen.paylinksystem.entity.Client;
import net.ryzen.paylinksystem.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SignatureVerificationInterceptor implements HandlerInterceptor {

    private final SignatureUtils signatureUtils;
    private final EncryptorUtils encryptorUtils;
    private final ClientRepository clientRepository;
    private final Gson gson;

    @Value("${paylink.signature.enabled-endpoints:}")
    private List<String> enabledEndpoints;

    @Value("${paylink.signature.timestamp-tolerance-seconds:300}")
    private long timestampToleranceSeconds;

    private static final String HEADER_CLIENT_ID = "Client-Id";
    private static final String HEADER_REQUEST_ID = "Request-Id";
    private static final String HEADER_SIGNATURE = "X-Signature";
    private static final String HEADER_TIMESTAMP = "Request-Timestamp";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();

        if (!isSignatureRequired(requestUri)) {
            return true;
        }

        String clientId = request.getHeader(HEADER_CLIENT_ID);
        String requestId = request.getHeader(HEADER_REQUEST_ID);
        String signature = request.getHeader(HEADER_SIGNATURE);
        String timestamp = request.getHeader(HEADER_TIMESTAMP);

        if (!validateHeaders(clientId, requestId, signature, timestamp, response)) {
            return false;
        }

        if (!validateTimestamp(timestamp, response)) {
            return false;
        }

        Optional<Client> clientOpt = clientRepository.findByClientId(clientId);
        if (clientOpt.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid client");
            return false;
        }

        Client client = clientOpt.get();
        if (!client.getIsActive()) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Client is inactive");
            return false;
        }

        String requestBody = getRequestBody(request);

        log.info("Interceptor - Request Body Length: {} bytes", requestBody.length());
        log.info("Interceptor - Request Body (first 100 chars): {}",
            requestBody.length() > 100 ? requestBody.substring(0, 100) : requestBody);

        boolean isValidSignature = signatureUtils.verifySignature(
                clientId,
                requestId,
                timestamp,
                requestBody,
                encryptorUtils.decrypt(client.getSharedKey()),
                signature
        );

        if (!isValidSignature) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature");
            return false;
        }

        request.setAttribute("clientId", clientId);
        request.setAttribute("clientEmail", client.getEmail());
        request.setAttribute("requestId", requestId);

        return true;
    }

    private boolean isSignatureRequired(String requestUri) {
        if (enabledEndpoints == null || enabledEndpoints.isEmpty()) {
            return false;
        }

        return enabledEndpoints.stream()
            .anyMatch(pattern -> {
                if (pattern.endsWith("**")) {
                    String prefix = pattern.substring(0, pattern.length() - 2);
                    return requestUri.startsWith(prefix);
                } else if (pattern.endsWith("*")) {
                    String prefix = pattern.substring(0, pattern.length() - 1);
                    return requestUri.startsWith(prefix);
                } else {
                    return requestUri.equals(pattern);
                }
            });
    }

    private boolean validateHeaders(String clientId, String requestId, String signature,
                                   String timestamp, HttpServletResponse response) throws IOException {
        if (clientId == null || clientId.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Missing " + HEADER_CLIENT_ID + " header");
            return false;
        }

        if (requestId == null || requestId.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Missing " + HEADER_REQUEST_ID + " header");
            return false;
        }

        if (signature == null || signature.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Missing " + HEADER_SIGNATURE + " header");
            return false;
        }

        if (timestamp == null || timestamp.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Missing " + HEADER_TIMESTAMP + " header");
            return false;
        }

        return true;
    }

    private boolean validateTimestamp(String timestamp, HttpServletResponse response) throws IOException {
        try {
            String trimmedTimestamp = timestamp.trim();

            long requestTime = Long.parseLong(trimmedTimestamp);
            long currentTime = Instant.now().getEpochSecond();
            long timeDifference = Math.abs(currentTime - requestTime);

            if (timeDifference > timestampToleranceSeconds) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Request timestamp is invalid or expired");
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Invalid timestamp format. Expected epoch seconds");
            return false;
        }
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        if (request instanceof CachedBodyHttpServletRequest) {
            return ((CachedBodyHttpServletRequest) request).getBody();
        }

        try {
            return request.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);
        } catch (Exception e) {
            log.warn("Unable to read request body: {}", e.getMessage());
            return "";
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", Instant.now().toString());

        response.getWriter().write(gson.toJson(errorResponse));
        response.getWriter().flush();
    }
}
