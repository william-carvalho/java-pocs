package com.example.socialmediaphotoapp.service;

import com.example.socialmediaphotoapp.dto.AddTagsRequest;
import com.example.socialmediaphotoapp.dto.PhotoPostResponse;
import com.example.socialmediaphotoapp.entity.PhotoPost;
import com.example.socialmediaphotoapp.entity.Tag;
import com.example.socialmediaphotoapp.exception.ResourceNotFoundException;
import com.example.socialmediaphotoapp.repository.PhotoPostRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {

    private final PhotoPostRepository photoPostRepository;
    private final UserService userService;
    private final TagService tagService;
    private final CommentService commentService;
    private final LocalFileStorageService localFileStorageService;

    public PhotoService(PhotoPostRepository photoPostRepository,
                        UserService userService,
                        TagService tagService,
                        CommentService commentService,
                        LocalFileStorageService localFileStorageService) {
        this.photoPostRepository = photoPostRepository;
        this.userService = userService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.localFileStorageService = localFileStorageService;
    }

    @Transactional
    public PhotoPostResponse publish(Long userId, String caption, MultipartFile file) {
        validateImage(file);

        LocalFileStorageService.StoredFileData storedFileData = localFileStorageService.store(file);

        PhotoPost photoPost = new PhotoPost();
        photoPost.setUser(userService.findEntity(userId));
        photoPost.setCaption(caption);
        photoPost.setOriginalFileName(storedFileData.getOriginalFileName());
        photoPost.setStoredFileName(storedFileData.getStoredFileName());
        photoPost.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        photoPost.setStoragePath(storedFileData.getStoragePath());
        photoPost.setDeleted(Boolean.FALSE);

        return toResponse(photoPostRepository.save(photoPost));
    }

    @Transactional(readOnly = true)
    public PhotoPost findActiveEntity(Long id) {
        return photoPostRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id " + id));
    }

    @Transactional(readOnly = true)
    public PhotoPostResponse findById(Long id) {
        return toResponse(findActiveEntity(id));
    }

    @Transactional
    public PhotoPostResponse addTags(Long photoId, AddTagsRequest request) {
        PhotoPost photoPost = findActiveEntity(photoId);
        Set<Tag> tags = tagService.resolveTags(request.getTags());
        photoPost.getTags().addAll(tags);
        return toResponse(photoPostRepository.save(photoPost));
    }

    @Transactional
    public void delete(Long id) {
        PhotoPost photoPost = findActiveEntity(id);
        photoPost.setDeleted(Boolean.TRUE);
        photoPostRepository.save(photoPost);
    }

    @Transactional(readOnly = true)
    public byte[] content(Long id) {
        return localFileStorageService.read(findActiveEntity(id).getStoragePath());
    }

    @Transactional(readOnly = true)
    public List<PhotoPost> listForTimeline(Long userId, String tag) {
        if (userId != null && tag != null && !tag.trim().isEmpty()) {
            return photoPostRepository.findByUserIdAndTag(userId, tag.trim());
        }
        if (userId != null) {
            return photoPostRepository.findByDeletedFalseAndUserIdOrderByCreatedAtDesc(userId);
        }
        if (tag != null && !tag.trim().isEmpty()) {
            return photoPostRepository.findByTag(tag.trim());
        }
        return photoPostRepository.findByDeletedFalseOrderByCreatedAtDesc();
    }

    public PhotoPostResponse toResponse(PhotoPost photoPost) {
        PhotoPostResponse response = new PhotoPostResponse();
        response.setId(photoPost.getId());
        response.setUser(userService.toResponse(photoPost.getUser()));
        response.setCaption(photoPost.getCaption());
        response.setOriginalFileName(photoPost.getOriginalFileName());
        response.setContentType(photoPost.getContentType());
        response.setCreatedAt(photoPost.getCreatedAt());
        response.setTags(tagService.toNames(photoPost.getTags()));
        response.setCommentsCount(commentService.countByPhoto(photoPost.getId()));
        return response;
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file must not be empty");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("file contentType must be an image");
        }
    }
}

