/**
 * Admin panel JavaScript.
 * Handles EasyMDE initialization, modal forms, and admin-specific interactions.
 */
(function () {
    'use strict';

    // ===== EasyMDE Initialization =====
    function initEasyMDE() {
        var contentTextarea = document.getElementById('content');
        if (contentTextarea && typeof EasyMDE !== 'undefined') {
            new EasyMDE({
                element: contentTextarea,
                spellChecker: false,
                autoDownloadFontAwesome: false,
                placeholder: 'Write your content in Markdown...',
                toolbar: [
                    'heading', 'bold', 'italic', 'strikethrough', '|',
                    'quote', 'unordered-list', 'ordered-list', '|',
                    'link', 'image', 'table', 'horizontal-rule', '|',
                    'code', 'code-block', '|',
                    'preview', 'side-by-side', 'fullscreen', '|',
                    'guide'
                ],
                renderingConfig: {
                    codeSyntaxHighlighting: true
                },
                status: ['lines', 'words', 'cursor'],
                minHeight: '400px'
            });
        }
    }

    // ===== Category Edit Modal =====
    window.editCategory = function (btn) {
        var id = btn.dataset.id;
        var name = btn.dataset.name;
        var slug = btn.dataset.slug;
        var desc = btn.dataset.desc;
        var order = btn.dataset.order;

        document.getElementById('editName').value = name;
        document.getElementById('editSlug').value = slug;
        document.getElementById('editDesc').value = desc || '';
        document.getElementById('editOrder').value = order || 0;

        var form = document.getElementById('editForm');
        form.action = '/admin/categories/' + id;

        var modal = new bootstrap.Modal(document.getElementById('editModal'));
        modal.show();
    };

    // ===== Tag Edit Modal =====
    window.editTag = function (btn) {
        var id = btn.dataset.id;
        var name = btn.dataset.name;

        document.getElementById('editName').value = name;

        var form = document.getElementById('editForm');
        form.action = '/admin/tags/' + id;

        // Use tag edit modal if it exists, otherwise reuse createModal with edit
        var editModal = document.getElementById('editModal');
        if (editModal) {
            var modal = new bootstrap.Modal(editModal);
            modal.show();
        }
    };

    // ===== Slug Auto-Generation =====
    function setupSlugGeneration() {
        var titleInput = document.getElementById('title');
        var slugInput = document.getElementById('slug');

        if (titleInput && slugInput) {
            titleInput.addEventListener('input', function () {
                if (!slugInput.dataset.manual || !slugInput.value) {
                    slugInput.value = generateSlug(titleInput.value);
                    slugInput.dataset.manual = 'false';
                }
            });

            slugInput.addEventListener('input', function () {
                slugInput.dataset.manual = 'true';
            });
        }
    }

    function generateSlug(text) {
        return text.trim().toLowerCase()
            .replace(/[^\w一-龥]+/g, '-')
            .replace(/^-+|-+$/g, '')
            .replace(/-+/g, '-');
    }

    // ===== Initialize =====
    document.addEventListener('DOMContentLoaded', function () {
        initEasyMDE();
        setupSlugGeneration();
    });
})();
