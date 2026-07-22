package com.github.yingzhuo.bayonet.utility.collection;

import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.Objects;

/**
 * 版本号比较器。
 *
 * <p>比较以 {@code "."} 分隔的版本号字符串（如 {@code "1.2.3"}），
 * 按数值语义逐段比较，支持不定长版本号。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
public class VersionComparator implements Comparator<String> {

    /**
     * 比较两个版本号。
     * <p>任一参数为 {@code null} 时，视其为最小版本（排在前面）。</p>
     *
     * @param version1 版本号 1（可为 {@code null}）
     * @param version2 版本号 2（可为 {@code null}）
     * @return 负数、零、正数，分别表示 version1 小于、等于、大于 version2
     */
    @Override
    public int compare(@Nullable String version1, @Nullable String version2) {
        if (Objects.equals(version1, version2)) {
            return 0;
        }

        if (version1 == null) {
            return -1;
        }

        if (version2 == null) {
            return 1;
        }

        final String[] v1s = StringUtils.split(version1, ".");
        final String[] v2s = StringUtils.split(version2, ".");

        Objects.requireNonNull(v1s);
        Objects.requireNonNull(v2s);

        int diff = 0;
        int minLength = Math.min(v1s.length, v2s.length);

        for (int i = 0; i < minLength; i++) {
            var n1 = Integer.parseInt(v1s[i]);
            var n2 = Integer.parseInt(v2s[i]);
            diff = Integer.compare(n1, n2);
            if (diff != 0) {
                break;
            }
        }

        return (diff != 0) ? diff : v1s.length - v2s.length;
    }

}
