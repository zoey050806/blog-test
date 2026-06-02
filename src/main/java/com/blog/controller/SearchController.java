package com.blog.controller;

import com.blog.entity.Post;
import com.blog.service.CategoryService;
import com.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final PostService postService;
    private final CategoryService categoryService;

    @GetMapping("/search")
    public String search(@RequestParam("q") String keyword,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {

        Page<Post> posts = postService.searchPosts(keyword, page, size);

        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("recentPosts", postService.getRecentPosts(5));

        return "search";
    }
}
