package com.dan.sd.user_management.security;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Getter
public class Keys {
    private static final Logger LOGGER = LoggerFactory.getLogger(Keys.class);

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public Keys(@Value("${jwt.public.key}") String publicKeyStr,
                @Value("${jwt.private.key}") String privateKeyStr) {
        this.publicKey = convertToPublicKey(publicKeyStr);
        this.privateKey = convertToPrivateKey(privateKeyStr);

        LOGGER.info("Public Key set: {}", publicKey);
        LOGGER.info("Private Key set: {}", privateKey);
    }

    private PublicKey convertToPublicKey(String key) {
        try {
            String publicKeyPEM = key.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid public key provided", e);
        }
    }

    private PrivateKey convertToPrivateKey(String key) {
        try {
            String privateKeyPEM = key.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid private key provided", e);
        }
    }
}
