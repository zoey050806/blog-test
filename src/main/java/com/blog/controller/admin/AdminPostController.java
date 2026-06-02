package com.blog.controller.admin;

import com.blog.dto.PostForm;
import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.service.CategoryService;
import com.blog.service.FileStorageService;
import com.blog.service.PostService;
import com.blog.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {

    private final PostService postService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Page<Post> posts = postService.getAllPostsForAdmin(page, size);
        model.addAttribute("posts", posts);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/posts/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("postForm", new PostForm());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("tags", tagService.getAllTags());
        model.addAttribute("isEdit", false);
        return "admin/posts/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("postForm") PostForm form,
                         BindingResult result,
                         @AuthenticationPrincipal User currentUser,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("tags", tagService.getAllTags());
            model.addAttribute("isEdit", false);
            return "admin/posts/form";
        }

        try {
            Post post = postService.createPost(form, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Post created successfully!");
            return "redirect:/admin/posts/" + post.getId() + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/posts/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Post post = postService.getAllPostsForAdmin(0, Integer.MAX_VALUE).getContent().stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        PostForm form = new PostForm();
        form.setTitle(post.getTitle());
        form.setSlug(post.getSlug());
        form.setContent(post.getContent());
        form.setSummary(post.getSummary());
        form.setCoverImage(post.getCoverImage());
        form.setStatus(post.getStatus());
        form.setIsTop(post.getIsTop());
        form.setCategoryId(post.getCategory() != null ? post.getCategory().getId() : null);
        form.setTagIds(post.getTags().stream().map(t -> t.getId()).toList());

        model.addAttribute("postForm", form);
        model.addAttribute("post", post);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("tags", tagService.getAllTags());
        model.addAttribute("isEdit", true);
        return "admin/posts/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("postForm") PostForm form,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("tags", tagService.getAllTags());
            model.addAttribute("isEdit", true);
            return "admin/posts/form";
        }

        try {
            postService.updatePost(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Post updated successfully!");
            return "redirect:/admin/posts/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/posts/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            postService.deletePost(id);
            redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/posts";
    }

    @PostMapping("/{id}/publish")
    public String publish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.publishPost(id);
        redirectAttributes.addFlashAttribute("successMessage", "Post published successfully!");
        return "redirect:/admin/posts";
    }

    @PostMapping("/{id}/archive")
    public String archive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.archivePost(id);
        redirectAttributes.addFlashAttribute("successMessage", "Post archived successfully.");
        return "redirect:/admin/posts";
    }

    @PostMapping("/upload-image")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String filename = fileStorageService.store(file);
            return Map.of("filename", filename, "url", "/uploads/" + filename);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
}
