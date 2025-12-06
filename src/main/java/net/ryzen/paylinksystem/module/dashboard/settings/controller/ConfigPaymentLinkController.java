package net.ryzen.paylinksystem.module.dashboard.settings.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ConfigPaymentLinkRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.service.contract.ConfigPaymentLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dashboard/config")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ConfigPaymentLinkController {
    private final ServiceExecutor serviceExecutor;

    @PostMapping("/payment-link")
    public ResponseEntity<?> doConfigPaymentLink(@RequestAttribute String clientId,
                                                      @RequestBody ConfigPaymentLinkRequestDTO request){
        request.setClientId(clientId);
        log.info("doConfigPaymentLink {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(ConfigPaymentLinkService.class, request));
    }
}
