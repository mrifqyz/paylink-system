package net.ryzen.paylinksystem.module.payment.cc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.ThreeDoSecureCallbackRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.ThreeDoSecureCallbackResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.ThreeDoSecureCallbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@Slf4j
@RequestMapping("/v1/credit-card")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ThreeDoSecureCallbackController {
    private final ServiceExecutor serviceExecutor;

    @GetMapping("/3ds-callback/{token_id}/")
    public ResponseEntity<?> doGet3dsCallback(@PathVariable("token_id") String tokenId,
                                              @RequestParam("status") String status,
                                              @RequestParam("client_id") String clientId,
                                              @RequestParam("request_id") String requestId) {

        ThreeDoSecureCallbackRequestDTO request = ThreeDoSecureCallbackRequestDTO.builder()
                    .requestId(requestId)
                    .tokenId(tokenId)
                    .clientId(clientId)
                    .status(status)
                .build();
        log.info("doGet3dsCallback {}", request.toString());
        ThreeDoSecureCallbackResponseDTO result = serviceExecutor.execute(ThreeDoSecureCallbackService.class, request);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(result.getUrl())).build();

    }


}
