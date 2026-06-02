package com.blog.controller;

import com.blog.entity.Category;
import com.blog.entity.Post;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.CategoryService;
import com.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final PostService postService;

    @GetMapping("/{slug}")
    public String postsByCategory(@PathVariable String slug,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        Category category = categoryService.getBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));

        Page<Post> posts = postService.getPostsByCategory(slug, page, size);

        model.addAttribute("category", category);
        model.addAttribute("posts", posts);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("recentPosts", postService.getRecentPosts(5));

        return "category/posts";
    }
}
