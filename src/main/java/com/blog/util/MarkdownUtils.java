package com.blog.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Server-side Markdown to HTML converter using commonmark-java.
 * Used for RSS feeds, email notifications, or API responses.
 * The main blog frontend uses showdown.js for client-side rendering.
 */
public class MarkdownUtils {

    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder()
        .escapeHtml(true)
        .build();

    public static String toHtml(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        Node document = PARSER.parse(markdown);
        return RENDERER.render(document);
    }
}
