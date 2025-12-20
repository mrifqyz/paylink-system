package net.ryzen.paylinksystem.module.payment.cc.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.entity.Transaction;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.payment.cc.config.properties.TokenizationProperties;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.request.GetTokenizationExternalRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.response.GetTokenizationExternalResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.GetTokenizationRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.GetTokenizationResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.GetTokenizationService;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import net.ryzen.paylinksystem.service.RestService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class GetTokenizationServiceImpl implements GetTokenizationService {
    private TransactionRepository transactionRepository;
    private RestService restService;
    private final TokenizationProperties tokenizationProperties;
    @Override
    public GetTokenizationResponseDTO execute(GetTokenizationRequestDTO request) {
        Transaction transaction = getTransactions(request);
        var requestToCore = buildRequestToCore(transaction);

        var responseFromCore = hitToCore(requestToCore, request);

        return GetTokenizationResponseDTO.builder()
                .cardsData(buildListCardData(responseFromCore))
                .build();
    }

    private Transaction getTransactions(GetTokenizationRequestDTO request) {
        return transactionRepository.findFirstByTokenIdAndRequestIdAndClient_ClientId(request.getTokenId(),
                        request.getRequestId(), request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private GetTokenizationExternalRequestDTO buildRequestToCore(Transaction transaction){
        return GetTokenizationExternalRequestDTO.builder()
                .customerId(transaction.getCustomer().getIdentifier())
                .build();
    }

    private GetTokenizationExternalResponseDTO hitToCore(GetTokenizationExternalRequestDTO requestToCore,
                                                        GetTokenizationRequestDTO request){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Request-Id", request.getRequestId());
        headers.set("Client-Id", request.getRequestId());

        String response = restService.httpPostWithHeader(tokenizationProperties.getGetTokenUrl(), requestToCore, headers);
        return new Gson().fromJson(response, GetTokenizationExternalResponseDTO.class);
    }

    private List<GetTokenizationResponseDTO.TokenizationItem> buildListCardData(GetTokenizationExternalResponseDTO responseCore){
        return responseCore.getTokens().parallelStream()
                .map(data -> GetTokenizationResponseDTO.TokenizationItem.builder()
                        .cardToken(data.getCardToken())
                        .maskedCard(data.getMaskedCard())
                        .cardholderName(data.getCardholderName())
                        .brandName(data.getBrandName())
                        .build())
                .toList();
    }
}
