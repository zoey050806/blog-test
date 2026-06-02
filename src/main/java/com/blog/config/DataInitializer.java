package com.blog.config;

import com.blog.entity.About;
import com.blog.entity.Category;
import com.blog.entity.User;
import com.blog.repository.AboutRepository;
import com.blog.repository.CategoryRepository;
import com.blog.repository.UserRepository;
import com.blog.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AboutRepository aboutRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdminUser();
        initDefaultCategory();
        initAbout();
    }

    private void initAdminUser() {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .displayName("Admin")
                .email("admin@blog.com")
                .build();
            userRepository.save(admin);
            log.info("Default admin user created: admin / admin123");
        }
    }

    private void initDefaultCategory() {
        if (categoryRepository.count() == 0) {
            Category uncategorized = Category.builder()
                .name("Uncategorized")
                .slug("uncategorized")
                .description("Default category for posts without a specific category")
                .sortOrder(0)
                .build();
            categoryRepository.save(uncategorized);
            log.info("Default category created: Uncategorized");
        }
    }

    private void initAbout() {
        if (aboutRepository.count() == 0) {
            About about = About.builder()
                .content("""
                    # About Me

                    Hi! I'm a passionate developer who loves to write about technology,
                    programming, and software engineering.

                    ## What I Do

                    - Full-stack web development
                    - Open source contributions
                    - Technical writing and blogging

                    ## About This Blog

                    This is my personal space where I share thoughts, tutorials, and
                    experiences from my journey in tech. Thanks for visiting!
                    """)
                .siteDescription("A personal blog about technology and life")
                .build();
            aboutRepository.save(about);
            log.info("Default About page created");
        }
    }
}
