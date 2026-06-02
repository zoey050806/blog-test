package com.blog.service;

import com.blog.dto.AboutForm;
import com.blog.entity.About;
import com.blog.repository.AboutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AboutService {

    private final AboutRepository aboutRepository;

    @Transactional
    public About getAbout() {
        return aboutRepository.findFirstByOrderByIdAsc()
            .orElseGet(() -> {
                About empty = About.builder()
                    .content("# About Me\n\nWrite something about yourself here.")
                    .build();
                return aboutRepository.save(empty);
            });
    }

    @Transactional
    public About updateAbout(AboutForm form) {
        About about = getAbout();

        about.setContent(form.getContent());
        about.setAvatarUrl(form.getAvatarUrl());
        about.setGithubUrl(form.getGithubUrl());
        about.setTwitterUrl(form.getTwitterUrl());
        about.setSiteDescription(form.getSiteDescription());

        return aboutRepository.save(about);
    }
}
