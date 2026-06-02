package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AboutForm {

    @NotBlank(message = "Content is required")
    private String content;

    @Size(max = 255, message = "Avatar URL must be at most 255 characters")
    private String avatarUrl;

    @Size(max = 255, message = "GitHub URL must be at most 255 characters")
    private String githubUrl;

    @Size(max = 255, message = "Twitter URL must be at most 255 characters")
    private String twitterUrl;

    @Size(max = 500, message = "Site description must be at most 500 characters")
    private String siteDescription;
}
