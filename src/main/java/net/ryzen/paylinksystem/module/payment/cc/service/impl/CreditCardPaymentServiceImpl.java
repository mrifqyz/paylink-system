package net.ryzen.paylinksystem.module.payment.cc.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.utils.CardDecryptorUtils;
import net.ryzen.paylinksystem.common.utils.DateTimeUtils;
import net.ryzen.paylinksystem.common.utils.EncryptorUtils;
import net.ryzen.paylinksystem.entity.Transaction;
import net.ryzen.paylinksystem.enums.RedisKeyEnum;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.enums.TransactionStatusEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.exception.InvalidDataException;
import net.ryzen.paylinksystem.module.payment.cc.config.properties.CreditCardPaymentProperties;
import net.ryzen.paylinksystem.module.payment.cc.dto.PaymentDataDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.request.CreditCardChargeRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.request.ThreeDoSecurePaymentRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.response.CheckBinInstallment3dsResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.external.response.CreditCardChargeResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.CreditCardPaymentRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.CreditCardPaymentResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.CreditCardPaymentService;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import net.ryzen.paylinksystem.service.RestService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditCardPaymentServiceImpl implements CreditCardPaymentService {
    private final TransactionRepository transactionRepository;
    private final RestService restService;
    private final RedisTemplate<String, String> redisTemplate;
    private final CreditCardPaymentProperties creditCardPaymentProperties;
    private final EncryptorUtils encryptorUtils;


    @Override
    public CreditCardPaymentResponseDTO execute(CreditCardPaymentRequestDTO request) {
        Transaction transaction = getTransaction(request);
        String redisPrivateKey = getPrivateKeyRedis(request);

        try {
           String paymentDataDecrypted = CardDecryptorUtils.decryptCardData(redisPrivateKey,
                   request.getEncryptedCardData());
           Gson gson = new Gson();
            PaymentDataDTO paymentData = gson.fromJson(paymentDataDecrypted, PaymentDataDTO.class);

            CheckBinInstallment3dsResponseDTO responseCheckData = gson.fromJson(getCheckCardDataRedis(request, paymentData), CheckBinInstallment3dsResponseDTO.class);

            if (responseCheckData.getIsUse3ds()) {
                var request3ds = build3dsPaymentRequest(transaction, paymentData, request);
                var response3ds = hitToCore3dsPayment(request, request3ds);
                storeDataPaymentInfoRedis(paymentData, request);
                return CreditCardPaymentResponseDTO.builder()
                        .status(response3ds.getStatus())
                        .url(response3ds.getUrl())
                        .build();
            } else {
                var requestCharge = buildChargeRequest(transaction, paymentData);
                var responseCharge = hitToCoreCharge(request, requestCharge);

                if (responseCharge.getStatus().equals(TransactionStatusEnum.SUCCESS.name())){
                    transaction.setStatus(TransactionStatusEnum.ON_HOLD.name());
                    transaction.setUpdatedDate(new Date());
                    transactionRepository.save(transaction);

                    return CreditCardPaymentResponseDTO.builder()
                            .status(TransactionStatusEnum.SUCCESS.name())
                            .build();
                }
            }

        } catch (Exception e){
            throw new InvalidDataException(ResponseMessageEnum.TRX_SECURITY_ERROR.getMessage());
        }
        throw new InvalidDataException(ResponseMessageEnum.TRX_SECURITY_ERROR.getMessage());
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

    private ThreeDoSecurePaymentRequestDTO build3dsPaymentRequest(Transaction transaction,
                                                              PaymentDataDTO paymentDataDTO,
                                                                  CreditCardPaymentRequestDTO request) {
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("callbackUrlSuccess", buildCallbackUrl(request, "success"));
        additionalData.put("callbackUrlFailed", buildCallbackUrl(request, "failed"));

        return ThreeDoSecurePaymentRequestDTO.builder()
                .transaction(CreditCardChargeRequestDTO.Transaction.builder()
                        .amount(transaction.getAmount())
                        .currency(transaction.getCurrency())
                        .invoiceNumber(transaction.getInvoiceNumber())
                        .build())
                .requestTimestamp(DateTimeUtils.convertDateToString(new Date()))
                .uuid(UUID.randomUUID().toString())
                .cardData(paymentDataDTO)
                .additionalInfo(additionalData)
                .build();
    }

    private Transaction getTransaction(CreditCardPaymentRequestDTO request) {
        return transactionRepository.findFirstByTokenIdAndRequestIdAndClient_ClientId(request.getTokenId(),
                        request.getRequestId(), request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private String getPrivateKeyRedis(CreditCardPaymentRequestDTO request){
        String redisKey = RedisKeyEnum.GENERATE_RSA_CC.getKey()
                .formatted(request.getClientId(), request.getTokenId(), request.getRequestId());

        String data = redisTemplate.opsForValue().get(redisKey);

        if (data == null) {
            log.debug("redis data with key {} not found", redisKey);
            throw new InvalidDataException(ResponseMessageEnum.TRX_SECURITY_ERROR.getMessage());
        }
        return encryptorUtils.decrypt(data);
    }

    private String getCheckCardDataRedis(CreditCardPaymentRequestDTO request, PaymentDataDTO paymentData) {
        String redisKey = RedisKeyEnum.FRICTIONLESS_3DS_CHECK.getKey().formatted(request.getClientId(), request.getTokenId(), request.getRequestId(), paymentData.getCardNumber().substring(0, 6));
        String data = redisTemplate.opsForValue().get(redisKey);
        if (data == null) {
            log.debug("redis data with key {} not found", redisKey);
            throw new InvalidDataException(ResponseMessageEnum.TRX_SECURITY_ERROR.getMessage());
        }
        return data;
    }

    private CreditCardChargeResponseDTO hitToCoreCharge(CreditCardPaymentRequestDTO request, CreditCardChargeRequestDTO requestToCore) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", request.getRequestId());
        headers.set("Request-Id", request.getRequestId());

        String response = restService.httpPostWithHeader(creditCardPaymentProperties.getCreditCardChargeUrl(), requestToCore, headers);
        return new Gson().fromJson(response, CreditCardChargeResponseDTO.class);
    }

    private CreditCardChargeResponseDTO hitToCore3dsPayment(CreditCardPaymentRequestDTO request, ThreeDoSecurePaymentRequestDTO requestToCore) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", request.getRequestId());
        headers.set("Request-Id", request.getRequestId());

        String response = restService.httpPostWithHeader(creditCardPaymentProperties.getCreditCard3dsCoreUrl(), requestToCore, headers);
        return new Gson().fromJson(response, CreditCardChargeResponseDTO.class);
    }

    private String buildCallbackUrl(CreditCardPaymentRequestDTO request, String status) {
        String callbackUrl = creditCardPaymentProperties.getCallbackUrl();
        callbackUrl = callbackUrl.replace("{tokenId}", request.getTokenId());
        String queryParam = String.format("?client_id=%s&request_id=%s&status=%s",
                request.getClientId(), request.getRequestId(), status);
        return callbackUrl + queryParam;

    }

    private void storeDataPaymentInfoRedis(PaymentDataDTO paymentData, CreditCardPaymentRequestDTO request) {
        String redisKey = String.format(RedisKeyEnum.CHARGE_CC.getKey(), request.getClientId(), request.getTokenId(), request.getRequestId());
        String toEncryptData = encryptorUtils.encrypt(new Gson().toJson(paymentData));
        log.debug("store data redis charge");
        redisTemplate.opsForValue().set(redisKey, toEncryptData, RedisKeyEnum.CHARGE_CC.getExpiredSeconds(),
                TimeUnit.SECONDS);
    }
}
