package com.blog.service;

import com.blog.entity.Tag;
import com.blog.repository.TagRepository;
import com.blog.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Optional<Tag> getBySlug(String slug) {
        return tagRepository.findBySlug(slug);
    }

    @Transactional
    public Tag createTag(String name) {
        String slug = SlugUtils.toSlug(name);

        // Check for existing tag with same name
        if (tagRepository.existsByName(name)) {
            throw new RuntimeException("A tag with this name already exists: " + name);
        }

        Tag tag = Tag.builder()
            .name(name)
            .slug(slug)
            .build();

        return tagRepository.save(tag);
    }

    @Transactional
    public Tag updateTag(Long id, String name) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));

        // Check uniqueness if name changed
        if (!name.equals(tag.getName()) && tagRepository.existsByName(name)) {
            throw new RuntimeException("A tag with this name already exists: " + name);
        }

        tag.setName(name);
        tag.setSlug(SlugUtils.toSlug(name));
        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));

        // Remove this tag from all associated posts
        tag.getPosts().forEach(post -> post.removeTag(tag));
        tagRepository.delete(tag);
    }

    public long getPostCountByTag(Long tagId) {
        return tagRepository.findById(tagId)
            .map(t -> (long) t.getPosts().size())
            .orElse(0L);
    }
}
