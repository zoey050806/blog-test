package com.blog.repository;

import com.blog.entity.Post;
import com.blog.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Public-facing queries (only PUBLISHED posts)
    Optional<Post> findBySlugAndStatus(String slug, PostStatus status);

    Page<Post> findByStatusOrderByIsTopDescCreatedAtDesc(PostStatus status, Pageable pageable);

    Page<Post> findByCategory_SlugAndStatus(String categorySlug, PostStatus status, Pageable pageable);

    Page<Post> findByTags_SlugAndStatus(String tagSlug, PostStatus status, Pageable pageable);

    // Admin queries (all statuses)
    Page<Post> findAllByOrderByIsTopDescCreatedAtDesc(Pageable pageable);

    Optional<Post> findBySlug(String slug);

    // Full-text search (native query using MySQL FULLTEXT index)
    @Query(value = "SELECT * FROM posts WHERE status = 'PUBLISHED' AND MATCH(title, content) AGAINST(?1 IN BOOLEAN MODE)",
           countQuery = "SELECT count(*) FROM posts WHERE status = 'PUBLISHED' AND MATCH(title, content) AGAINST(?1 IN BOOLEAN MODE)",
           nativeQuery = true)
    Page<Post> search(String keyword, Pageable pageable);

    // Simple LIKE-based search fallback (if FULLTEXT not available)
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchByKeyword(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

    // Recent posts for sidebar
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts(Pageable pageable);

    // Count queries for dashboard
    long countByStatus(PostStatus status);

    boolean existsBySlug(String slug);
}
