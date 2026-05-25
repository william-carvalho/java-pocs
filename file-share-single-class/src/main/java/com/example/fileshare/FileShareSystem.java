package com.example.fileshare;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class FileShareSystem {
    private static final String AES = "AES";
    private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private final Map<Long, StoredFile> filesById = new LinkedHashMap<Long, StoredFile>();
    private final SecretKeySpec secretKey;
    private long nextId = 1;

    public FileShareSystem(String encryptionKey) {
        this.secretKey = new SecretKeySpec(normalizeKey(encryptionKey), AES);
    }

    public StoredFile saveFile(String originalFileName, String contentType, byte[] content) {
        if (isBlank(originalFileName)) {
            throw new IllegalArgumentException("originalFileName is required");
        }
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("content is required");
        }

        byte[] encryptedContent = encrypt(content);
        StoredFile storedFile = new StoredFile(
                nextId++,
                originalFileName.trim(),
                contentType == null ? "application/octet-stream" : contentType.trim(),
                content.length,
                UUID.randomUUID().toString(),
                encryptedContent);
        filesById.put(storedFile.getId(), storedFile);
        return storedFile;
    }

    public byte[] restoreFile(long id) {
        StoredFile storedFile = findActiveFile(id);
        return decrypt(storedFile.encryptedContent);
    }

    public StoredFile deleteFile(long id) {
        StoredFile storedFile = findActiveFile(id);
        storedFile.markDeleted();
        return storedFile;
    }

    public List<StoredFile> listFiles() {
        List<StoredFile> files = new ArrayList<StoredFile>();
        for (StoredFile file : filesById.values()) {
            if (!file.isDeleted()) {
                files.add(file);
            }
        }
        return Collections.unmodifiableList(files);
    }

    public List<StoredFile> search(String query) {
        if (isBlank(query)) {
            return listFiles();
        }

        String normalizedQuery = query.trim().toLowerCase(Locale.ENGLISH);
        List<StoredFile> matches = new ArrayList<StoredFile>();
        for (StoredFile file : listFiles()) {
            if (file.getOriginalFileName().toLowerCase(Locale.ENGLISH).contains(normalizedQuery)
                    || file.getContentType().toLowerCase(Locale.ENGLISH).contains(normalizedQuery)) {
                matches.add(file);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    public StoredFile getFile(long id) {
        return findActiveFile(id);
    }

    public byte[] encryptedBytes(long id) {
        StoredFile storedFile = filesById.get(id);
        if (storedFile == null) {
            throw new IllegalArgumentException("File not found: " + id);
        }
        return Arrays.copyOf(storedFile.encryptedContent, storedFile.encryptedContent.length);
    }

    private StoredFile findActiveFile(long id) {
        StoredFile storedFile = filesById.get(id);
        if (storedFile == null || storedFile.isDeleted()) {
            throw new IllegalArgumentException("File not found: " + id);
        }
        return storedFile;
    }

    private byte[] encrypt(byte[] plainBytes) {
        return crypt(Cipher.ENCRYPT_MODE, plainBytes);
    }

    private byte[] decrypt(byte[] encryptedBytes) {
        return crypt(Cipher.DECRYPT_MODE, encryptedBytes);
    }

    private byte[] crypt(int mode, byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(mode, secretKey);
            return cipher.doFinal(bytes);
        } catch (Exception ex) {
            throw new IllegalStateException("Encryption operation failed", ex);
        }
    }

    private static byte[] normalizeKey(String encryptionKey) {
        if (isBlank(encryptionKey)) {
            throw new IllegalArgumentException("encryptionKey is required");
        }

        byte[] source = encryptionKey.getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[16];
        for (int index = 0; index < key.length; index++) {
            key[index] = index < source.length ? source[index] : 0;
        }
        return key;
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static final class StoredFile {
        private final long id;
        private final String originalFileName;
        private final String contentType;
        private final long originalSize;
        private final String storageName;
        private final byte[] encryptedContent;
        private final LocalDateTime createdAt;
        private LocalDateTime deletedAt;
        private boolean deleted;

        private StoredFile(long id,
                           String originalFileName,
                           String contentType,
                           long originalSize,
                           String storageName,
                           byte[] encryptedContent) {
            this.id = id;
            this.originalFileName = originalFileName;
            this.contentType = contentType;
            this.originalSize = originalSize;
            this.storageName = storageName;
            this.encryptedContent = Arrays.copyOf(encryptedContent, encryptedContent.length);
            this.createdAt = LocalDateTime.now();
        }

        private void markDeleted() {
            this.deleted = true;
            this.deletedAt = LocalDateTime.now();
        }

        public long getId() {
            return id;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getContentType() {
            return contentType;
        }

        public long getOriginalSize() {
            return originalSize;
        }

        public String getStorageName() {
            return storageName;
        }

        public boolean isEncrypted() {
            return true;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getDeletedAt() {
            return deletedAt;
        }
    }
}
