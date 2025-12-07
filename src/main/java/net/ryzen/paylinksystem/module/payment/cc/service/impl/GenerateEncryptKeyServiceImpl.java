package net.ryzen.paylinksystem.module.payment.cc.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.common.utils.RsaUtils;
import net.ryzen.paylinksystem.enums.RedisKeyEnum;
import net.ryzen.paylinksystem.enums.ResponseMessageEnum;
import net.ryzen.paylinksystem.exception.DataNotFoundException;
import net.ryzen.paylinksystem.module.payment.cc.dto.request.GenerateEncryptKeyRequestDTO;
import net.ryzen.paylinksystem.module.payment.cc.dto.response.GenerateEncryptKeyResponseDTO;
import net.ryzen.paylinksystem.module.payment.cc.service.contract.GenerateEncryptKeyService;
import net.ryzen.paylinksystem.repository.TransactionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateEncryptKeyServiceImpl implements GenerateEncryptKeyService {
    private final TransactionRepository transactionRepository;
    private final RedisTemplate<String, String> redisTemplate;
    @Override
    public GenerateEncryptKeyResponseDTO execute(GenerateEncryptKeyRequestDTO request) {
        validateTrx(request);
        try {
            RsaUtils rsaUtils = new RsaUtils(2048);
            storeDataKeyTrxRedis(request, rsaUtils.getPrivateKey());
            return GenerateEncryptKeyResponseDTO.builder()
                    .publicKey(rsaUtils.getPublicKey())
                    .build();
        } catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(ResponseMessageEnum.FAILED_GENERATE_RSA.getMessage());
        }
    }

    private void validateTrx(GenerateEncryptKeyRequestDTO request) {
        transactionRepository.findFirstByTokenIdAndRequestIdAndClient_ClientId(request.getTokenId(),
                        request.getRequestId(), request.getClientId())
                .orElseThrow(() -> new DataNotFoundException(ResponseMessageEnum.DATA_NOT_FOUND.getMessage()));
    }

    private void storeDataKeyTrxRedis(GenerateEncryptKeyRequestDTO request, String privateKey) {
        String redisKey = RedisKeyEnum.GENERATE_RSA_CC.getKey()
                .formatted(request.getClientId(), request.getTokenId(), request.getRequestId());
        log.debug("storeDataKeyTrxRedis key {}", redisKey);
        redisTemplate.opsForValue().set(
                redisKey, privateKey, RedisKeyEnum.GENERATE_RSA_CC.getExpiredSeconds(), TimeUnit.SECONDS);
    }
}
