package net.ryzen.paylinksystem.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptorUtils {

    @Value("${encryption.aes.key}")
    private String key;

    @Value("${encryption.aes.iv}")
    private String iv;

    public String encrypt(String data) {
        if (data == null) {
            return null;
        }
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
            SecretKeySpec skeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(data.toString().getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException("Error encrypting data", ex);
        }
    }

    public Long decrypt(String encryptedId) {
        if (encryptedId == null || encryptedId.isEmpty()) {
            return null;
        }
        try {
            String decodedParam = encryptedId.replace(" ", "+");
            
            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
            SecretKeySpec skeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(decodedParam));
            return Long.parseLong(new String(original));
        } catch (Exception ex) {
            throw new RuntimeException("Error decrypting data", ex);
        }
    }
}
