package com.blog.service;

import com.blog.entity.Category;
import com.blog.repository.CategoryRepository;
import com.blog.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    public Optional<Category> getBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    @Transactional
    public Category createCategory(String name, String slug, String description) {
        if (slug == null || slug.isBlank()) {
            slug = SlugUtils.toSlug(name);
        }

        if (categoryRepository.existsBySlug(slug)) {
            throw new RuntimeException("A category with this slug already exists: " + slug);
        }

        Category category = Category.builder()
            .name(name)
            .slug(slug)
            .description(description)
            .sortOrder(0)
            .build();

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, String name, String slug, String description, Integer sortOrder) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (slug != null && !slug.equals(category.getSlug()) && categoryRepository.existsBySlug(slug)) {
            throw new RuntimeException("A category with this slug already exists: " + slug);
        }

        category.setName(name);
        if (slug != null) category.setSlug(slug);
        category.setDescription(description);
        if (sortOrder != null) category.setSortOrder(sortOrder);

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Detach posts from this category
        category.getPosts().forEach(post -> post.setCategory(null));
        categoryRepository.delete(category);
    }

    public long getPostCountByCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .map(c -> (long) c.getPosts().size())
            .orElse(0L);
    }
}
