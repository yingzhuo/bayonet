package com.github.yingzhuo.bayonet.classpath;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.io.ApplicationResourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 类路径扫描器。
 * <p>封装 {@link ClassPathScannerWorker}，提供更简洁的包扫描 API。
 * 支持自定义 ResourceLoader、Environment、ClassLoader 和过滤器，
 * 并自动将扫描到的候选组件转换为 {@link GenericBeanDefinition}。</p>
 *
 * <pre>{@code
 * var scanner = new ClassPathScanner();
 * scanner.addIncludeFilters(new AnnotationTypeFilter(Component.class));
 * var result = scanner.scan(new PackageSet().acceptPackages("com.example"));
 * }</pre>
 */
public class ClassPathScanner {

    private static final Logger log = LoggerFactory.getLogger(ClassPathScanner.class);

    private final ClassPathScannerWorker worker;
    private ClassLoader classLoader = ClassPathScanner.class.getClassLoader();

    /**
     * 构造器，默认不启用 Spring 内置过滤器。
     */
    public ClassPathScanner() {
        this(false);
    }

    /**
     * 构造器。
     *
     * @param useDefaultFilters 是否启用 Spring 默认过滤器
     */
    public ClassPathScanner(boolean useDefaultFilters) {
        this.worker = new ClassPathScannerWorker(useDefaultFilters);
    }

    /**
     * 设置 ResourceLoader，{@code null} 时使用默认的 {@link ApplicationResourceLoader}。
     *
     * @param resourceLoader ResourceLoader，可为 {@code null}
     */
    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        worker.setResourceLoader(Objects.requireNonNullElseGet(resourceLoader, ApplicationResourceLoader::get));
    }

    /**
     * 设置 Environment，{@code null} 时使用默认的 {@link StandardEnvironment}。
     *
     * @param environment Environment，可为 {@code null}
     */
    public void setEnvironment(@Nullable Environment environment) {
        worker.setEnvironment(Objects.requireNonNullElseGet(environment, StandardEnvironment::new));
    }

    /**
     * 设置 ClassLoader，不能为 {@code null}。
     *
     * @param classLoader ClassLoader
     * @throws IllegalArgumentException 若 {@code classLoader} 为 {@code null}
     */
    public void setClassLoader(ClassLoader classLoader) {
        Assert.notNull(classLoader, "classLoader must not be null");
        this.classLoader = classLoader;
    }

    /**
     * 重置过滤器（不启用默认过滤器）。
     */
    public void resetFilters() {
        worker.resetFilters(false);
    }

    /**
     * 重置过滤器。
     *
     * @param useDefaultFilters 重置后是否启用默认过滤器
     */
    public void resetFilters(boolean useDefaultFilters) {
        worker.resetFilters(useDefaultFilters);
    }

    /**
     * 添加包含过滤器（类型过滤器）。
     *
     * @param includeFilters 包含过滤器，{@code null} 元素将被忽略
     */
    public void addIncludeFilters(@Nullable TypeFilter... includeFilters) {
        if (includeFilters != null) {
            Stream.of(includeFilters)
                    .filter(Objects::nonNull)
                    .forEach(worker::addIncludeFilter);
        }
    }

    /**
     * 添加排除过滤器（类型过滤器）。
     *
     * @param excludeFilters 排除过滤器，{@code null} 元素将被忽略
     */
    public void addExcludeFilters(@Nullable TypeFilter... excludeFilters) {
        if (excludeFilters != null) {
            Stream.of(excludeFilters)
                    .filter(Objects::nonNull)
                    .forEach(worker::addExcludeFilter);
        }
    }

    /**
     * 扫描指定包下的候选组件。
     * <p>返回的 {@link GenericBeanDefinition} 中 {@link GenericBeanDefinition#getBeanClass() BeanClass}
     * 已通过 ClassLoader 加载到 JVM 中。如果某个类无法加载（类不存在、依赖缺失等），该定义将被静默丢弃。</p>
     *
     * @param packageSet 待扫描的包名集合，{@code null} 或空集合时返回空集
     * @return 不可修改的 {@link GenericBeanDefinition} 集合
     */
    public Set<GenericBeanDefinition> scan(@Nullable PackageSet packageSet) {
        if (packageSet == null || packageSet.isEmpty()) {
            return Set.of();
        }

        var set = new HashSet<GenericBeanDefinition>();

        for (var basePackage : packageSet) {
            worker.findCandidateComponents(basePackage)
                    .stream()
                    .map(bd -> bd instanceof GenericBeanDefinition g ? g : new GenericBeanDefinition(bd))
                    .forEach(set::add);
        }

        var result = new HashSet<GenericBeanDefinition>();
        for (var beanDef : set) {
            if (resolveBeanClass(beanDef)) {
                result.add(beanDef);
            }
        }

        return Collections.unmodifiableSet(result);
    }

    /**
     * 尝试加载 BeanClass，失败时丢弃该定义。
     *
     * @param beanDef 待解析的 BeanDefinition
     * @return 加载成功返回 {@code true}，失败返回 {@code false}
     */
    private boolean resolveBeanClass(GenericBeanDefinition beanDef) {
        var className = beanDef.getBeanClassName();
        if (className == null) {
            return false;
        }
        try {
            var clazz = ClassUtils.resolveClassName(className, this.classLoader);
            beanDef.setBeanClass(clazz);
            return true;
        } catch (Exception e) {
            log.trace("Failed to resolve bean class: {}", className, e);
            return false;
        }
    }

}
