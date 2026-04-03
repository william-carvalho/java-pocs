package com.example.filesharesystem.service;

import com.example.filesharesystem.dto.FileMetadataResponse;
import com.example.filesharesystem.dto.FileUploadResponse;
import com.example.filesharesystem.entity.StoredFile;
import com.example.filesharesystem.exception.ResourceNotFoundException;
import com.example.filesharesystem.exception.StorageException;
import com.example.filesharesystem.repository.StoredFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final StoredFileRepository storedFileRepository;
    private final FileStorageService fileStorageService;

    public FileService(StoredFileRepository storedFileRepository, FileStorageService fileStorageService) {
        this.storedFileRepository = storedFileRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public FileUploadResponse upload(MultipartFile file) {
        validateFile(file);

        try {
            String originalFileName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String storedFileName = fileStorageService.generateStoredFileName(originalFileName);
            String storagePath = fileStorageService.storeEncrypted(storedFileName, file.getBytes());

            StoredFile storedFile = new StoredFile();
            storedFile.setOriginalFileName(originalFileName);
            storedFile.setStoredFileName(storedFileName);
            storedFile.setContentType(resolveContentType(file.getContentType()));
            storedFile.setSize(file.getSize());
            storedFile.setEncrypted(Boolean.TRUE);
            storedFile.setStoragePath(storagePath);
            storedFile.setDeleted(Boolean.FALSE);

            StoredFile savedFile = storedFileRepository.save(storedFile);
            return toUploadResponse(savedFile);
        } catch (IOException ex) {
            throw new StorageException("Failed to read uploaded file", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<FileMetadataResponse> listAll() {
        return storedFileRepository.findByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::toMetadataResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FileMetadataResponse getMetadata(Long id) {
        return toMetadataResponse(findActiveById(id));
    }

    @Transactional(readOnly = true)
    public List<FileMetadataResponse> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name query param is required");
        }

        return storedFileRepository.findByDeletedFalseAndOriginalFileNameContainingIgnoreCaseOrderByCreatedAtDesc(name.trim())
                .stream()
                .map(this::toMetadataResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RestoredFile restore(Long id) {
        StoredFile storedFile = findActiveById(id);
        byte[] content = fileStorageService.restoreDecrypted(storedFile.getStoragePath());
        return new RestoredFile(
                storedFile.getOriginalFileName(),
                resolveContentType(storedFile.getContentType()),
                content
        );
    }

    @Transactional
    public void delete(Long id) {
        StoredFile storedFile = findActiveById(id);
        storedFile.setDeleted(Boolean.TRUE);
        storedFileRepository.save(storedFile);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file must not be empty");
        }
    }

    private StoredFile findActiveById(Long id) {
        return storedFileRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id " + id));
    }

    private String resolveContentType(String contentType) {
        return contentType == null || contentType.trim().isEmpty()
                ? "application/octet-stream"
                : contentType;
    }

    private FileUploadResponse toUploadResponse(StoredFile storedFile) {
        FileUploadResponse response = new FileUploadResponse();
        response.setId(storedFile.getId());
        response.setOriginalFileName(storedFile.getOriginalFileName());
        response.setContentType(storedFile.getContentType());
        response.setSize(storedFile.getSize());
        response.setEncrypted(storedFile.getEncrypted());
        response.setCreatedAt(storedFile.getCreatedAt());
        return response;
    }

    private FileMetadataResponse toMetadataResponse(StoredFile storedFile) {
        FileMetadataResponse response = new FileMetadataResponse();
        response.setId(storedFile.getId());
        response.setOriginalFileName(storedFile.getOriginalFileName());
        response.setContentType(storedFile.getContentType());
        response.setSize(storedFile.getSize());
        response.setEncrypted(storedFile.getEncrypted());
        response.setCreatedAt(storedFile.getCreatedAt());
        return response;
    }

    public static class RestoredFile {
        private final String originalFileName;
        private final String contentType;
        private final byte[] content;

        public RestoredFile(String originalFileName, String contentType, byte[] content) {
            this.originalFileName = originalFileName;
            this.contentType = contentType;
            this.content = content;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getContentType() {
            return contentType;
        }

        public byte[] getContent() {
            return content;
        }
    }
}

