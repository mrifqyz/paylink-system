package net.ryzen.paylinksystem.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
    private static final Random RANDOM = new Random();
    public static String generateRandString(String prefix, Integer seqCount) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder key = new StringBuilder(prefix);

        for (int i = 0; i < seqCount; i++) {
            int index = RANDOM.nextInt(characters.length());
            key.append(characters.charAt(index));
        }

        return key.toString();
    }
}
