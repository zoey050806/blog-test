package com.blog.controller.admin;

import com.blog.dto.AboutForm;
import com.blog.entity.About;
import com.blog.service.AboutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/about")
@RequiredArgsConstructor
public class AdminAboutController {

    private final AboutService aboutService;

    @GetMapping
    public String editForm(Model model) {
        About about = aboutService.getAbout();

        AboutForm form = new AboutForm();
        form.setContent(about.getContent());
        form.setAvatarUrl(about.getAvatarUrl());
        form.setGithubUrl(about.getGithubUrl());
        form.setTwitterUrl(about.getTwitterUrl());
        form.setSiteDescription(about.getSiteDescription());

        model.addAttribute("aboutForm", form);
        model.addAttribute("about", about);
        return "admin/about/form";
    }

    @PostMapping
    public String update(@Valid @ModelAttribute("aboutForm") AboutForm form,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("about", aboutService.getAbout());
            return "admin/about/form";
        }

        try {
            aboutService.updateAbout(form);
            redirectAttributes.addFlashAttribute("successMessage", "About page updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/about";
    }
}
