package net.ryzen.paylinksystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestService {
    public String httpPostWithHeader(String url, Object body, HttpHeaders  headers) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            log.info("httpPostWithHeader url {} request {} headers {}", url, new Gson().toJson(body), headers.toString());
            ResponseEntity<String> response = restTemplate.postForEntity(url, body, String.class, headers);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(response.getBody(), JsonNode.class);
            log.info("status code: {} body: {}", response.getStatusCode(), jsonNode.toString());
            return response.getBody();
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public ResponseEntity<String> httpGetWithHeader(String url, HttpHeaders  headers) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(url, String.class, headers);
    }

    public ResponseEntity<String> httpPost(String url, Object body) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(url, body, String.class);
    }

    public ResponseEntity<String> httpGet(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(url, String.class);
    }
}
