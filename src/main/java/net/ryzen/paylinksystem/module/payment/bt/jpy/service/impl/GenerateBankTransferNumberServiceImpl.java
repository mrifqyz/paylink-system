package net.ryzen.paylinksystem.module.payment.bt.jpy.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.utils.DateTimeUtils;
import net.ryzen.paylinksystem.entity.Transaction;
import net.ryzen.paylinksystem.enums.RedisKeyEnum;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.payment.bt.jpy.config.properties.BankTransferProperties;
import net.ryzen.paylinksystem.module.payment.bt.jpy.dto.external.request.GenerateBankTransferCoreRequestDTO;
import net.ryzen.paylinksystem.module.payment.bt.jpy.dto.external.response.GenerateBankTransferCoreResponseDTO;
import net.ryzen.paylinksystem.module.payment.bt.jpy.dto.request.GenerateBankTransferNumberRequestDTO;
import net.ryzen.paylinksystem.module.payment.bt.jpy.dto.response.GenerateBankTransferNumberResponseDTO;
import net.ryzen.paylinksystem.module.payment.bt.jpy.service.contract.GenerateBankTransferNumberService;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import net.ryzen.paylinksystem.service.RestService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateBankTransferNumberServiceImpl implements GenerateBankTransferNumberService {
    private final TransactionRepository transactionRepository;
    private final RestService restService;
    private final RedisTemplate<String, String> redisTemplate;
    private final BankTransferProperties bankTransferProperties;

    @Override
    public GenerateBankTransferNumberResponseDTO execute(GenerateBankTransferNumberRequestDTO request) {
        Transaction transaction = getTransaction(request);

        var dataRedis = getDataRedis(request);

        if (dataRedis != null) {
            return buildResponse(dataRedis);
        }

        GenerateBankTransferCoreRequestDTO requestGenerateBankTransfer = buildRequestToCore(request, transaction);
        var responseCore = hitToCoreGenerateBankNumber(request, requestGenerateBankTransfer);
        storeDataRedis(request, responseCore);

        return buildResponse(responseCore);
    }

    private Transaction getTransaction(GenerateBankTransferNumberRequestDTO request) {
        return transactionRepository.findFirstByTokenIdAndRequestIdAndClient_ClientId(request.getTokenId(),
                        request.getRequestId(), request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private GenerateBankTransferNumberResponseDTO buildResponse(GenerateBankTransferCoreResponseDTO responseCore) {

        return GenerateBankTransferNumberResponseDTO.builder()
                .accountNumber(responseCore.getAccountNumber())
                .howToPay(responseCore.getHowToPay())
                .bankDetails(GenerateBankTransferNumberResponseDTO.BankDetails.builder()
                        .accountType(responseCore.getBankDetails().getAccountType())
                        .bankName(responseCore.getBankDetails().getBankName())
                        .bankBranch(responseCore.getBankDetails().getBankBranch())
                        .recipientName(responseCore.getBankDetails().getRecipientName())
                        .build())
                .currency(responseCore.getCurrency())
                .build();
    }

    private void storeDataRedis(GenerateBankTransferNumberRequestDTO request, GenerateBankTransferCoreResponseDTO responseCore) {
        String redisKey = String.format(RedisKeyEnum.BANK_TRANSFER_DATA.getKey(), request.getBankCode(), request.getClientId(), request.getTokenId(), request.getRequestId());
        redisTemplate.opsForValue().set(redisKey, new Gson().toJson(responseCore), RedisKeyEnum.BANK_TRANSFER_DATA.getExpiredSeconds(), TimeUnit.SECONDS);
    }

    private GenerateBankTransferCoreResponseDTO getDataRedis(GenerateBankTransferNumberRequestDTO request) {
        String redisKey = String.format(RedisKeyEnum.BANK_TRANSFER_DATA.getKey(), request.getBankCode(), request.getClientId(), request.getTokenId(), request.getRequestId());
        String data = redisTemplate.opsForValue().get(redisKey);

        if (data == null) {
            log.debug("Redis data is null");
            return null;
        }

        return new Gson().fromJson(data, GenerateBankTransferCoreResponseDTO.class);
    }

    private GenerateBankTransferCoreRequestDTO buildRequestToCore(GenerateBankTransferNumberRequestDTO request, Transaction transaction) {
        return GenerateBankTransferCoreRequestDTO.builder()
                .bankCode(request.getBankCode())
                .transaction(GenerateBankTransferCoreRequestDTO.Transaction.builder()
                        .invoiceNumber(transaction.getInvoiceNumber())
                        .amount(transaction.getAmount())
                        .currency(transaction.getCurrency())
                        .build())
                .uuid(UUID.randomUUID().toString())
                .requestTimestamp(DateTimeUtils.convertDateToString(new Date()))
                .build();
    }

    private GenerateBankTransferCoreResponseDTO hitToCoreGenerateBankNumber(GenerateBankTransferNumberRequestDTO request, GenerateBankTransferCoreRequestDTO requestToCore) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", request.getRequestId());
        headers.set("Request-Id", request.getRequestId());

        String response = restService.httpPostWithHeader(bankTransferProperties.getGenerateBankNumberUrl(), requestToCore, headers);
        return new Gson().fromJson(response, GenerateBankTransferCoreResponseDTO.class);
    }
}
