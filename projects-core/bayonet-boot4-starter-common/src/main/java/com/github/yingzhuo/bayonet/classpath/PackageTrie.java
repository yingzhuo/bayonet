package com.github.yingzhuo.bayonet.classpath;

import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * 包名前缀树。
 * <p>基于 Trie 数据结构维护一组包名，自动确保短前缀包名优先于长包名。
 * 当添加一个包名时，若已有更短的前缀存在，则拒绝添加；反之若新包名是已有
 * 包名的更短前缀，则移除所有被它覆盖的包名。</p>
 * 注意: 本类型不是线程安全的
 *
 * <pre>{@code
 * var trie = new PackageTrie();
 * trie.add("com.example");        // true, 正常添加
 * trie.add("com.example.foo");    // false, "com.example" 已是其前缀
 * trie.add("com");                // true, "com.example" 被移除
 * trie.contains("com");           // true
 * trie.contains("com.example");   // false
 * }</pre>
 */
public final class PackageTrie {

    private Node root = new Node();
    private int size = 0;

    /**
     * 添加包名。
     * <p>若已有更短前缀则拒绝（返回 {@code false}）；若新包名是已有包名的更短前缀，
     * 则移除所有已被覆盖的包名。</p>
     *
     * @param pkgName 包名
     * @return 添加成功返回 {@code true}
     */
    public boolean add(String pkgName) {
        if (hasShorterPrefix(pkgName)) {
            return false;
        }
        var removed = removeAllWithPrefix(pkgName);
        insert(pkgName);
        size = size - removed + 1;
        return true;
    }

    /**
     * 是否存在指定包名。
     *
     * @param pkgName 包名
     * @return 存在返回 {@code true}
     */
    public boolean contains(String pkgName) {
        var node = root;
        for (char c : pkgName.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return false;
        }
        return node.isEnd;
    }

    /**
     * 是否存在以指定前缀开头的包名。
     *
     * @param prefix 前缀
     * @return 存在返回 {@code true}
     */
    public boolean startsWith(String prefix) {
        var node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return false;
        }
        return true;
    }

    /**
     * 获取当前存储的所有包名。
     *
     * @return 包名集合
     */
    public Set<String> getAllPackages() {
        Set<String> result = new HashSet<>();
        collectAll(root, new StringBuilder(), result);
        return result;
    }

    /**
     * 清空所有包名。
     */
    public void clear() {
        root = new Node();
        size = 0;
    }

    /**
     * 包名数量。
     *
     * @return 包名数量
     */
    public int size() {
        return size;
    }

    /**
     * 是否为空。
     *
     * @return 空返回 {@code true}
     */
    public boolean isEmpty() {
        return size == 0;
    }

    // ------

    private boolean hasShorterPrefix(String target) {
        var node = root;
        for (char c : target.toCharArray()) {
            if (node.isEnd) {
                return true;
            }
            node = node.children.get(c);
            if (node == null) {
                return false;
            }
        }
        return node.isEnd;
    }

    private int removeAllWithPrefix(String prefix) {
        List<String> toRemove = new ArrayList<>();
        collectKeysWithPrefix(root, prefix, new StringBuilder(), toRemove);
        for (var key : toRemove) {
            delete(key);
        }
        return toRemove.size();
    }

    private void collectKeysWithPrefix(@Nullable Node node, String prefix,
                                       StringBuilder current, List<String> result) {
        if (node == null) return;

        if (current.length() >= prefix.length()) {
            if (node.isEnd) {
                result.add(current.toString());
            }
            for (var entry : node.children.entrySet()) {
                current.append(entry.getKey());
                collectKeysWithPrefix(entry.getValue(), prefix, current, result);
                current.deleteCharAt(current.length() - 1);
            }
            return;
        }

        char nextChar = prefix.charAt(current.length());
        var child = node.children.get(nextChar);
        if (child != null) {
            current.append(nextChar);
            collectKeysWithPrefix(child, prefix, current, result);
            current.deleteCharAt(current.length() - 1);
        }
    }

    private boolean delete(String key) {
        return deleteHelper(root, key, 0);
    }

    private boolean deleteHelper(@Nullable Node node, String key, int depth) {
        if (node == null) return false;

        if (depth == key.length()) {
            if (!node.isEnd) return false;
            node.isEnd = false;
            return true;
        }

        char c = key.charAt(depth);
        var child = node.children.get(c);
        return deleteHelper(child, key, depth + 1);
    }

    private void insert(String key) {
        var node = root;
        for (char c : key.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new Node());
        }
        node.isEnd = true;
    }

    private void collectAll(Node node, StringBuilder sb, Set<String> result) {
        if (node.isEnd) {
            result.add(sb.toString());
        }
        for (var entry : node.children.entrySet()) {
            sb.append(entry.getKey());
            collectAll(entry.getValue(), sb, result);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    // ------

    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        boolean isEnd = false;
    }
}
