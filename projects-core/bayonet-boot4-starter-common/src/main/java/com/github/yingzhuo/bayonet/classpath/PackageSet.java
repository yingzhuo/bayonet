package com.github.yingzhuo.bayonet.classpath;

import com.github.yingzhuo.bayonet.collection.PackageTrie;
import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 * 包名集合。
 * <p>基于 {@link PackageTrie} 实现，自动维护短前缀包名优先的集合语义。
 * 支持从字符串、{@link Package} 对象、{@link Class 类} 三种来源添加包名。</p>
 *
 * <pre>{@code
 * var set = new PackageSet()
 *         .acceptPackages("com.example.foo", "com.example.bar")
 *         .acceptBasePackageClasses(MyApplication.class)
 *         .asSet();
 * }</pre>
 */
public final class PackageSet implements Iterable<String> {

    private final PackageTrie trie = new PackageTrie();

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
                    .forEach(this::addToInnerSet);
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
                    .forEach(this::addToInnerSet);
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
                    .forEach(this::addToInnerSet);
        }
        return this;
    }

    @Override
    public Iterator<String> iterator() {
        return asSet().iterator();
    }

    /**
     * 清空所有包名。
     *
     * @return this
     */
    public PackageSet clear() {
        trie.clear();
        return this;
    }

    /**
     * 集合是否为空。
     *
     * @return 空返回 {@code true}
     */
    public boolean isEmpty() {
        return trie.isEmpty();
    }

    /**
     * 返回包名数量。
     *
     * @return 包名数量
     */
    public int size() {
        return trie.size();
    }

    /**
     * 返回按自然顺序排序的不可修改包名集合视图。
     *
     * @return 不可修改的排序集合
     */
    public SortedSet<String> asSet() {
        return Collections.unmodifiableSortedSet(new TreeSet<>(trie.getAllPackages()));
    }

    private void addToInnerSet(String pkg) {
        if (!StringUtils.hasText(pkg)) {
            return;
        }
        trie.add(pkg);
    }

}
