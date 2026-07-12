package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CloseUtilsTest {

    // ============== Closeable ==============

    @Test
    void should_close_Closeable() {
        var closed = new AtomicBoolean(false);
        Closeable c = () -> closed.set(true);

        CloseUtils.closeQuietly(c);
        assertThat(closed).isTrue();
    }

    @Test
    void should_notThrow_when_CloseableIsNull() {
        assertThatCode(() -> CloseUtils.closeQuietly((Closeable) null))
                .doesNotThrowAnyException();
    }

    @Test
    void should_notThrow_when_CloseableThrows() {
        Closeable c = () -> {
            throw new IOException("boom");
        };

        assertThatCode(() -> CloseUtils.closeQuietly(c))
                .doesNotThrowAnyException();
    }

    @Test
    void should_notClose_when_CloseableAlreadyNull() {
        // 验证空安全
        assertThatCode(() -> CloseUtils.closeQuietly((Closeable) null))
                .doesNotThrowAnyException();
    }

    // ============== AutoCloseable ==============

    @Test
    void should_close_AutoCloseable() {
        var closed = new AtomicBoolean(false);
        AutoCloseable c = () -> closed.set(true);

        CloseUtils.closeQuietly(c);
        assertThat(closed).isTrue();
    }

    @Test
    void should_notThrow_when_AutoCloseableIsNull() {
        assertThatCode(() -> CloseUtils.closeQuietly((AutoCloseable) null))
                .doesNotThrowAnyException();
    }

    @Test
    void should_notThrow_when_AutoCloseableThrows() {
        AutoCloseable c = () -> {
            throw new Exception("boom");
        };

        assertThatCode(() -> CloseUtils.closeQuietly(c))
                .doesNotThrowAnyException();
    }

    // Closeable 也是 AutoCloseable，验证重载解析不冲突
    @Test
    void should_close_Closeable_via_AutoCloseable_overload() {
        var closed = new AtomicBoolean(false);
        Closeable c = () -> closed.set(true);

        // 显式转型为 AutoCloseable，验证重载行为
        CloseUtils.closeQuietly((AutoCloseable) c);
        assertThat(closed).isTrue();
    }

}
