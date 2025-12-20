package net.ryzen.paylinksystem.module.payment.cc.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.utils.DateTimeUtils;
import net.ryzen.paylinksystem.common.utils.EncryptorUtils;
import net.ryzen.paylinksystem.entity.Transaction;
import net.ryzen.paylinksystem.enums.RedisKeyEnum;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.enums.TransactionStatusEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.payment.cc.config.properties.CreditCardPaymentProperties;
import net.ryzen.paylinksystem.module.payment.cc.dto.PaymentDataDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.request.CreditCardChargeRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.response.CreditCardChargeResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.ThreeDoSecureCallbackRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.ThreeDoSecureCallbackResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.ThreeDoSecureCallbackService;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import net.ryzen.paylinksystem.service.RestService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThreeDoSecureCallbackServiceImpl implements ThreeDoSecureCallbackService {
    private final TransactionRepository transactionRepository;
    private final CreditCardPaymentProperties creditCardPaymentProperties;
    private final RestService restService;
    private final RedisTemplate<String, String> redisTemplate;
    private final EncryptorUtils encryptorUtils;

    @Override
    public ThreeDoSecureCallbackResponseDTO execute(ThreeDoSecureCallbackRequestDTO request) {

        if (request.getStatus().equalsIgnoreCase("success")) {
            Transaction transaction = getTransaction(request);
            var dataPayment = getPaymentDataRedis(request);

            if (dataPayment == null) {
                log.error("There is no payment data for this transaction");
                throw new DataNotFoundException("There is no payment data for this transaction");
            }

            var chargeRequest = buildChargeRequest(transaction, dataPayment);
            var chargeResponse = hitToCoreCharge(request, chargeRequest);

            if (chargeResponse.getStatus().equals(TransactionStatusEnum.SUCCESS.name())) {
                transaction.setStatus(TransactionStatusEnum.ON_HOLD.name());
                transaction.setUpdatedDate(new Date());
                transactionRepository.save(transaction);
                return ThreeDoSecureCallbackResponseDTO.builder()
                        .url(creditCardPaymentProperties.getFrontendRedirectUrlSuccess())
                        .build();
            }
        }

        return ThreeDoSecureCallbackResponseDTO.builder()
                .url(creditCardPaymentProperties.getFrontendRedirectUrlFailed())
                .build();
    }
    private Transaction getTransaction(ThreeDoSecureCallbackRequestDTO request) {
        return transactionRepository.findFirstByTokenIdAndRequestIdAndClient_ClientId(request.getTokenId(),
                        request.getRequestId(), request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private CreditCardChargeResponseDTO hitToCoreCharge(ThreeDoSecureCallbackRequestDTO request, CreditCardChargeRequestDTO requestToCore) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", request.getRequestId());
        headers.set("Request-Id", request.getRequestId());

        String response = restService.httpPostWithHeader(creditCardPaymentProperties.getCreditCardChargeUrl(), requestToCore, headers);
        return new Gson().fromJson(response, CreditCardChargeResponseDTO.class);
    }

    private CreditCardChargeRequestDTO buildChargeRequest(Transaction transaction,
                                                          PaymentDataDTO paymentDataDTO) {
        return CreditCardChargeRequestDTO.builder()
                .transaction(CreditCardChargeRequestDTO.Transaction.builder()
                        .amount(transaction.getAmount())
                        .currency(transaction.getCurrency())
                        .invoiceNumber(transaction.getInvoiceNumber())
                        .build())
                .requestTimestamp(DateTimeUtils.convertDateToString(new Date()))
                .uuid(UUID.randomUUID().toString())
                .cardData(paymentDataDTO)
                .build();
    }

    private PaymentDataDTO getPaymentDataRedis(ThreeDoSecureCallbackRequestDTO request) {
        String redisKey = String.format(RedisKeyEnum.CHARGE_CC.getKey(), request.getClientId(), request.getTokenId(), request.getRequestId());
        String data = redisTemplate.opsForValue().get(redisKey);

        if (data != null) {
            return new Gson().fromJson(encryptorUtils.decrypt(data), PaymentDataDTO.class);
        }
        log.debug("Redis data redis charge not found");
        return null;
    }

}
