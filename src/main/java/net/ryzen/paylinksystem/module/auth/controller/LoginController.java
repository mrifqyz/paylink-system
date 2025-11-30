package net.ryzen.paylinksystem.module.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.auth.dto.request.LoginRequestDTO;
import net.ryzen.paylinksystem.module.auth.services.contract.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dashboard/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LoginController {
    private final ServiceExecutor serviceExecutor;

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody LoginRequestDTO request) {
        log.info("doLogin: {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(LoginService.class, request));
    }
}
