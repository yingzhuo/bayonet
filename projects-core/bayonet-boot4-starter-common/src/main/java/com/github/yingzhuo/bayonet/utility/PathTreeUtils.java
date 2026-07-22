package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 目录树遍历工具类。
 *
 * <p>基于 {@link Files#walk} 实现，支持递归遍历和指定深度限制。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * try (var paths = PathTreeUtils.list(Paths.get("/tmp"))) {
 *     paths.forEach(System.out::println);
 * }
 * }</pre>
 *
 * @author 应卓
 * @see Files#walk
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathTreeUtils {

    /**
     * 递归遍历目录（不限深度）。
     *
     * <p>返回的 {@link Stream} 需要在使用后关闭（如 try-with-resources）。</p>
     *
     * @param path 起始路径，不能为 {@code null}
     * @return 路径流
     */
    public static Stream<Path> list(Path path) {
        return list(path, Integer.MAX_VALUE);
    }

    /**
     * 遍历目录（指定最大深度）。
     *
     * <p>返回的 {@link Stream} 需要在使用后关闭（如 try-with-resources）。</p>
     *
     * @param path     起始路径，不能为 {@code null}
     * @param maxDepth 最大遍历深度，不能为负数
     * @return 路径流
     */
    public static Stream<Path> list(Path path, int maxDepth) {
        Assert.notNull(path, "path is required");
        Assert.isTrue(maxDepth >= 0, "maxDepth must greater than 0");

        if (!PathUtils.exists(path)) {
            final String msg = String.format("'{%s}' does not exist", path);
            throw new UncheckedIOException(new IOException(msg));
        }

        try {
            return Files.walk(path, maxDepth, FileVisitOption.FOLLOW_LINKS);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
