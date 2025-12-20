package net.ryzen.paylinksystem.module.payment.cc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.GetTokenizationRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.GetTokenizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/paylink/v1/credit-card")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GetTokenizationController {
    private final ServiceExecutor serviceExecutor;

    @GetMapping("/tokenization")
    public ResponseEntity<?> doGetTokenization(@RequestHeader("Client-Id") String clientId,
                                               @RequestHeader("Request-Id") String requestId,
                                               @RequestParam("tokenId") String tokenId){
        GetTokenizationRequestDTO request = GetTokenizationRequestDTO.builder()
                .clientId(clientId)
                .requestId(requestId)
                .tokenId(tokenId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(GetTokenizationService.class, request));
    }
}
