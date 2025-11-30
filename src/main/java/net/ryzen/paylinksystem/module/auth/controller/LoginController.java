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
    public ResponseEntity<?> doLogin(@RequestBody LoginRequestDTO request,
                                         HttpServletRequest servletRequest) {
        log.info("doLogin: {}", request);
        request.setIpAddress(getIpAddress(servletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(serviceExecutor.execute(LoginService.class, request));
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress != null && !ipAddress.isEmpty()) {
            return ipAddress.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
