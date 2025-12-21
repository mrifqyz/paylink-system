package net.ryzen.paylinksystem.module.payment.cc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.GenerateEncryptKeyRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.GenerateEncryptKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/v1/credit-card")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GenerateEncryptKeyController {
    private final ServiceExecutor serviceExecutor;

    @PostMapping("/generate-key/{tokenId}")
    public ResponseEntity<?> doGenerateEncryptKey(@PathVariable("tokenId")  String tokenId,
                                                  @RequestHeader("Client-Id") String clientId,
                                                  @RequestHeader("Request-Id") String requestId) {
        GenerateEncryptKeyRequestDTO request = GenerateEncryptKeyRequestDTO.builder()
                .requestId(requestId)
                .clientId(clientId)
                .tokenId(tokenId)
                .build();
        log.info("doGenerateEncryptKey {}", request.toString());
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(GenerateEncryptKeyService.class, request));
    }
}
