package com.github.yingzhuo.bayonet.classpath;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PackageTrieTest {

    // ============== add ==============

    @Test
    void should_add_package() {
        var trie = new PackageTrie();
        assertThat(trie.add("com.example")).isTrue();
        assertThat(trie.contains("com.example")).isTrue();
    }

    @Test
    void should_reject_duplicate() {
        var trie = new PackageTrie();
        assertThat(trie.add("com.example")).isTrue();
        assertThat(trie.add("com.example")).isFalse();
    }

    // ============== 前缀吸收 ==============

    @Test
    void should_absorb_longer_when_shorter_added_first() {
        var trie = new PackageTrie();
        trie.add("com.example");
        assertThat(trie.add("com.example.foo")).isFalse();
        assertThat(trie.contains("com.example.foo")).isFalse();
        assertThat(trie.contains("com.example")).isTrue();
    }

    @Test
    void should_absorb_shorter_when_longer_added_first() {
        var trie = new PackageTrie();
        trie.add("com.example.foo");
        assertThat(trie.add("com.example")).isTrue();
        assertThat(trie.contains("com.example.foo")).isFalse();
        assertThat(trie.contains("com.example")).isTrue();
    }

    @Test
    void should_absorb_nested_packages() {
        var trie = new PackageTrie();
        trie.add("com.a.b");
        trie.add("com.a.b.c");
        assertThat(trie.add("com.a")).isTrue();
        assertThat(trie.getAllPackages()).containsExactly("com.a");
    }

    @Test
    void should_keep_siblings() {
        var trie = new PackageTrie();
        trie.add("com.example.foo");
        trie.add("com.example.bar");
        assertThat(trie.getAllPackages()).containsExactlyInAnyOrder("com.example.bar", "com.example.foo");
    }

    // ============== contains ==============

    @Test
    void should_return_false_when_not_contains() {
        var trie = new PackageTrie();
        assertThat(trie.contains("com.example")).isFalse();
    }

    @Test
    void should_return_false_for_absorbed_package() {
        var trie = new PackageTrie();
        trie.add("com");
        trie.add("com.example");  // rejected
        assertThat(trie.contains("com.example")).isFalse();
    }

    // ============== startsWith ==============

    @Test
    void should_check_prefix() {
        var trie = new PackageTrie();
        trie.add("com.example.service");
        assertThat(trie.startsWith("com.example")).isTrue();
        assertThat(trie.startsWith("com.other")).isFalse();
    }

    @Test
    void should_return_false_when_startsWith_on_empty_trie() {
        var trie = new PackageTrie();
        assertThat(trie.startsWith("com")).isFalse();
    }

    @Test
    void startsWith_should_match_full_package() {
        var trie = new PackageTrie();
        trie.add("com.example");
        assertThat(trie.startsWith("com.example")).isTrue();
    }

    // ============== getAllPackages ==============

    @Test
    void getAllPackages_should_be_empty_initially() {
        var trie = new PackageTrie();
        assertThat(trie.getAllPackages()).isEmpty();
    }

    @Test
    void getAllPackages_should_return_all() {
        var trie = new PackageTrie();
        trie.add("com.example.foo");
        trie.add("com.example.bar");
        assertThat(trie.getAllPackages()).hasSize(2);
    }

    // ============== 复杂场景 ==============

    @Test
    void should_handle_multiple_levels() {
        var trie = new PackageTrie();
        trie.add("a");
        trie.add("a.b");
        trie.add("a.b.c");

        assertThat(trie.contains("a")).isTrue();
        assertThat(trie.contains("a.b")).isFalse();
        assertThat(trie.contains("a.b.c")).isFalse();
        assertThat(trie.getAllPackages()).containsExactly("a");
    }

    @Test
    void should_handle_independent_branches() {
        var trie = new PackageTrie();
        trie.add("com.example.foo");
        trie.add("org.other.bar");
        trie.add("net.test");

        assertThat(trie.getAllPackages()).hasSize(3);
        assertThat(trie.contains("com.example.foo")).isTrue();
        assertThat(trie.contains("org.other.bar")).isTrue();
        assertThat(trie.contains("net.test")).isTrue();
    }

    @Test
    void should_handle_single_character() {
        var trie = new PackageTrie();
        assertThat(trie.add("x")).isTrue();
        assertThat(trie.contains("x")).isTrue();
        assertThat(trie.contains("y")).isFalse();
    }

    @Test
    void should_handle_add_after_absorption() {
        var trie = new PackageTrie();
        trie.add("com.example.foo");

        // com.example 吸收 com.example.foo
        trie.add("com.example");
        assertThat(trie.contains("com.example.foo")).isFalse();

        // 可以再添加一个独立的分支
        assertThat(trie.add("com.other")).isTrue();
        assertThat(trie.getAllPackages()).containsExactlyInAnyOrder("com.example", "com.other");
    }

    // ============== acceptPackages(String...) ==============

    @Test
    void should_accept_strings() {
        var trie = new PackageTrie();
        trie.acceptPackages("com.example.foo", "com.example.bar");
        assertThat(trie.contains("com.example.foo")).isTrue();
        assertThat(trie.contains("com.example.bar")).isTrue();
    }

    @Test
    void should_ignore_null_string_varargs() {
        var trie = new PackageTrie();
        trie.acceptPackages((String[]) null);
        assertThat(trie.isEmpty()).isTrue();
    }

    @Test
    void should_handle_empty_string_varargs() {
        var trie = new PackageTrie();
        trie.acceptPackages(new String[0]);
        assertThat(trie.isEmpty()).isTrue();
    }

    // ============== acceptPackages(Package...) ==============

    @Test
    void should_accept_package_objects() {
        var trie = new PackageTrie();
        trie.acceptPackages(String.class.getPackage(), getClass().getPackage());
        assertThat(trie.contains("java.lang")).isTrue();
        assertThat(trie.contains("com.github.yingzhuo.bayonet.classpath")).isTrue();
    }

    @Test
    void should_ignore_null_package_varargs() {
        var trie = new PackageTrie();
        trie.acceptPackages((Package[]) null);
        assertThat(trie.isEmpty()).isTrue();
    }

    // ============== acceptBasePackageClasses ==============

    @Test
    void should_accept_classes() {
        var trie = new PackageTrie();
        trie.acceptBasePackageClasses(PackageTrieTest.class);
        assertThat(trie.contains("com.github.yingzhuo.bayonet.classpath")).isTrue();
    }

    @Test
    void should_skip_primitive_types() {
        var trie = new PackageTrie();
        trie.acceptBasePackageClasses(int.class, String.class);
        assertThat(trie.contains("java.lang")).isTrue();
        assertThat(trie.size()).isOne();
    }

    @Test
    void should_handle_null_class_varargs() {
        var trie = new PackageTrie();
        trie.acceptBasePackageClasses((Class<?>[]) null);
        assertThat(trie.isEmpty()).isTrue();
    }

    // ============== iterator ==============

    @Test
    void should_iterate_packages() {
        var trie = new PackageTrie();
        trie.add("b");
        trie.add("a");

        var list = new java.util.ArrayList<String>();
        for (var pkg : trie) {
            list.add(pkg);
        }
        assertThat(list).containsExactlyInAnyOrder("a", "b");
    }

    // ============== clear / size / isEmpty ==============

    @Test
    void should_clear_all() {
        var trie = new PackageTrie();
        trie.add("com.example");
        trie.clear();
        assertThat(trie.isEmpty()).isTrue();
        assertThat(trie.size()).isZero();
    }

    @Test
    void should_report_size() {
        var trie = new PackageTrie();
        trie.add("a");
        trie.add("b");
        assertThat(trie.size()).isEqualTo(2);
    }

    @Test
    void should_be_empty_initially() {
        var trie = new PackageTrie();
        assertThat(trie.isEmpty()).isTrue();
    }

    // ============== add with null/blank ==============

    @Test
    void should_reject_null() {
        assertThat(new PackageTrie().add(null)).isFalse();
    }

    @Test
    void should_reject_blank() {
        assertThat(new PackageTrie().add("  ")).isFalse();
    }

}
