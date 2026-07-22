package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.nio.file.Paths;

/**
 * 文件名和路径处理工具类。
 *
 * <p>提供文件名/扩展名提取、路径拼接和规范化等常用操作。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * String name = FilenameUtils.getName("/path/to/file.txt");       // "file.txt"
 * String base = FilenameUtils.getBaseName("/path/to/file.txt");   // "file"
 * String ext  = FilenameUtils.getExtension("/path/to/file.txt");  // "txt"
 * String full = FilenameUtils.concat("/path", "to", "file.txt"); // "/path/to/file.txt"
 * }</pre>
 *
 * @author 应卓
 * @see java.nio.file.Paths
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilenameUtils {

    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';
    private static final char EXTENSION_SEPARATOR = '.';
    private static final int NOT_FOUND = -1;

    /**
     * 规范化路径。
     *
     * @param filename 路径字符串，不能为 {@code null}
     * @return 规范化后的路径
     */
    public static String normalize(String filename) {
        return Paths.get(filename).normalize().toString();
    }

    /**
     * 拼接路径并规范化。
     *
     * @param path 基础路径，不能为 {@code null}
     * @param more 待拼接的路径片段，可为 {@code null}
     * @return 拼接并规范化后的路径
     */
    public static String concat(String path, @Nullable String... more) {
        if (more != null) {
            return Paths.get(path, more).normalize().toString();
        }
        return Paths.get(path).normalize().toString();
    }

    /**
     * 从路径中提取文件名（含扩展名）。
     *
     * @param fileName 路径字符串，不能为 {@code null}
     * @return 文件名
     */
    public static String getName(String fileName) {
        requireNonNullChars(fileName);
        int index = indexOfLastSeparator(fileName);
        return fileName.substring(index + 1);
    }

    /**
     * 从路径中提取文件名（不含扩展名）。
     *
     * @param fileName 路径字符串，不能为 {@code null}
     * @return 不含扩展名的文件名
     */
    public static String getBaseName(String fileName) {
        return removeExtension(getName(fileName));
    }

    /**
     * 从文件名中提取扩展名。
     *
     * @param fileName 文件名或路径，不能为 {@code null}
     * @return 扩展名，无扩展名时返回空字符串
     */
    public static String getExtension(String fileName) {
        requireNonNullChars(fileName);
        int index = indexOfExtension(fileName);
        if (index == NOT_FOUND) {
            return "";
        }
        return fileName.substring(index + 1);
    }

    /**
     * 移除文件名的扩展名。
     *
     * @param fileName 文件名或路径，不能为 {@code null}
     * @return 移除扩展名后的字符串
     */
    public static String removeExtension(String fileName) {
        requireNonNullChars(fileName);
        int index = indexOfExtension(fileName);
        if (index == NOT_FOUND) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

    /**
     * 返回扩展名分隔符在文件名中的位置。
     *
     * @param fileName 文件名或路径，可为 {@code null}
     * @return 扩展名分隔符位置，无扩展名或输入为 {@code null} 时返回 {@code -1}
     */
    public static int indexOfExtension(@Nullable String fileName) {
        if (fileName == null) {
            return NOT_FOUND;
        }
        int extensionPos = fileName.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(fileName);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    private static int indexOfLastSeparator(@Nullable String fileName) {
        if (fileName == null) {
            return NOT_FOUND;
        }
        int lastUnixPos = fileName.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = fileName.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    private static void requireNonNullChars(String path) {
        if (path.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("Null byte present in file/path name. There are no "
                    + "known legitimate use cases for such data, but several injection attacks may use it");
        }
    }

}
