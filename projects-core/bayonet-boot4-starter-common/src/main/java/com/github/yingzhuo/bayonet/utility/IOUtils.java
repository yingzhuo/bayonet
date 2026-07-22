package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * IO 操作工具类。
 *
 * <p>基于 Spring 的 {@link FileCopyUtils} 和 {@link StreamUtils} 实现，
 * 将 checked {@link IOException} 统一包装为 {@link UncheckedIOException}。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * byte[] data = IOUtils.copyToByteArray(inputStream);
 * String text = IOUtils.copyToString(inputStream);
 * IOUtils.copy(inputStream, outputStream);
 * }</pre>
 *
 * @author 应卓
 * @see FileCopyUtils
 * @see StreamUtils
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IOUtils {

    /**
     * 拷贝输入流到输出流。
     *
     * @param in  输入流，不能为 {@code null}
     * @param out 输出流，不能为 {@code null}
     * @return 拷贝的字节数
     */
    public static int copy(InputStream in, OutputStream out) {
        try {
            return FileCopyUtils.copy(in, out);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    /**
     * 拷贝字符输入流到字符输出流。
     *
     * @param in  字符输入流，不能为 {@code null}
     * @param out 字符输出流，不能为 {@code null}
     * @return 拷贝的字符数
     */
    public static int copy(Reader in, Writer out) {
        try {
            return FileCopyUtils.copy(in, out);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    /**
     * 拷贝字节数组到输出流。
     *
     * @param in  字节数组，不能为 {@code null}
     * @param out 输出流，不能为 {@code null}
     */
    public static void copy(byte[] in, OutputStream out) {
        try {
            StreamUtils.copy(in, out);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    /**
     * 拷贝字符串到输出流（默认 UTF-8 编码）。
     *
     * @param in  字符串，不能为 {@code null}
     * @param out 输出流，不能为 {@code null}
     */
    public static void copy(String in, OutputStream out) {
        copy(in, null, out);
    }

    /**
     * 拷贝字符串到输出流（指定编码）。
     *
     * @param in      字符串，不能为 {@code null}
     * @param charset 字符编码，为 {@code null} 时使用 UTF-8
     * @param out     输出流，不能为 {@code null}
     */
    public static void copy(String in, @Nullable Charset charset, OutputStream out) {
        charset = Objects.requireNonNullElse(charset, StandardCharsets.UTF_8);

        try {
            StreamUtils.copy(in, charset, out);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    /**
     * 读取输入流全部字节。
     *
     * @param in 输入流，不能为 {@code null}
     * @return 字节数组
     */
    public static byte[] copyToByteArray(InputStream in) {
        try {
            return StreamUtils.copyToByteArray(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    /**
     * 读取输入流为字符串（默认 UTF-8 编码）。
     *
     * @param in 输入流，不能为 {@code null}
     * @return 字符串
     */
    public static String copyToString(InputStream in) {
        return copyToString(in, null);
    }

    /**
     * 读取输入流为字符串（指定编码）。
     *
     * @param in      输入流，不能为 {@code null}
     * @param charset 字符编码，为 {@code null} 时使用 UTF-8
     * @return 字符串
     */
    public static String copyToString(InputStream in, @Nullable Charset charset) {
        charset = Objects.requireNonNullElse(charset, StandardCharsets.UTF_8);

        try {
            return StreamUtils.copyToString(in, charset);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    /**
     * 范围拷贝输入流到输出流。
     *
     * @param in    输入流，不能为 {@code null}
     * @param out   输出流，不能为 {@code null}
     * @param start 起始位置
     * @param end   结束位置（不包含）
     * @return 拷贝的字节数
     */
    public static long copyRange(InputStream in, OutputStream out, long start, long end) {
        try {
            return StreamUtils.copyRange(in, out, start, end);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    /**
     * 丢弃输入流中所有剩余字节。
     *
     * @param in 输入流，不能为 {@code null}
     * @return 丢弃的字节数
     */
    public static int drain(InputStream in) {
        try {
            return StreamUtils.drain(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

}
