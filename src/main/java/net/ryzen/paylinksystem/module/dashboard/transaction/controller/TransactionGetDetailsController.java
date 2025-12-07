package net.ryzen.paylinksystem.module.dashboard.transaction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.dashboard.transaction.dto.request.TransactionGetDetailsRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.transaction.service.contract.TransactionGetDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dashboard/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TransactionGetDetailsController {
    private final ServiceExecutor serviceExecutor;

    @GetMapping("/transactions")
    public ResponseEntity<?> doGetDetailsTransaction(@Validated @RequestParam("transaction_id") String transactionId,
                                                     @RequestAttribute String clientId) {
        String decodedTransactionId = transactionId.replace(" ", "+");

        TransactionGetDetailsRequestDTO request = TransactionGetDetailsRequestDTO.builder()
                .transactionId(decodedTransactionId)
                .clientId(clientId)
                .build();

        log.info("doGetDetailsTransaction request: {}", request);

        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(TransactionGetDetailsService.class, request));

    }
}
