package net.ryzen.paylinksystem.enums;

import lombok.Getter;

@Getter
public enum RedisKeyEnum {
    GENERATE_ORDER("PAYLINK::MIDDLE::%s::%s::%s", 86400);

    private final String key;
    private final Integer expiredSeconds;
    RedisKeyEnum(String key, Integer expiredSeconds) {
        this.key = key;
        this.expiredSeconds = expiredSeconds;
    }
}
