package net.ryzen.paylinksystem.module.checkout.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
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
    public ResponseEntity<?> doGeneratePaymentLink(@RequestBody GeneratePaymentLinkRequestDTO request) {
        log.info("doGeneratePaymentLink: {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(GeneratePaymentLinkService.class, request));
    }

    //uses jwt
    @PostMapping("/internal/generate")
    public ResponseEntity<?> doGeneratePaymentLinkInternal(@RequestBody GeneratePaymentLinkRequestDTO request) {
        log.info("doGeneratePaymentLinkInternal: {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(GeneratePaymentLinkService.class, request));
    }
}
