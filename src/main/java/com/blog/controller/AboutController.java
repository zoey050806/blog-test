package com.blog.controller;

import com.blog.service.AboutService;
import com.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AboutController {

    private final AboutService aboutService;
    private final CategoryService categoryService;

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("about", aboutService.getAbout());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "about";
    }
}
