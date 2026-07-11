package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceUtilsTest {

    private static final String EXISTS = "classpath:resource-utils-test.txt";

    // ============== loadBytes ==============

    @Test
    void should_loadBytes_when_locationExists() {
        var bytes = ResourceUtils.loadBytes(EXISTS);
        assertThat(bytes).isNotEmpty();
        assertThat(new String(bytes, StandardCharsets.UTF_8)).startsWith("Hello, ResourceUtils!");
    }

    @Test
    void should_throw_when_loadBytes_locationEmpty() {
        assertThatThrownBy(() -> ResourceUtils.loadBytes(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadBytes_locationNotExists() {
        assertThatThrownBy(() -> ResourceUtils.loadBytes("classpath:nonexistent.txt"))
                .isInstanceOf(UncheckedIOException.class);
    }

    // ============== loadText ==============

    @Test
    void should_loadText_when_locationExists() {
        var text = ResourceUtils.loadText(EXISTS);
        assertThat(text).startsWith("Hello, ResourceUtils!");
    }

    @Test
    void should_loadText_withExplicitCharset() {
        var text = ResourceUtils.loadText(EXISTS, StandardCharsets.UTF_8);
        assertThat(text).startsWith("Hello, ResourceUtils!");
    }

    @Test
    void should_loadText_withNullCharset() {
        // null charset -> 默认 UTF_8
        var text = ResourceUtils.loadText(EXISTS, null);
        assertThat(text).startsWith("Hello, ResourceUtils!");
    }

    @Test
    void should_throw_when_loadText_locationEmpty() {
        assertThatThrownBy(() -> ResourceUtils.loadText(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadText_locationNotExists() {
        assertThatThrownBy(() -> ResourceUtils.loadText("classpath:nonexistent.txt"))
                .isInstanceOf(UncheckedIOException.class);
    }

    // ============== loadAsInputStream ==============

    @Test
    void should_loadAsInputStream_when_locationExists() throws Exception {
        try (var stream = ResourceUtils.loadAsInputStream(EXISTS)) {
            assertThat(stream).isNotNull();
            assertThat(stream.read()).isNotEqualTo(-1);
        }
    }

    @Test
    void should_throw_when_loadAsInputStream_locationEmpty() {
        assertThatThrownBy(() -> ResourceUtils.loadAsInputStream(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadAsInputStream_locationNotExists() {
        assertThatThrownBy(() -> ResourceUtils.loadAsInputStream("classpath:nonexistent.txt"))
                .isInstanceOf(UncheckedIOException.class);
    }

    // ============== getResourceLoader ==============

    @Test
    void should_return_nonNull_resourceLoader() {
        // 即使没有 Spring 上下文，也应返回默认 ResourceLoader
        assertThat(ResourceUtils.getResourceLoader()).isNotNull();
    }

}
