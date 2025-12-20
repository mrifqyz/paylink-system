package net.ryzen.paylinksystem.module.payment.cc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.CheckCardRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.CheckCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/paylink/v1/credit-card")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CheckCardController {
    private final ServiceExecutor serviceExecutor;

    @PostMapping("/check/{tokenId}")
    public ResponseEntity<?> doCheckCard(@RequestHeader("Client-Id") String clientId,
                                         @RequestHeader("Request-Id") String requestId,
                                         @RequestBody CheckCardRequestDTO request){
        request.setClientId(clientId);
        request.setRequestId(requestId);
        log.info("doCheckCard: {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(CheckCardService.class, request));
    }
}
