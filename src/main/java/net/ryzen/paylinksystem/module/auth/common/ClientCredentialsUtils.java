package net.ryzen.paylinksystem.module.auth.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientCredentialsUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HHmmssyyyyMMdd");
    private static final Random RANDOM = new Random();

    private static String generateSequence() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(FORMATTER);
        String randomSuffix = String.format("%03d", RANDOM.nextInt(1000));

        return timestamp + randomSuffix;
    }

    public static String generateClientId(String prefix) {
        return prefix + generateSequence();
    }
}
