package net.ryzen.paylinksystem.module.checkout.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.base.command.ServiceExecutor;
import net.ryzen.paylinksystem.module.checkout.dto.request.GetPaymentLinkDataRequestDTO;
import net.ryzen.paylinksystem.module.checkout.services.contract.GetPaymentLinkDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/paylink/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GetPaymentLinkDataController {
    private final ServiceExecutor serviceExecutor;

    @GetMapping("/data")
    public ResponseEntity<?> doGetPaymentLinkData(@RequestParam(name = "tokenId") String tokenId){
        log.info("doGetPaymentLinkData tokenId: {}", tokenId);
        return ResponseEntity.status(HttpStatus.OK).body(
                serviceExecutor.execute(GetPaymentLinkDataService.class,
                GetPaymentLinkDataRequestDTO.builder()
                        .tokenId(tokenId)
                .build()));
    }
}
