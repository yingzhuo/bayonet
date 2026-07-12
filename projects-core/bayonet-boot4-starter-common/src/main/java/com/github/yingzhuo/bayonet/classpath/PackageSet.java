package com.github.yingzhuo.bayonet.classpath;

import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 * 包名集合。
 * <p>一个不可变视图的包名集合，支持从字符串、{@link Package} 对象、{@link Class 类} 三种来源添加包名。
 * 内部使用 {@link TreeSet} 保持自然排序，通过 {@link #asSet()} 返回不可修改视图。</p>
 *
 * <pre>{@code
 * var set = new PackageSet()
 *         .acceptPackages("com.example.foo", "com.example.bar")
 *         .acceptBasePackageClasses(MyApplication.class)
 *         .asSet();
 * }</pre>
 */
public final class PackageSet implements Iterable<String> {

    private final SortedSet<String> innerSet = new TreeSet<>();

    /**
     * 添加包名（字符串形式）。
     *
     * @param packages 包名数组，{@code null} 或空字符串将被忽略
     * @return this
     */
    public PackageSet acceptPackages(@Nullable String... packages) {
        if (packages != null) {
            Stream.of(packages)
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .forEach(innerSet::add);
        }
        return this;
    }

    /**
     * 添加包名（{@link Package} 对象形式）。
     *
     * @param packages {@link Package} 数组，{@code null} 元素将被忽略
     * @return this
     */
    public PackageSet acceptPackages(@Nullable Package... packages) {
        if (packages != null) {
            Stream.of(packages)
                    .filter(Objects::nonNull)
                    .map(Package::getName)
                    .forEach(innerSet::add);
        }
        return this;
    }

    /**
     * 添加包名（通过 {@link Class 类} 推断）。
     * <p>通过 {@link Class#getPackage()} 推断包名。仅支持常规类类型，
     * 原始类型（{@code int.class}）、数组类型（{@code String[].class}）等
     * {@link Class#getPackage()} 返回 {@code null} 的类型将被忽略。</p>
     *
     * @param baseClasses 类数组，{@code null} 元素或无法获取包名的类将被忽略
     * @return this
     */
    public PackageSet acceptBasePackageClasses(@Nullable Class<?>... baseClasses) {
        if (baseClasses != null) {
            Arrays.stream(baseClasses)
                    .filter(Objects::nonNull)
                    .map(c -> c.getPackage())
                    .filter(Objects::nonNull)
                    .map(Package::getName)
                    .forEach(innerSet::add);
        }
        return this;
    }

    @Override
    public Iterator<String> iterator() {
        return innerSet.iterator();
    }

    /**
     * 清空所有包名。
     *
     * @return this
     */
    public PackageSet clear() {
        innerSet.clear();
        return this;
    }

    /**
     * 集合是否为空。
     *
     * @return 空返回 {@code true}
     */
    public boolean isEmpty() {
        return innerSet.isEmpty();
    }

    /**
     * 返回包名数量。
     *
     * @return 包名数量
     */
    public int size() {
        return innerSet.size();
    }

    /**
     * 返回不可修改的包名集合视图。
     *
     * @return 不可修改的排序集合
     */
    public SortedSet<String> asSet() {
        return Collections.unmodifiableSortedSet(innerSet);
    }

}
