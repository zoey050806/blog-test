package com.blog.dto;

import com.blog.entity.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PostForm {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @Size(max = 500, message = "Summary must be at most 500 characters")
    private String summary;

    @NotBlank(message = "Content is required")
    private String content;

    private String coverImage;

    private Long categoryId;

    private List<Long> tagIds;

    private PostStatus status = PostStatus.DRAFT;

    private Boolean isTop = false;

    @Size(max = 200, message = "Slug must be at most 200 characters")
    private String slug;
}
