package net.ryzen.paylinksystem.module.payment.cc.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.enums.RedisKeyEnum;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.payment.cc.common.LuhnValidator;
import net.ryzen.paylinksystem.module.payment.cc.config.properties.CreditCardPaymentProperties;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.request.CheckBinInstallment3dsRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.response.CheckBinInstallment3dsResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.CheckCardRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.CheckCardResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.CheckCardService;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import net.ryzen.paylinksystem.service.RestService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckCardServiceImpl implements CheckCardService {
    private final TransactionRepository transactionRepository;
    private final CreditCardPaymentProperties creditCardPaymentProperties;
    private final RestService restService;
    private final RedisTemplate<String, String> redisTemplate;
    @Override
    public CheckCardResponseDTO execute(CheckCardRequestDTO request) {

        if (request.getCardNumber().length() > 8 && request.getCardNumber().length() <= 16) {
            LuhnValidator.isValid(request.getCardNumber());
            request.setCardNumber(request.getCardNumber().substring(0, 8));
        }

        validateTrx(request);

        var requestToCore = buildRequestToCore(request);
        var responseFromCore = hitToCore(requestToCore, request);

        storeDataBin3dsToRedis(request, responseFromCore);
        return buildResponse(responseFromCore);
    }

    private void validateTrx(CheckCardRequestDTO request) {
        transactionRepository.findFirstByTokenIdAndRequestIdAndClient_ClientId(request.getTokenId(),
                        request.getRequestId(), request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private CheckBinInstallment3dsRequestDTO buildRequestToCore(CheckCardRequestDTO request){
        return CheckBinInstallment3dsRequestDTO.builder()
                .binNumber(request.getCardNumber().substring(0, 6))
                .build();
    }

    private CheckBinInstallment3dsResponseDTO hitToCore(CheckBinInstallment3dsRequestDTO requestToCore,
                                                        CheckCardRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", request.getRequestId());

        String response = restService.httpPostWithHeader(creditCardPaymentProperties.getCheckBinUrl(), requestToCore, headers);
        return new Gson().fromJson(response, CheckBinInstallment3dsResponseDTO.class);
    }

    private CheckCardResponseDTO buildResponse(CheckBinInstallment3dsResponseDTO responseFromCore){
        return CheckCardResponseDTO.builder()
                .allowInstallment(responseFromCore.getIsAllowInstallment() && !responseFromCore.getInstallmentPlans().isEmpty())
                .installmentOptions(responseFromCore.getIsAllowInstallment() && !responseFromCore.getInstallmentPlans().isEmpty() ? responseFromCore.getInstallmentPlans() : null)
                .isValid(true)
                .isUse3ds(responseFromCore.getIsUse3ds())
                .build();
    }

    private void storeDataBin3dsToRedis(CheckCardRequestDTO request, CheckBinInstallment3dsResponseDTO responseCore){
        String redisKey = RedisKeyEnum.FRICTIONLESS_3DS_CHECK.getKey().formatted(request.getClientId(), request.getTokenId(), request.getRequestId(), request.getCardNumber().substring(0, 6));
        redisTemplate.opsForValue().set(redisKey, new Gson().toJson(responseCore), RedisKeyEnum.FRICTIONLESS_3DS_CHECK.getExpiredSeconds(), TimeUnit.SECONDS);
    }
}
