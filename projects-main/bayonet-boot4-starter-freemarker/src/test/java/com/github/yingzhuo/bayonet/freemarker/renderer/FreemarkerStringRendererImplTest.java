package com.github.yingzhuo.bayonet.freemarker.renderer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class FreemarkerStringRendererImplTest {

    private FreemarkerStringRendererImpl renderer;

    @BeforeEach
    void setUp() {
        renderer = new FreemarkerStringRendererImpl();
        renderer.setTemplateLoaderPaths(new String[]{"classpath:/templates/"});
        renderer.setSuffix(".ftl");
        renderer.setDefaultEncoding("UTF-8");
        renderer.afterPropertiesSet();
    }

    @Test
    void should_render_template_with_data() {
        var result = renderer.render("test", Map.of("name", "World"));
        assertThat(result).isEqualTo("Hello, World!\n");
    }

    @Test
    void should_render_template_with_data_when_calling_single_arg_render() {
        var result = renderer.render("no-vars");
        assertThat(result).isEqualTo("no variables\n");
    }

    @Test
    void should_throw_when_template_not_found() {
        assertThatExceptionOfType(UncheckedIOException.class)
                .isThrownBy(() -> renderer.render("non-existent"));
    }

    @Test
    void should_throw_when_template_name_is_blank() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> renderer.render("  "))
                .withMessageContaining("template name is required");
    }

    @Test
    void should_use_custom_suffix() {
        var r = new FreemarkerStringRendererImpl();
        r.setTemplateLoaderPaths(new String[]{"classpath:/templates/"});
        r.setSuffix("");
        r.setDefaultEncoding("UTF-8");
        r.afterPropertiesSet();

        var result = r.render("empty-suffix", Map.of("value", "ok"));
        assertThat(result).isEqualTo("content: ok\n");
    }

    @Test
    void should_throw_when_templateLoaderPaths_contains_unsupported_prefix() {
        var bad = new FreemarkerStringRendererImpl();
        bad.setTemplateLoaderPaths(new String[]{"unsupported:/path"});
        bad.setSuffix(".ftl");

        assertThatIllegalArgumentException()
                .isThrownBy(bad::afterPropertiesSet)
                .withMessageContaining("Unsupported path prefix");
    }

    @Test
    void should_support_file_prefix_path() throws Exception {
        var tempDir = java.nio.file.Files.createTempDirectory("ftl-test-");
        tempDir.toFile().deleteOnExit();
        var tempFile = tempDir.resolve("hello.ftl");
        java.nio.file.Files.writeString(tempFile, "file: ${msg}\n");

        var r = new FreemarkerStringRendererImpl();
        r.setTemplateLoaderPaths(new String[]{"file:" + tempDir + "/"});
        r.setSuffix(".ftl");
        r.setDefaultEncoding("UTF-8");
        r.afterPropertiesSet();

        var result = r.render("hello", Map.of("msg", "ok"));
        assertThat(result).isEqualTo("file: ok\n");
    }

}
