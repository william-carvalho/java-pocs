package com.example.socialmediaphotoapp.controller;

import com.example.socialmediaphotoapp.dto.AddCommentRequest;
import com.example.socialmediaphotoapp.dto.AddTagsRequest;
import com.example.socialmediaphotoapp.dto.CommentResponse;
import com.example.socialmediaphotoapp.dto.PhotoPostResponse;
import com.example.socialmediaphotoapp.entity.PhotoPost;
import com.example.socialmediaphotoapp.service.CommentService;
import com.example.socialmediaphotoapp.service.PhotoService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService photoService;
    private final CommentService commentService;

    public PhotoController(PhotoService photoService, CommentService commentService) {
        this.photoService = photoService;
        this.commentService = commentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoPostResponse> publish(@RequestPart("file") MultipartFile file,
                                                     @RequestParam("userId") Long userId,
                                                     @RequestParam(value = "caption", required = false) String caption) {
        return ResponseEntity.ok(photoService.publish(userId, caption, file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotoPostResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(photoService.findById(id));
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<byte[]> content(@PathVariable Long id) {
        PhotoPost photoPost = photoService.findActiveEntity(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photoPost.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(photoPost.getContentType()))
                .body(photoService.content(id));
    }

    @PostMapping("/{id}/tags")
    public ResponseEntity<PhotoPostResponse> addTags(@PathVariable Long id, @Valid @org.springframework.web.bind.annotation.RequestBody AddTagsRequest request) {
        return ResponseEntity.ok(photoService.addTags(id, request));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> comments(@PathVariable Long id) {
        photoService.findActiveEntity(id);
        return ResponseEntity.ok(commentService.listByPhoto(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long id, @Valid @org.springframework.web.bind.annotation.RequestBody AddCommentRequest request) {
        PhotoPost photoPost = photoService.findActiveEntity(id);
        return ResponseEntity.ok(commentService.addComment(photoPost, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        photoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

