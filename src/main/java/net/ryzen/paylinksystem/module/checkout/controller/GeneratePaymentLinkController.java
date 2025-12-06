package net.ryzen.paylinksystem.module.checkout.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.checkout.dto.HeadersDTO;
import net.ryzen.paylinksystem.module.checkout.dto.request.GeneratePaymentLinkRequestDTO;
import net.ryzen.paylinksystem.module.checkout.services.contract.GeneratePaymentLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/paylink/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GeneratePaymentLinkController {
    private final ServiceExecutor serviceExecutor;

    //uses signature
    @PostMapping("/generate")
    public ResponseEntity<?> doGeneratePaymentLink(@RequestBody GeneratePaymentLinkRequestDTO request,
                                                   @RequestHeader(name = "Client-Id") String clientId,
                                                   @RequestHeader(name = "Request-Id") String requestId,
                                                   @RequestHeader(name = "Request-Timestamp") String requestTimestamp,
                                                   @RequestHeader(name = "X-Signature") String signature) {
        request.setHeaders(buildHeadersDTO(clientId, requestId, requestTimestamp, signature));
        log.info("doGeneratePaymentLink: {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(GeneratePaymentLinkService.class, request));
    }

    //uses jwt
    @PostMapping("/internal/generate")
    public ResponseEntity<?> doGeneratePaymentLinkInternal(@RequestBody GeneratePaymentLinkRequestDTO request,
                                                           @RequestAttribute String clientId) {
        request.setClientId(clientId);
        log.info("doGeneratePaymentLinkInternal: {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(GeneratePaymentLinkService.class, request));
    }

    private HeadersDTO buildHeadersDTO(String clientId, String requestId, String requestTimestamp, String signature) {
        return HeadersDTO.builder()
                .clientId(clientId)
                .requestId(requestId)
                .timestamp(requestTimestamp)
                .signature(signature)
                .build();
    }
}
