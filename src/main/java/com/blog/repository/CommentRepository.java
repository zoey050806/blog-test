package com.blog.repository;

import com.blog.entity.Comment;
import com.blog.entity.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, CommentStatus status);

    List<Comment> findByPostIdAndParentIsNullAndStatusOrderByCreatedAtAsc(Long postId, CommentStatus status);

    Page<Comment> findByStatusOrderByCreatedAtDesc(CommentStatus status, Pageable pageable);

    long countByStatus(CommentStatus status);

    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC")
    Page<Comment> findAllOrderByCreatedAtDesc(Pageable pageable);
}
