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
    void scan_should_returnEmptySet_when_emptyPackageSet() {
        assertThat(scanner.scan(new PackageSet())).isEmpty();
    }

    // ============== scan ==============

    @Test
    void scan_should_findClasses_and_loadBeanClass() {
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);

        var result = scanner.scan(new PackageSet().acceptPackages("com.github.yingzhuo.bayonet.classpath"));

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
        // 不抛异常即为通过
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        assertThat(scanner.scan(new PackageSet().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

    @Test
    void setResourceLoader_should_useCustom_when_notNull() {
        scanner.setResourceLoader(new DefaultResourceLoader());
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        assertThat(scanner.scan(new PackageSet().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

    // ============== setEnvironment(null) ==============

    @Test
    void setEnvironment_should_useDefault_when_null() {
        scanner.setEnvironment(null);
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        assertThat(scanner.scan(new PackageSet().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

    // ============== setClassLoader(null) ==============

    @Test
    void setClassLoader_should_throw_when_null() {
        assertThatThrownBy(() -> scanner.setClassLoader(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== addIncludeFilters(null) ==============

    @Test
    void addIncludeFilters_should_ignore_null() {
        scanner.addIncludeFilters((TypeFilter[]) null);
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        // 不抛异常即为通过
        assertThat(scanner.scan(new PackageSet().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isNotEmpty();
    }

    // ============== addExcludeFilters(null) ==============

    @Test
    void addExcludeFilters_should_ignore_null() {
        scanner.addExcludeFilters((TypeFilter[]) null);
        // 不抛异常即为通过
        assertThat(scanner.scan(new PackageSet().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isEmpty();
    }

    // ============== resetFilters ==============

    @Test
    void resetFilters_should_clear_allFilters() {
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        scanner.resetFilters();

        assertThat(scanner.scan(new PackageSet().acceptPackages("com.github.yingzhuo.bayonet.classpath")))
                .isEmpty();
    }

    // ============== 返回不可修改集 ==============

    @Test
    void scan_should_return_unmodifiableSet() {
        scanner.addIncludeFilters((metadataReader, metadataReaderFactory) -> true);
        var result = scanner.scan(new PackageSet().acceptPackages("com.github.yingzhuo.bayonet.classpath"));
        assertThatThrownBy(result::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }

}
