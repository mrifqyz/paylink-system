package net.ryzen.paylinksystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestService {
    public ResponseEntity<String> httpPostWithHeader(String url, Object body, HttpHeaders  headers) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(url, body, String.class, headers);
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
