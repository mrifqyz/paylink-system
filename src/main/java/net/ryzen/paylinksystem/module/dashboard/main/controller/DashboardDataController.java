package net.ryzen.paylinksystem.module.dashboard.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.dashboard.main.dto.request.DashboardDataRequestDTO;
import net.ryzen.paylinksystem.module.dashboard.main.service.contract.DashboardDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dashboard/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DashboardDataController {
    private final ServiceExecutor serviceExecutor;

    @GetMapping
    public ResponseEntity<?> doGetDashboardSummary(@RequestAttribute String clientId){
        log.info("doGetDashboardSummary client id: {}", clientId);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(DashboardDataService.class, DashboardDataRequestDTO.builder().clientId(clientId).build()));
    }
}
