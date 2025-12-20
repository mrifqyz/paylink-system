package net.ryzen.paylinksystem.module.payment.cc.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.payment.cc.config.properties.TokenizationProperties;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.request.DeleteTokenizationExternalRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.response.DeleteTokenizationExternalResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.DeleteTokenizationRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.DeleteTokenizationResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.DeleteTokenizationService;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import net.ryzen.paylinksystem.service.RestService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteTokenizationServiceImpl implements DeleteTokenizationService {
    private final RestService restService;
    private final TransactionRepository transactionRepository;
    private final TokenizationProperties tokenizationProperties;
    @Override
    public DeleteTokenizationResponseDTO execute(DeleteTokenizationRequestDTO request) {
        validateTrx(request);

        var requestToCore = buildRequestExternal(request);
        var responseFromCore = hitToCore(requestToCore, request);

        return DeleteTokenizationResponseDTO.builder()
                .status(responseFromCore.getStatus())
                .build();
    }

    private void validateTrx(DeleteTokenizationRequestDTO request) {
        transactionRepository.findFirstByTokenIdAndRequestIdAndClient_ClientId(request.getTokenId(),
                        request.getRequestId(), request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private DeleteTokenizationExternalRequestDTO buildRequestExternal(DeleteTokenizationRequestDTO request) {
        return DeleteTokenizationExternalRequestDTO.builder()
                .cardToken(request.getCardToken())
                .build();
    }

    private DeleteTokenizationExternalResponseDTO hitToCore(DeleteTokenizationExternalRequestDTO requestToCore, DeleteTokenizationRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Request-Id", request.getRequestId());
        headers.set("Client-Id", request.getRequestId());

        String response = restService.httpPostWithHeader(tokenizationProperties.getDeleteTokenUrl(), requestToCore, headers);
        return new Gson().fromJson(response, DeleteTokenizationExternalResponseDTO.class);
    }
}
