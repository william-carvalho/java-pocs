package com.example.socialmediaphotoapp.repository;

import com.example.socialmediaphotoapp.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPhotoPostIdAndDeletedFalseOrderByCreatedAtAsc(Long photoPostId);
    long countByPhotoPostIdAndDeletedFalse(Long photoPostId);
}

