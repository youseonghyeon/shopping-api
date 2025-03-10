package com.shop.shoppingapi.security.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RsaUtils {

    private final Path rsaPrivateKeyPath;
    private PrivateKey rsaPrivateKeyCache;

    public RsaUtils(String rsaPrivateKeyPath) {
        this.rsaPrivateKeyPath = Paths.get(rsaPrivateKeyPath);
        if (!Files.exists(this.rsaPrivateKeyPath) || !Files.isReadable(this.rsaPrivateKeyPath)) {
            throw new IllegalStateException("RSA Private Key 파일이 존재하지 않거나 읽을 수 없습니다: " + rsaPrivateKeyPath);
        }
    }

    public PrivateKey getPrivateKey() {
        if (rsaPrivateKeyCache != null) {
            return rsaPrivateKeyCache;
        }

        try {
            // 키 파일 읽고, 헤더 및 공백 제거
            String keyContent = Files.readString(rsaPrivateKeyPath)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("\n", "")
                    .replace("\r", "");

            byte[] keyBytes = Base64.getDecoder().decode(keyContent);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            rsaPrivateKeyCache = keyFactory.generatePrivate(spec);
            return rsaPrivateKeyCache;
        } catch (Exception e) {
            throw new RuntimeException("RSA Private Key 로드 실패", e);
        }
    }

    public String decrypt(String encryptedData) throws Exception {
        PrivateKey privateKey = getPrivateKey();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // PKCS1Padding 사용
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
