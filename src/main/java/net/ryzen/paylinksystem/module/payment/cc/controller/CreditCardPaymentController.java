package net.ryzen.paylinksystem.module.payment.cc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.CreditCardPaymentRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.CreditCardPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/paylink/v1/credit-card")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CreditCardPaymentController {
    private final ServiceExecutor serviceExecutor;

    @PostMapping("/payment")
    public ResponseEntity<?> doCreditCardPayment(@RequestHeader("Client-Id") String clientId,
                                                 @RequestHeader("Request-Id") String requestId,
                                                 @RequestBody CreditCardPaymentRequestDTO request){
        request.setClientId(clientId);
        request.setRequestId(requestId);
        log.info("doCreditCardPayment: {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(CreditCardPaymentService.class, request));
    }


}
