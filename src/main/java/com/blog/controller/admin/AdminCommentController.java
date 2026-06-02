package com.blog.controller.admin;

import com.blog.entity.Comment;
import com.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<Comment> comments = commentService.getAllComments(page, size);
        model.addAttribute("comments", comments);
        return "admin/comments/list";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        commentService.approveComment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Comment approved!");
        return "redirect:/admin/comments";
    }

    @PostMapping("/{id}/spam")
    public String markAsSpam(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        commentService.markAsSpam(id);
        redirectAttributes.addFlashAttribute("successMessage", "Comment marked as spam.");
        return "redirect:/admin/comments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        commentService.deleteComment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Comment deleted.");
        return "redirect:/admin/comments";
    }
}
