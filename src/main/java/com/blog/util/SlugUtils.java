package com.blog.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtils {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\u4e00-\\u9fa5]+");
    private static final Pattern HYPHEN_EDGES = Pattern.compile("^-|-$");

    /**
     * Convert a string to a URL-friendly slug.
     * Preserves Chinese characters for SEO.
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String slug = input.trim().toLowerCase();

        // Normalize to decompose combined characters
        slug = Normalizer.normalize(slug, Normalizer.Form.NFKD);

        // Replace non-alphanumeric (excluding Chinese) with hyphens
        slug = NON_ALPHANUMERIC.matcher(slug).replaceAll("-");

        // Collapse multiple hyphens
        slug = slug.replaceAll("-{2,}", "-");

        // Remove leading/trailing hyphens
        slug = HYPHEN_EDGES.matcher(slug).replaceAll("");

        return slug;
    }
}
