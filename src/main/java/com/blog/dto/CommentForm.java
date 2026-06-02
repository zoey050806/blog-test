package com.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentForm {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be at most 50 characters")
    private String authorName;

    @Email(message = "Please provide a valid email")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String authorEmail;

    @NotBlank(message = "Comment content is required")
    @Size(max = 2000, message = "Comment must be at most 2000 characters")
    private String content;

    private Long parentId;
}
