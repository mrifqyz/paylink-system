package net.ryzen.paylinksystem.module.payment.bt.jpy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.payment.bt.jpy.dto.request.GenerateBankTransferNumberRequestDTO;
import net.ryzen.paylinksystem.module.payment.bt.jpy.service.contract.GenerateBankTransferNumberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/paylink/v1/bank-transfer")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GenerateBankTransferNumberController {
    private final ServiceExecutor serviceExecutor;

    @PostMapping
    private ResponseEntity<?> doGenerateBankTransferNumber(@RequestHeader("Client-Id") String clientId,
                                                           @RequestHeader("Request-Id") String requestId,
                                                           @RequestParam("tokenId") String tokenId,
                                                           @RequestBody GenerateBankTransferNumberRequestDTO request) {
        request.setClientId(clientId);
        request.setRequestId(requestId);
        request.setTokenId(tokenId);
        log.debug("doGenerateBankTransferNumber: {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(GenerateBankTransferNumberService.class, request));
    }
}
