package com.blog.service;

import com.blog.dto.PostForm;
import com.blog.entity.*;
import com.blog.repository.CategoryRepository;
import com.blog.repository.PostRepository;
import com.blog.repository.TagRepository;
import com.blog.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final FileStorageService fileStorageService;

    // ===== Public queries =====

    public Page<Post> getPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByStatusOrderByIsTopDescCreatedAtDesc(PostStatus.PUBLISHED, pageable);
    }

    public Optional<Post> getPublishedPostBySlug(String slug) {
        Optional<Post> postOpt = postRepository.findBySlugAndStatus(slug, PostStatus.PUBLISHED);
        // Increment view count is handled by the controller to avoid side effects here
        return postOpt;
    }

    @Transactional
    public void incrementViewCount(Long postId) {
        postRepository.findById(postId).ifPresent(post -> {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        });
    }

    public Page<Post> getPostsByCategory(String categorySlug, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByCategory_SlugAndStatus(categorySlug, PostStatus.PUBLISHED, pageable);
    }

    public Page<Post> getPostsByTag(String tagSlug, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByTags_SlugAndStatus(tagSlug, PostStatus.PUBLISHED, pageable);
    }

    public Page<Post> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Use LIKE-based search for broader compatibility; switch to native FULLTEXT if needed
        return postRepository.searchByKeyword(keyword, pageable);
    }

    public List<Post> getRecentPosts(int count) {
        return postRepository.findRecentPosts(PageRequest.of(0, count));
    }

    // ===== Admin operations =====

    public Page<Post> getAllPostsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByOrderByIsTopDescCreatedAtDesc(pageable);
    }

    @Transactional
    public Post createPost(PostForm form, User author) {
        Post post = new Post();
        applyFormToPost(form, post);
        post.setAuthor(author);
        post.setStatus(form.getStatus());

        if (post.getStatus() == PostStatus.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        // Generate slug
        String slug = (form.getSlug() != null && !form.getSlug().isBlank())
            ? SlugUtils.toSlug(form.getSlug())
            : generateUniqueSlug(form.getTitle());
        post.setSlug(slug);

        // Set tags
        syncTags(post, form.getTagIds());

        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Long id, PostForm form) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        applyFormToPost(form, post);

        // Regenerate slug if title changed and no custom slug provided
        if (!form.getTitle().equals(post.getTitle()) && (form.getSlug() == null || form.getSlug().isBlank())) {
            post.setSlug(generateUniqueSlug(form.getTitle(), post.getId()));
        } else if (form.getSlug() != null && !form.getSlug().isBlank()) {
            String newSlug = SlugUtils.toSlug(form.getSlug());
            if (!newSlug.equals(post.getSlug()) && postRepository.existsBySlug(newSlug)) {
                throw new RuntimeException("A post with this slug already exists: " + newSlug);
            }
            post.setSlug(newSlug);
        }

        // Set tags
        syncTags(post, form.getTagIds());

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        // Clean up cover image
        if (post.getCoverImage() != null) {
            fileStorageService.delete(post.getCoverImage());
        }

        postRepository.delete(post);
    }

    @Transactional
    public Post publishPost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    public Post archivePost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.setStatus(PostStatus.DRAFT);
        return postRepository.save(post);
    }

    // ===== Slug generation =====

    public String generateUniqueSlug(String title) {
        return generateUniqueSlug(title, null);
    }

    private String generateUniqueSlug(String title, Long excludeId) {
        String baseSlug = SlugUtils.toSlug(title);
        if (baseSlug.isBlank()) {
            baseSlug = "post";
        }

        String slug = baseSlug;
        int suffix = 1;

        while (true) {
            boolean exists;
            if (excludeId != null) {
                Optional<Post> existing = postRepository.findBySlug(slug);
                exists = existing.isPresent() && !existing.get().getId().equals(excludeId);
            } else {
                exists = postRepository.existsBySlug(slug);
            }

            if (!exists) break;

            suffix++;
            slug = baseSlug + "-" + suffix;
        }

        return slug;
    }

    // ===== Helper methods =====

    private void applyFormToPost(PostForm form, Post post) {
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setSummary(form.getSummary());
        post.setIsTop(form.getIsTop() != null && form.getIsTop());

        // Cover image — only update if provided
        if (form.getCoverImage() != null && !form.getCoverImage().isBlank()) {
            post.setCoverImage(form.getCoverImage());
        }

        // Category
        if (form.getCategoryId() != null) {
            Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + form.getCategoryId()));
            post.setCategory(category);
        } else {
            post.setCategory(null);
        }
    }

    private void syncTags(Post post, List<Long> tagIds) {
        if (tagIds == null) {
            return; // no change
        }

        Set<Tag> newTags = new HashSet<>();
        if (!tagIds.isEmpty()) {
            newTags = tagRepository.findAllById(tagIds).stream().collect(Collectors.toSet());
        }

        // Clear existing tags
        post.getTags().clear();
        // Add new tags
        newTags.forEach(post::addTag);
    }
}
