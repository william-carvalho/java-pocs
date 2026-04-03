package com.example.filesharesystem.service;

import com.example.filesharesystem.config.StorageProperties;
import com.example.filesharesystem.exception.EncryptionException;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class EncryptionService {

    private final SecretKeySpec secretKeySpec;

    public EncryptionService(StorageProperties storageProperties) {
        String key = storageProperties.getEncryption().getKey();
        int keyLength = key == null ? 0 : key.length();
        if (keyLength != 16 && keyLength != 24 && keyLength != 32) {
            throw new EncryptionException("file.encryption.key must have 16, 24 or 32 characters");
        }
        this.secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
    }

    public byte[] encrypt(byte[] content) {
        return doCipher(content, Cipher.ENCRYPT_MODE, "Failed to encrypt file content");
    }

    public byte[] decrypt(byte[] content) {
        return doCipher(content, Cipher.DECRYPT_MODE, "Failed to decrypt file content");
    }

    private byte[] doCipher(byte[] content, int cipherMode, String errorMessage) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKeySpec);
            return cipher.doFinal(content);
        } catch (Exception ex) {
            throw new EncryptionException(errorMessage, ex);
        }
    }
}

