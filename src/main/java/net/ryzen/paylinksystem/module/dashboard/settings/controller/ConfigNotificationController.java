package net.ryzen.paylinksystem.module.dashboard.settings.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ConfigNotificationRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.service.contract.ConfigNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dashboard/config")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ConfigNotificationController {
    private final ServiceExecutor serviceExecutor;

    @PostMapping("/notification")
    public ResponseEntity<?> doConfigNotificationClient(@RequestAttribute String clientId,
                                                        @RequestBody ConfigNotificationRequestDTO request) {
        request.setClientId(clientId);
        log.info("doConfigNotificationClient {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(ConfigNotificationService.class, request));
    }
}
