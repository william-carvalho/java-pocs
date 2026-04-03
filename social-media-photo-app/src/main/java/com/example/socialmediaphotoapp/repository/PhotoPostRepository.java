package com.example.socialmediaphotoapp.repository;

import com.example.socialmediaphotoapp.entity.PhotoPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhotoPostRepository extends JpaRepository<PhotoPost, Long> {

    Optional<PhotoPost> findByIdAndDeletedFalse(Long id);

    List<PhotoPost> findByDeletedFalseOrderByCreatedAtDesc();

    List<PhotoPost> findByDeletedFalseAndUserIdOrderByCreatedAtDesc(Long userId);

    @Query("select distinct p from PhotoPost p join p.tags t where p.deleted = false and lower(t.name) = lower(:tag) order by p.createdAt desc")
    List<PhotoPost> findByTag(@Param("tag") String tag);

    @Query("select distinct p from PhotoPost p join p.tags t where p.deleted = false and p.user.id = :userId and lower(t.name) = lower(:tag) order by p.createdAt desc")
    List<PhotoPost> findByUserIdAndTag(@Param("userId") Long userId, @Param("tag") String tag);
}

