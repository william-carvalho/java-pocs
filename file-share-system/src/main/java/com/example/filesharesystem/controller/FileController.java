package com.example.filesharesystem.controller;

import com.example.filesharesystem.dto.FileMetadataResponse;
import com.example.filesharesystem.dto.FileUploadResponse;
import com.example.filesharesystem.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileService.upload(file));
    }

    @GetMapping
    public ResponseEntity<List<FileMetadataResponse>> listAll() {
        return ResponseEntity.ok(fileService.listAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FileMetadataResponse>> search(@RequestParam("name") String name) {
        return ResponseEntity.ok(fileService.searchByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileMetadataResponse> getMetadata(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getMetadata(id));
    }

    @GetMapping("/{id}/restore")
    public ResponseEntity<byte[]> restore(@PathVariable Long id) {
        FileService.RestoredFile restoredFile = fileService.restore(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + restoredFile.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(restoredFile.getContentType()))
                .body(restoredFile.getContent());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
