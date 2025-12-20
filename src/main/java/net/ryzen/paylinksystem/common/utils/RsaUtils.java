package net.ryzen.paylinksystem.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RsaUtils {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private KeyPair keyPair;

    public RsaUtils(Integer length) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(length);
        keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public String getPublicKey(){
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(publicKey.getEncoded());
    }

    public String getPrivateKey(){
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(privateKey.getEncoded());
    }
}