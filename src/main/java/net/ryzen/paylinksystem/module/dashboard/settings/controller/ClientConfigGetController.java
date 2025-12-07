package net.ryzen.paylinksystem.module.dashboard.settings.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.dashboard.settings.dto.request.ClientConfigGetRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.settings.service.contract.ClientConfigGetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dashboard/config")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ClientConfigGetController {
    private final ServiceExecutor serviceExecutor;

    @GetMapping
    public ResponseEntity<?> getClientConfig(@RequestAttribute String clientId) {
        log.info("getClientConfig {}", clientId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(serviceExecutor.execute(ClientConfigGetService.class,
                        ClientConfigGetRequestDTO.builder()
                                .clientId(clientId)
                                .build())
                );
    }
}
