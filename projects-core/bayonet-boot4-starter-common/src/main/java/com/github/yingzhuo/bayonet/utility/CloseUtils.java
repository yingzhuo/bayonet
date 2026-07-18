package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

/**
 * 资源静默关闭工具类。
 * <p>提供 {@link Closeable} 和 {@link AutoCloseable} 的静默关闭方法，避免繁琐的 try-catch。</p>
 *
 * <pre>{@code
 * CloseUtils.closeQuietly(inputStream);
 * CloseUtils.closeQuietly(connection);
 * }</pre>
 * @author 应卓
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CloseUtils {

    /**
     * 静默关闭 {@link Closeable}。
     * <p>关闭过程中抛出的 {@link IOException} 将被吞掉。</p>
     *
     * @param closeable 待关闭的资源，可为 {@code null}
     */
    public static void closeQuietly(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 静默关闭 {@link AutoCloseable}。
     * <p>关闭过程中抛出的 {@link Exception} 将被吞掉。</p>
     *
     * @param closeable 待关闭的资源，可为 {@code null}
     */
    public static void closeQuietly(@Nullable AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

}
