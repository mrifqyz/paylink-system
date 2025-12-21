package net.ryzen.paylinksystem.module.payment.cc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.DeleteTokenizationRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.DeleteTokenizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/v1/credit-card")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DeleteTokenizationController {
    private final ServiceExecutor serviceExecutor;

    @DeleteMapping("/tokenization")
    public ResponseEntity<?> doDeleteTokenization(@RequestHeader("Client-Id") String clientId,
                                                  @RequestHeader("Request-Id") String requestId,
                                                  @RequestParam("tokenId") String tokenId,
                                                  @RequestParam("cardToken") String cardToken){
        DeleteTokenizationRequestDTO request = DeleteTokenizationRequestDTO.builder()
                .cardToken(cardToken)
                .clientId(clientId)
                .requestId(requestId)
                .tokenId(tokenId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(DeleteTokenizationService.class, request));
    }
}
