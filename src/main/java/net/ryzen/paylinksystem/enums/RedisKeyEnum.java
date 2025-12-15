package net.ryzen.paylinksystem.enums;

import lombok.Getter;

@Getter
public enum RedisKeyEnum {
    GENERATE_ORDER("PAYLINK::MIDDLE::%s::%s::%s", 86400),
    GENERATE_RSA_CC("PAYLINK::MIDDLE::RSA::%s::%s::%s", 300),
    CHARGE_CC("PAYLINK::MIDDLE::CHARGE::%s::%s::%s", 300),
    BANK_TRANSFER_DATA("PAYLINK::MIDDLE::BANK_TRANSFER::%s::%s::%s::%s", 3600),
    FRICTIONLESS_3DS_CHECK("PAYLINK::MIDDLE::CARD_DATA::%s::%s::%s::%s", 300);

    private final String key;
    private final Integer expiredSeconds;
    RedisKeyEnum(String key, Integer expiredSeconds) {
        this.key = key;
        this.expiredSeconds = expiredSeconds;
    }
}
