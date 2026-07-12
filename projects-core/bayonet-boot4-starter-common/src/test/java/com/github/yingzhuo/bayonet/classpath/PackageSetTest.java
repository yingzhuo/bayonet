package com.github.yingzhuo.bayonet.classpath;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PackageSetTest {

    // ============== acceptPackages(String...) ==============

    @Test
    void should_accept_strings() {
        var set = new PackageSet()
                .acceptPackages("com.example.foo", "com.example.bar")
                .asSet();
        assertThat(set).containsExactly("com.example.bar", "com.example.foo");
    }

    @Test
    void should_ignore_null_strings() {
        var set = new PackageSet()
                .acceptPackages((String) null)
                .asSet();
        assertThat(set).isEmpty();
    }

    @Test
    void should_ignore_blank_strings() {
        var set = new PackageSet()
                .acceptPackages("", "  ", "com.example.foo")
                .asSet();
        assertThat(set).containsExactly("com.example.foo");
    }

    @Test
    void should_trim_strings() {
        var set = new PackageSet()
                .acceptPackages("  com.example.foo  ")
                .asSet();
        assertThat(set).containsExactly("com.example.foo");
    }

    @Test
    void should_ignore_null_string_varargs() {
        String[] nullArray = null;
        var set = new PackageSet()
                .acceptPackages(nullArray)
                .asSet();
        assertThat(set).isEmpty();
    }

    // ============== acceptPackages(Package...) ==============

    @Test
    void should_accept_packages() {
        var set = new PackageSet()
                .acceptPackages(String.class.getPackage(), java.util.List.class.getPackage())
                .asSet();
        assertThat(set).contains("java.lang", "java.util");
    }

    @Test
    void should_ignore_null_package_varargs() {
        Package[] nullArray = null;
        var set = new PackageSet()
                .acceptPackages(nullArray)
                .asSet();
        assertThat(set).isEmpty();
    }

    // ============== acceptBasePackageClasses(Class<?>...) ==============

    @Test
    void should_accept_classes() {
        var set = new PackageSet()
                .acceptBasePackageClasses(PackageSetTest.class)
                .asSet();
        assertThat(set).contains("com.github.yingzhuo.bayonet.classpath");
    }

    @Test
    void should_skip_primitive_types() {
        var set = new PackageSet()
                .acceptBasePackageClasses(int.class, void.class, boolean.class, String.class)
                .asSet();
        assertThat(set).containsExactly("java.lang");
    }

    @Test
    void should_skip_null_classes() {
        var set = new PackageSet()
                .acceptBasePackageClasses((Class<?>) null)
                .asSet();
        assertThat(set).isEmpty();
    }

    @Test
    void should_ignore_null_class_varargs() {
        Class<?>[] nullArray = null;
        var set = new PackageSet()
                .acceptBasePackageClasses(nullArray)
                .asSet();
        assertThat(set).isEmpty();
    }

    // ============== isEmpty / size ==============

    @Test
    void should_be_empty_initially() {
        var set = new PackageSet();
        assertThat(set.isEmpty()).isTrue();
        assertThat(set.size()).isZero();
    }

    @Test
    void should_report_size() {
        var set = new PackageSet()
                .acceptPackages("a", "b", "c");
        assertThat(set.isEmpty()).isFalse();
        assertThat(set.size()).isEqualTo(3);
    }

    // ============== clear ==============

    @Test
    void should_clear_all_packages() {
        var set = new PackageSet()
                .acceptPackages("com.example.foo")
                .clear();
        assertThat(set.isEmpty()).isTrue();
    }

    @Test
    void clear_should_return_this() {
        var set = new PackageSet();
        assertThat(set.clear()).isSameAs(set);
    }

    // ============== asSet 不可修改 ==============

    @Test
    void asSet_should_be_unmodifiable() {
        var set = new PackageSet().acceptPackages("com.example.foo");
        var unmod = set.asSet();
        assertThatThrownBy(() -> unmod.add("com.example.bar"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // ============== iterable ==============

    @Test
    void should_iterate_packages() {
        var set = new PackageSet()
                .acceptPackages("b", "a");
        assertThat(set).containsExactly("a", "b");
    }

    // ============== 去重 ==============

    @Test
    void should_deduplicate() {
        var set = new PackageSet()
                .acceptPackages("com.example.foo", "com.example.foo")
                .asSet();
        assertThat(set).hasSize(1);
    }

}
