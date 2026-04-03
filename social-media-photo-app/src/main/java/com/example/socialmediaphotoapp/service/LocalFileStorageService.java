package com.example.socialmediaphotoapp.service;

import com.example.socialmediaphotoapp.config.PhotoStorageProperties;
import com.example.socialmediaphotoapp.exception.StorageException;
import com.example.socialmediaphotoapp.util.FileNameUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService {

    private final Path storageRoot;

    public LocalFileStorageService(PhotoStorageProperties properties) {
        this.storageRoot = Paths.get(properties.getPath()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageRoot);
        } catch (IOException ex) {
            throw new StorageException("Failed to initialize photo storage", ex);
        }
    }

    public StoredFileData store(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename() == null ? "photo" : file.getOriginalFilename();
            String storedFileName = UUID.randomUUID().toString() + FileNameUtils.extension(originalFileName);
            Path targetPath = storageRoot.resolve(storedFileName).normalize();
            Files.write(targetPath, file.getBytes());
            return new StoredFileData(originalFileName, storedFileName, targetPath.toString());
        } catch (IOException ex) {
            throw new StorageException("Failed to store photo", ex);
        }
    }

    public byte[] read(String storagePath) {
        try {
            return Files.readAllBytes(Paths.get(storagePath));
        } catch (IOException ex) {
            throw new StorageException("Failed to read stored photo", ex);
        }
    }

    public static class StoredFileData {
        private final String originalFileName;
        private final String storedFileName;
        private final String storagePath;

        public StoredFileData(String originalFileName, String storedFileName, String storagePath) {
            this.originalFileName = originalFileName;
            this.storedFileName = storedFileName;
            this.storagePath = storagePath;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getStoredFileName() {
            return storedFileName;
        }

        public String getStoragePath() {
            return storagePath;
        }
    }
}
