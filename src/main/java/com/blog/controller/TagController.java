package com.blog.controller;

import com.blog.entity.Post;
import com.blog.entity.Tag;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.CategoryService;
import com.blog.service.PostService;
import com.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final PostService postService;
    private final CategoryService categoryService;

    @GetMapping("/{slug}")
    public String postsByTag(@PathVariable String slug,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        Tag tag = tagService.getBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Tag", "slug", slug));

        Page<Post> posts = postService.getPostsByTag(slug, page, size);

        model.addAttribute("tag", tag);
        model.addAttribute("posts", posts);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("recentPosts", postService.getRecentPosts(5));

        return "tag/posts";
    }
}
