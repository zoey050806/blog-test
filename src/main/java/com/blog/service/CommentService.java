package com.blog.service;

import com.blog.dto.CommentForm;
import com.blog.entity.Comment;
import com.blog.entity.CommentStatus;
import com.blog.entity.Post;
import com.blog.entity.PostStatus;
import com.blog.repository.CommentRepository;
import com.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<Comment> getApprovedCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdAndParentIsNullAndStatusOrderByCreatedAtAsc(
            postId, CommentStatus.APPROVED);
    }

    @Transactional
    public Comment submitComment(Long postId, CommentForm form, String ipAddress) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new RuntimeException("Cannot comment on an unpublished post");
        }

        Comment comment = Comment.builder()
            .post(post)
            .authorName(form.getAuthorName())
            .authorEmail(form.getAuthorEmail())
            .content(form.getContent())
            .status(CommentStatus.PENDING)
            .ipAddress(ipAddress)
            .build();

        // Handle reply
        if (form.getParentId() != null) {
            Comment parent = commentRepository.findById(form.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParent(parent);
        }

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment approveComment(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        comment.setStatus(CommentStatus.APPROVED);
        return commentRepository.save(comment);
    }

    @Transactional
    public void markAsSpam(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        comment.setStatus(CommentStatus.SPAM);
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        commentRepository.delete(comment);
    }

    public Page<Comment> getPendingComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findByStatusOrderByCreatedAtDesc(CommentStatus.PENDING, pageable);
    }

    public Page<Comment> getAllComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findAllOrderByCreatedAtDesc(pageable);
    }

    public long getPendingCommentCount() {
        return commentRepository.countByStatus(CommentStatus.PENDING);
    }

    public long getTotalCommentCount() {
        return commentRepository.count();
    }
}
