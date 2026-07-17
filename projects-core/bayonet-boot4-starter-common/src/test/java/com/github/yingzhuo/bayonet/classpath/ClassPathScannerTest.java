package com.github.yingzhuo.bayonet.classpath;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.type.filter.TypeFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClassPathScannerTest {

    private final ClassPathScanner scanner = new ClassPathScanner();

    // ============== scan(null / empty) ==============

    @Test
    void scan_should_returnEmptySet_when_null() {
        assertThat(scanner.scan(null)).isEmpty();
    }

    @Test
    void scan_should_returnEmptySet_when_emptyPackageTrie() {
        assertThat(scanner.scan(new PackageTrie())).isEmpty();
    }

    // ============== scan ==============

    @Test
    void scan_should_findClasses_and_loadBeanClass() {
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);

        var result = scanner.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath"));

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(bd -> {
            var bc = bd.getBeanClass();
            return bc != null && bc.getName().equals(bd.getBeanClassName());
        });
    }

    // ============== setResourceLoader(null) ==============

    @Test
    void setResourceLoader_should_useDefault_when_null() {
        scanner.setResourceLoader(null);
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        assertThat(scanner.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

    @Test
    void setResourceLoader_should_useCustom_when_notNull() {
        scanner.setResourceLoader(new DefaultResourceLoader());
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        assertThat(scanner.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

    // ============== setEnvironment(null) ==============

    @Test
    void setEnvironment_should_useDefault_when_null() {
        scanner.setEnvironment(null);
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        assertThat(scanner.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

    // ============== setClassLoader ==============

    @Test
    void setClassLoader_should_throw_when_null() {
        assertThatThrownBy(() -> scanner.setClassLoader(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void setClassLoader_should_useCustom_when_notNull() {
        scanner.setClassLoader(getClass().getClassLoader());
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        assertThat(scanner.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

    // ============== addIncludeFilters(null) ==============

    @Test
    void addIncludeFilters_should_ignore_null() {
        scanner.addIncludeFilters((TypeFilter[]) null);
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        assertThat(scanner.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

    // ============== addExcludeFilters(null) ==============

    @Test
    void addExcludeFilters_should_ignore_null() {
        scanner.addExcludeFilters((TypeFilter[]) null);
        assertThat(scanner.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isEmpty();
    }

    // ============== resetFilters ==============

    @Test
    void resetFilters_should_clear_allFilters() {
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        scanner.resetFilters();

        assertThat(scanner.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isEmpty();
    }

    @Test
    void resetFilters_withDefault_should_useDefaultFilters() {
        scanner.resetFilters(true);
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        assertThat(scanner.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

    // ============== 返回不可修改集 ==============

    @Test
    void scan_should_return_unmodifiableSet() {
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        var result = scanner.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath"));
        assertThatThrownBy(result::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // ============== useDefaultFilters 构造器 ==============

    @Test
    void should_use_defaultFilters_when_constructed_with_true() {
        var s = new ClassPathScanner(true);
        assertThat(s.scan(new PackageTrie().acceptPackages("java.lang"))).isEmpty();
    }

    @Test
    void should_not_use_defaultFilters_when_constructed_with_false() {
        var s = new ClassPathScanner(false);
        s.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        assertThat(s.scan(new PackageTrie().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

}
