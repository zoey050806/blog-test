package com.blog.controller;

import com.blog.dto.CommentForm;
import com.blog.entity.Comment;
import com.blog.entity.Post;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.CommentService;
import com.blog.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        Post post = postService.getPublishedPostBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Post", "slug", slug));

        // Increment view count
        postService.incrementViewCount(post.getId());
        post.setViewCount(post.getViewCount() + 1); // reflect in current view

        List<Comment> comments = commentService.getApprovedCommentsByPostId(post.getId());

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", new CommentForm());

        if (!model.containsAttribute("categories")) {
            model.addAttribute("categories", List.of());
        }

        return "post/detail";
    }

    @PostMapping("/{slug}/comments")
    public String submitComment(@PathVariable String slug,
                                @Valid @ModelAttribute("commentForm") CommentForm form,
                                BindingResult result,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        Post post = postService.getPublishedPostBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Post", "slug", slug));

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.commentForm", result);
            redirectAttributes.addFlashAttribute("commentForm", form);
            redirectAttributes.addFlashAttribute("commentError", "Please fix the errors in your comment.");
            return "redirect:/posts/" + slug;
        }

        String ipAddress = getClientIp(request);
        commentService.submitComment(post.getId(), form, ipAddress);

        redirectAttributes.addFlashAttribute("commentSuccess",
            "Your comment has been submitted and is pending approval.");
        return "redirect:/posts/" + slug;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
