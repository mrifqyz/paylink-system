package net.ryzen.paylinksystem.module.dashboard.settings.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ConfigPaymentMethodRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.service.contract.ConfigPaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dashboard/config")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ConfigPaymentMethodController {
    private final ServiceExecutor serviceExecutor;

    @PostMapping("/payment-method")
    public ResponseEntity<?> doConfigPaymentMethod(@RequestAttribute String clientId,
                                                   @RequestBody ConfigPaymentMethodRequestDTO request){
        request.setClientId(clientId);
        log.info("doConfigPaymentMethod {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(ConfigPaymentMethodService.class, request));
    }
}
