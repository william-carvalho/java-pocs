package com.example.filesharesystem.service;

import com.example.filesharesystem.config.StorageProperties;
import com.example.filesharesystem.exception.StorageException;
import com.example.filesharesystem.util.FileNameUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageRootPath;
    private final EncryptionService encryptionService;

    public FileStorageService(StorageProperties storageProperties, EncryptionService encryptionService) {
        this.storageRootPath = Paths.get(storageProperties.getStorage().getPath()).toAbsolutePath().normalize();
        this.encryptionService = encryptionService;
        initializeStorage();
    }

    public String generateStoredFileName(String originalFileName) {
        return UUID.randomUUID().toString() + FileNameUtils.extractExtension(originalFileName);
    }

    public String storeEncrypted(String storedFileName, byte[] content) {
        try {
            byte[] encryptedContent = encryptionService.encrypt(content);
            Path targetPath = storageRootPath.resolve(storedFileName).normalize();
            Files.write(targetPath, encryptedContent);
            return targetPath.toString();
        } catch (IOException ex) {
            throw new StorageException("Failed to store encrypted file", ex);
        }
    }

    public byte[] restoreDecrypted(String storagePath) {
        try {
            byte[] encryptedContent = Files.readAllBytes(Paths.get(storagePath));
            return encryptionService.decrypt(encryptedContent);
        } catch (IOException ex) {
            throw new StorageException("Failed to read stored file", ex);
        }
    }

    private void initializeStorage() {
        try {
            Files.createDirectories(storageRootPath);
        } catch (IOException ex) {
            throw new StorageException("Failed to initialize storage directory", ex);
        }
    }
}

