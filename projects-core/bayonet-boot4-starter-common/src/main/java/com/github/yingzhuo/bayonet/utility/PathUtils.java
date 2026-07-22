package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 文件路径操作工具类。
 *
 * <p>提供路径创建、拷贝、移动、删除、读写等常用文件系统操作，
 * 将 checked {@link IOException} 统一包装为 {@link UncheckedIOException}。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * Path p = PathUtils.createPath("/tmp", "test.txt");
 * PathUtils.writeBytes(p, "hello".getBytes(), true, false);
 * String text = new String(PathUtils.readBytes(p));
 * PathUtils.delete(p);
 * }</pre>
 *
 * @author 应卓
 * @see Files
 * @see Paths
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathUtils {

    /**
     * 将 {@link Path} 转换为 {@link File}。
     *
     * @param path 路径，不能为 {@code null}
     * @return File 对象
     */
    public static File toFile(Path path) {
        return path.toFile();
    }

    /**
     * 创建并规范化路径。
     *
     * @param first 路径首段，不能为 {@code null}
     * @param more  后续路径片段
     * @return 规范化后的路径
     */
    public static Path createPath(String first, String... more) {
        return Paths.get(first, more).normalize();
    }

    /**
     * 创建绝对路径。
     *
     * @param first 路径首段，不能为 {@code null}
     * @param more  后续路径片段
     * @return 绝对路径
     */
    public static Path createAbsolutePath(String first, String... more) {
        return createPath(first, more).toAbsolutePath();
    }

    /**
     * 创建文件并返回路径。
     *
     * @param first 路径首段，不能为 {@code null}
     * @param more  后续路径片段
     * @return 创建后的路径
     */
    public static Path createFile(String first, String... more) {
        final Path path = createPath(first, more);
        createFile(path);
        return path;
    }

    /**
     * 创建文件。
     *
     * @param path 文件路径，不能为 {@code null}
     */
    public static void createFile(Path path) {
        try {
            boolean success = toFile(path).createNewFile();
            if (!success) {
                final String msg = String.format("unable to create file: {%s}", path);
                throw new UncheckedIOException(new IOException(msg));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 创建目录并返回路径。
     *
     * @param first 路径首段，不能为 {@code null}
     * @param more  后续路径片段
     * @return 创建后的路径
     */
    public static Path createDirectory(String first, String... more) {
        final Path path = createPath(first, more);
        createDirectory(path);
        return path;
    }

    /**
     * 创建目录（含中间目录）。
     *
     * @param path 目录路径，不能为 {@code null}
     */
    public static void createDirectory(Path path) {
        if (exists(path)) {
            if (isDirectory(path)) {
                return;
            } else {
                final String msg = String.format("unable to create dir: {%s}", path);
                throw new UncheckedIOException(new IOException(msg));
            }
        }

        boolean success = toFile(path).mkdirs();
        if (!success) {
            final String msg = String.format("unable to create dir: {%s}", path);
            throw new UncheckedIOException(new IOException(msg));
        }
    }

    /**
     * 移动文件或目录。
     *
     * @param source          源路径，不能为 {@code null}
     * @param target          目标路径，不能为 {@code null}
     * @param replaceExisting 是否覆盖已有文件
     */
    public static void move(Path source, Path target, boolean replaceExisting) {
        final List<CopyOption> copyOptions = new LinkedList<>();

        if (replaceExisting) {
            copyOptions.add(StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            Files.move(source, target, copyOptions.toArray(new CopyOption[0]));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 拷贝文件或目录。
     *
     * @param source          源路径，不能为 {@code null}
     * @param target          目标路径，不能为 {@code null}
     * @param replaceExisting 是否覆盖已有文件
     */
    public static void copy(Path source, Path target, boolean replaceExisting) {
        final List<CopyOption> copyOptions = new ArrayList<>();

        if (replaceExisting) {
            copyOptions.add(StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            Files.copy(source, target, copyOptions.toArray(new CopyOption[0]));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 更新文件的最后修改时间，不存在则创建。
     *
     * @param path 文件路径，不能为 {@code null}
     */
    public static void touch(Path path) {
        try {
            if (Files.exists(path)) {
                Files.setLastModifiedTime(path, FileTime.from(Instant.now()));
            } else {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 判断路径是否存在。
     *
     * @param path 路径，不能为 {@code null}
     * @return 存在返回 {@code true}
     */
    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    /**
     * 判断路径是否为目录。
     *
     * @param path 路径，不能为 {@code null}
     * @return 是目录返回 {@code true}
     */
    public static boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    /**
     * 判断目录是否为空。
     *
     * @param path 路径，不能为 {@code null}
     * @return 空目录返回 {@code true}，非目录也返回 {@code false}
     */
    public static boolean isEmptyDirectory(Path path) {
        if (!isDirectory(path)) {
            return false;
        }

        try {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                return !directory.iterator().hasNext();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 判断路径是否为普通文件。
     *
     * @param path 路径，不能为 {@code null}
     * @return 是普通文件返回 {@code true}
     */
    public static boolean isRegularFile(Path path) {
        return Files.isRegularFile(path);
    }

    /**
     * 判断路径是否为符号链接。
     *
     * @param path 路径，不能为 {@code null}
     * @return 是符号链接返回 {@code true}
     */
    public static boolean isSymbolicLink(Path path) {
        return Files.isSymbolicLink(path);
    }

    /**
     * 判断路径是否为隐藏文件。
     *
     * @param path 路径，不能为 {@code null}
     * @return 是隐藏文件返回 {@code true}
     */
    public static boolean isHidden(Path path) {
        try {
            return Files.isHidden(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 判断路径是否可读且可写。
     *
     * @param path 路径，不能为 {@code null}
     * @return 可读且可写返回 {@code true}
     */
    public static boolean isReadableAndWritable(Path path) {
        return isReadable(path) && isWritable(path);
    }

    /**
     * 判断路径是否可读。
     *
     * @param path 路径，不能为 {@code null}
     * @return 可读返回 {@code true}
     */
    public static boolean isReadable(Path path) {
        return Files.isReadable(path);
    }

    /**
     * 判断路径是否可写。
     *
     * @param path 路径，不能为 {@code null}
     * @return 可写返回 {@code true}
     */
    public static boolean isWritable(Path path) {
        return Files.isWritable(path);
    }

    /**
     * 判断路径是否可执行。
     *
     * @param path 路径，不能为 {@code null}
     * @return 可执行返回 {@code true}
     */
    public static boolean isExecutable(Path path) {
        return Files.isExecutable(path);
    }

    /**
     * 获取文件大小。
     *
     * @param path 文件路径，不能为 {@code null}
     * @return 文件大小（字节）
     */
    public static long size(Path path) {
        if (!exists(path)) {
            throw new UncheckedIOException(new IOException("file not exists"));
        }

        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 删除文件或目录。
     *
     * @param path 路径，不能为 {@code null}
     */
    public static void delete(Path path) {
        try {
            if (!exists(path)) {
                return;
            }

            if (isRegularFile(path)) {
                Files.deleteIfExists(path);
            } else {
                FileSystemUtils.deleteRecursively(path);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 静默删除文件或目录（忽略异常）。
     *
     * @param path 路径，不能为 {@code null}
     */
    public static void deleteQuietly(Path path) {
        try {
            delete(path);
        } catch (Exception e) {
            // nop
        }
    }

    /**
     * 清空目录（不删除目录本身）。
     *
     * @param path 目录路径，不能为 {@code null}
     */
    public static void cleanDirectory(Path path) {
        if (!isDirectory(path)) {
            return;
        }

        PathTreeUtils.list(path, 1).forEach(found -> {
            if (!found.equals(path)) {
                delete(found);
            }
        });
    }

    /**
     * 静默清空目录（忽略异常）。
     *
     * @param path 目录路径，不能为 {@code null}
     */
    public static void cleanDirectoryQuietly(Path path) {
        if (!isDirectory(path)) {
            return;
        }

        PathTreeUtils.list(path, 1).forEach(found -> {
            if (!found.equals(path)) {
                deleteQuietly(found);
            }
        });
    }

    /**
     * 获取文件创建时间。
     *
     * @param path 文件路径，不能为 {@code null}
     * @return 创建时间
     */
    public static Date getCreationTime(Path path) {
        return new Date(readAttributes(path).creationTime().to(java.util.concurrent.TimeUnit.MILLISECONDS));
    }

    /**
     * 获取文件最后修改时间。
     *
     * @param path 文件路径，不能为 {@code null}
     * @return 最后修改时间
     */
    public static Date getLastModifiedTime(Path path) {
        return new Date(readAttributes(path).lastModifiedTime().to(java.util.concurrent.TimeUnit.MILLISECONDS));
    }

    /**
     * 获取文件最后访问时间。
     *
     * @param path 文件路径，不能为 {@code null}
     * @return 最后访问时间
     */
    public static Date getLastAccessTime(Path path) {
        return new Date(readAttributes(path).lastAccessTime().to(java.util.concurrent.TimeUnit.MILLISECONDS));
    }

    /**
     * 读取文件所有行（默认 UTF-8）。
     *
     * @param path 文件路径，不能为 {@code null}
     * @return 行列表
     */
    public static List<String> readLines(Path path) {
        return readLines(path, UTF_8);
    }

    /**
     * 读取文件所有行（指定编码）。
     *
     * @param path    文件路径，不能为 {@code null}
     * @param charset 字符编码，不能为 {@code null}
     * @return 行列表
     */
    public static List<String> readLines(Path path, Charset charset) {
        try {
            return Files.readAllLines(path, charset);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 读取文件所有字节。
     *
     * @param path 文件路径，不能为 {@code null}
     * @return 字节数组
     */
    public static byte[] readBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 写入字节到文件。
     *
     * @param path              文件路径，不能为 {@code null}
     * @param bytes             字节数据，不能为 {@code null}
     * @param createIfNotExists 不存在时是否创建
     * @param append            是否追加
     */
    public static void writeBytes(Path path, byte[] bytes, boolean createIfNotExists, boolean append) {
        final List<OpenOption> openOptions = new LinkedList<>();
        openOptions.add(StandardOpenOption.WRITE);

        if (createIfNotExists) {
            openOptions.add(StandardOpenOption.CREATE);
        }

        if (append) {
            openOptions.add(StandardOpenOption.APPEND);
        } else {
            openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
        }

        try {
            Files.write(path, bytes, openOptions.toArray(new OpenOption[0]));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 写入文本行到文件（默认 UTF-8）。
     *
     * @param path              文件路径，不能为 {@code null}
     * @param lines             文本行列表，不能为 {@code null}
     * @param createIfNotExists 不存在时是否创建
     * @param append            是否追加
     */
    public static void writeLines(Path path, List<String> lines, boolean createIfNotExists, boolean append) {
        writeLines(path, lines, UTF_8, createIfNotExists, append);
    }

    /**
     * 写入文本行到文件（指定编码）。
     *
     * @param path              文件路径，不能为 {@code null}
     * @param lines             文本行列表，不能为 {@code null}
     * @param charset           字符编码，不能为 {@code null}
     * @param createIfNotExists 不存在时是否创建
     * @param append            是否追加
     */
    public static void writeLines(Path path, List<String> lines, Charset charset, boolean createIfNotExists,
                                  boolean append) {
        final List<OpenOption> openOptions = new ArrayList<>();
        openOptions.add(StandardOpenOption.WRITE);

        if (createIfNotExists) {
            openOptions.add(StandardOpenOption.CREATE);
        }

        if (append) {
            openOptions.add(StandardOpenOption.APPEND);
        } else {
            openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
        }

        try {
            Files.write(path, lines, charset, openOptions.toArray(new OpenOption[0]));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 将路径转换为绝对路径。
     *
     * @param path 路径，不能为 {@code null}
     * @return 绝对路径
     */
    public static Path toAbsolutePath(Path path) {
        return path.toAbsolutePath();
    }

    /**
     * 判断两个路径是否指向同一文件。
     *
     * @param p1 路径 1，不能为 {@code null}
     * @param p2 路径 2，不能为 {@code null}
     * @return 是同一文件返回 {@code true}
     */
    public static boolean isSameFile(Path p1, Path p2) {
        try {
            return Files.isSameFile(p1, p2);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static BasicFileAttributes readAttributes(Path path) {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
