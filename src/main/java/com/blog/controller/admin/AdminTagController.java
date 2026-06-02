package com.blog.controller.admin;

import com.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagService tagService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tags", tagService.getAllTags());
        return "admin/tags/list";
    }

    @PostMapping
    public String create(@RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            tagService.createTag(name);
            redirectAttributes.addFlashAttribute("successMessage", "Tag created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tags";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @RequestParam String name,
                         RedirectAttributes redirectAttributes) {
        try {
            tagService.updateTag(id, name);
            redirectAttributes.addFlashAttribute("successMessage", "Tag updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tags";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tagService.deleteTag(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tag deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tags";
    }
}
