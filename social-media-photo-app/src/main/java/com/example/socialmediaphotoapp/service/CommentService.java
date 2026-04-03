package com.example.socialmediaphotoapp.service;

import com.example.socialmediaphotoapp.dto.AddCommentRequest;
import com.example.socialmediaphotoapp.dto.CommentResponse;
import com.example.socialmediaphotoapp.entity.Comment;
import com.example.socialmediaphotoapp.entity.PhotoPost;
import com.example.socialmediaphotoapp.repository.CommentRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    @Transactional
    public CommentResponse addComment(PhotoPost photoPost, AddCommentRequest request) {
        Comment comment = new Comment();
        comment.setPhotoPost(photoPost);
        comment.setUser(userService.findEntity(request.getUserId()));
        comment.setText(request.getText().trim());
        return toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listByPhoto(Long photoId) {
        return commentRepository.findByPhotoPostIdAndDeletedFalseOrderByCreatedAtAsc(photoId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int countByPhoto(Long photoId) {
        return (int) commentRepository.countByPhotoPostIdAndDeletedFalse(photoId);
    }

    public CommentResponse toResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPhotoId(comment.getPhotoPost().getId());
        response.setUser(userService.toResponse(comment.getUser()));
        response.setText(comment.getText());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}

