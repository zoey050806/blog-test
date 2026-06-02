package com.blog.controller.admin;

import com.blog.entity.PostStatus;
import com.blog.repository.CategoryRepository;
import com.blog.repository.PostRepository;
import com.blog.repository.TagRepository;
import com.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentService commentService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        long totalPosts = postRepository.count();
        long publishedPosts = postRepository.countByStatus(PostStatus.PUBLISHED);
        long draftPosts = postRepository.countByStatus(PostStatus.DRAFT);
        long pendingComments = commentService.getPendingCommentCount();
        long totalComments = commentService.getTotalCommentCount();
        long totalCategories = categoryRepository.count();
        long totalTags = tagRepository.count();

        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("publishedPosts", publishedPosts);
        model.addAttribute("draftPosts", draftPosts);
        model.addAttribute("pendingComments", pendingComments);
        model.addAttribute("totalComments", totalComments);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalTags", totalTags);

        return "admin/dashboard";
    }
}
