package net.ryzen.paylinksystem.module.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.auth.dto.request.RegisterRequestDTO;
import net.ryzen.paylinksystem.module.auth.services.contract.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dashboard/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RegisterController {
    private final ServiceExecutor serviceExecutor;

    @PostMapping("/register")
    public ResponseEntity<?> doRegister(@RequestBody RegisterRequestDTO request) {
        log.info("doRegister: {}", request);
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(RegisterService.class, request));
    }
}
