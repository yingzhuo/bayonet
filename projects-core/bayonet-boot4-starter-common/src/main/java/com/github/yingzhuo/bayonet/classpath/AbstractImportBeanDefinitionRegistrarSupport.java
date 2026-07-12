package com.github.yingzhuo.bayonet.classpath;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * {@link ImportBeanDefinitionRegistrar} 抽象支持类。
 * <p>提供导入类的 Class、Package、Annotation 等元数据获取工具方法，
 * 强制子类通过构造函数注入 {@link ResourceLoader}、{@link Environment}、
 * {@link BeanFactory}、{@link ClassLoader} 四个依赖。</p>
 *
 * <pre>{@code
 * public class MyRegistrar extends AbstractImportBeanDefinitionRegistrarSupport {
 *     public MyRegistrar(ResourceLoader resourceLoader, Environment environment,
 *                        BeanFactory beanFactory, ClassLoader beanClassLoader) {
 *         super(resourceLoader, environment, beanFactory, beanClassLoader);
 *     }
 *
 *     @Override
 *     public void registerBeanDefinitions(AnnotationMetadata metadata,
 *             BeanDefinitionRegistry registry, BeanNameGenerator generator) {
 *         // 实现注册逻辑
 *     }
 * }
 * }</pre>
 */
public abstract class AbstractImportBeanDefinitionRegistrarSupport implements ImportBeanDefinitionRegistrar {

    protected final ResourceLoader resourceLoader;
    protected final Environment environment;
    protected final BeanFactory beanFactory;
    protected final ClassLoader beanClassLoader;

    /**
     * 构造器。
     *
     * @param resourceLoader  ResourceLoader
     * @param environment     Environment
     * @param beanFactory     BeanFactory
     * @param beanClassLoader ClassLoader
     */
    protected AbstractImportBeanDefinitionRegistrarSupport(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        Assert.notNull(resourceLoader, "resourceLoader must not be null");
        Assert.notNull(environment, "environment must not be null");
        Assert.notNull(beanFactory, "beanFactory must not be null");
        Assert.notNull(beanClassLoader, "beanClassLoader must not be null");

        this.resourceLoader = resourceLoader;
        this.environment = environment;
        this.beanFactory = beanFactory;
        this.beanClassLoader = beanClassLoader;
    }

    /**
     * 注册 Bean 定义（3 参数版本）。
     * <p>子类应覆盖此方法实现自定义注册逻辑。{@link #registerBeanDefinitions(AnnotationMetadata, BeanDefinitionRegistry) 2 参数版本}
     * 已被声明为 {@code final}，不可覆盖。</p>
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
    }

    /**
     * 注册 Bean 定义（2 参数版本，已弃用）。
     * <p>标记为 {@code final} 禁止子类覆盖，统一使用 3 参数版本。</p>
     */
    @Override
    public final void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    }

    // ------

    /**
     * 获取导入类的全限定名。
     *
     * @param metadata ClassMetadata
     * @return 全限定类名
     */
    protected final String getImportingClassName(ClassMetadata metadata) {
        Assert.notNull(metadata, "metadata must not be null");
        return metadata.getClassName();
    }

    /**
     * 获取导入类的 Class 对象。
     * <p>使用 {@link #beanClassLoader} 加载类。</p>
     *
     * @param metadata ClassMetadata
     * @return 导入类的 Class 对象
     */
    protected final Class<?> getImportingClass(ClassMetadata metadata) {
        return ClassUtils.resolveClassName(getImportingClassName(metadata), this.beanClassLoader);
    }

    /**
     * 获取导入类所在的 Package。
     *
     * @param metadata ClassMetadata
     * @return 导入类的 Package
     */
    protected final Package getImportingClassPackage(ClassMetadata metadata) {
        return getImportingClass(metadata).getPackage();
    }

    /**
     * 获取导入类上的指定注解。
     *
     * @param metadata       ClassMetadata
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return 注解实例，不存在时返回 {@code null}
     */
    @Nullable
    protected final <A extends Annotation> A getAnnotationOfImportingClass(ClassMetadata metadata, Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(getImportingClass(metadata), annotationType);
    }

    // ------

    /**
     * 获取导入注解的属性集合（不含容器注解）。
     *
     * @param metadata            AnnotationMetadata
     * @param importingAnnotation 导入注解类型
     * @return 注解属性集合
     */
    protected final Set<AnnotationAttributes> getAnnotationAttributesSet(
            AnnotationMetadata metadata,
            Class<? extends Annotation> importingAnnotation) {
        return getAnnotationAttributesSet(metadata, importingAnnotation, null);
    }

    /**
     * 获取导入注解的属性集合（可选支持容器注解）。
     *
     * @param metadata                     AnnotationMetadata
     * @param importingAnnotation          导入注解类型
     * @param importingContainerAnnotation 容器注解类型（{@code @Repeatable}），可为 {@code null}
     * @return 注解属性集合
     */
    protected final Set<AnnotationAttributes> getAnnotationAttributesSet(
            AnnotationMetadata metadata,
            Class<? extends Annotation> importingAnnotation,
            @Nullable Class<? extends Annotation> importingContainerAnnotation) {

        Assert.notNull(metadata, "metadata must not be null");
        Assert.notNull(importingAnnotation, "importingAnnotation must not be null");

        if (importingContainerAnnotation == null) {
            var attrMap = metadata.getAnnotationAttributes(importingAnnotation.getName(), false);
            return attrMap == null ? Set.of() : Set.of(AnnotationAttributes.fromMap(attrMap));
        }

        return metadata.getMergedRepeatableAnnotationAttributes(importingAnnotation, importingContainerAnnotation, false);
    }

    // ------

    /**
     * 创建 {@link ClassPathScanner}（不启用默认过滤器）。
     *
     * @return ClassPathScanner
     */
    protected final ClassPathScanner createClassPathScanner() {
        return createClassPathScanner(false);
    }

    /**
     * 创建 {@link ClassPathScanner}。
     * <p>使用 registrar 持有的 {@link #resourceLoader}、{@link #environment}、{@link #beanClassLoader}
     * 配置扫描器，确保扫描行为与当前 registrar 上下文一致。</p>
     *
     * @param useDefaultFilters 是否启用 Spring 默认过滤器
     * @return ClassPathScanner
     */
    protected final ClassPathScanner createClassPathScanner(boolean useDefaultFilters) {
        var scanner = new ClassPathScanner(useDefaultFilters);
        scanner.setEnvironment(this.environment);
        scanner.setResourceLoader(this.resourceLoader);
        scanner.setClassLoader(this.beanClassLoader);
        return scanner;
    }

}
