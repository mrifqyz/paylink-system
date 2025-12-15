package net.ryzen.paylinksystem.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardDecryptorUtils {
    public static String decryptCardData(String privateKey, String encryptedCardData) throws Exception {
        RSAPrivateKey rsaPrivateKey = decodeRsaPrivateKey(privateKey);
        String decryptedData = decryptDataUsingPrivateKey(rsaPrivateKey, encryptedCardData);
        log.debug("decryptedData: {}", decryptedData);
        return decryptedData;
    }

    private static String decryptDataUsingPrivateKey(RSAPrivateKey rsaPrivateKey, String encryptedCardData) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] contentByteCipher = Base64.getDecoder().decode(encryptedCardData.getBytes());
        byte[] decryptedByte = cipher.doFinal(contentByteCipher);
        return new String(decryptedByte, StandardCharsets.UTF_8);
    }

    private static RSAPrivateKey decodeRsaPrivateKey(String keyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] encoded = Base64.getDecoder().decode(keyString.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}
