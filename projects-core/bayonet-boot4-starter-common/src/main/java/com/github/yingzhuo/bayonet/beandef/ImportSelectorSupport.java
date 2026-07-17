package com.github.yingzhuo.bayonet.beandef;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

/**
 * {@link ImportSelector} 抽象支持类。
 * <p>提供导入类的 Class、Package、Annotation 等元数据获取工具方法（继承自 {@link AbstractImportingSupport}），
 * 强制子类通过构造函数注入 {@link ResourceLoader}、{@link Environment}、
 * {@link BeanFactory}、{@link ClassLoader} 四个依赖。</p>
 *
 * <pre>{@code
 * public class MyImportSelector extends ImportSelectorSupport {
 *     public MyImportSelector(ResourceLoader resourceLoader, Environment environment,
 *                             BeanFactory beanFactory, ClassLoader beanClassLoader) {
 *         super(resourceLoader, environment, beanFactory, beanClassLoader);
 *     }
 *
 *     @Override
 *     public String[] selectImports(AnnotationMetadata metadata) {
 *         // 实现导入逻辑
 *         return new String[] { MyConfiguration.class.getName() };
 *     }
 * }
 * }</pre>
 *
 * @see BeanDefinitionRegistrarSupport
 */
public abstract class ImportSelectorSupport extends AbstractImportingSupport implements ImportSelector {

    /**
     * 构造器。
     *
     * @param resourceLoader  ResourceLoader
     * @param environment     Environment
     * @param beanFactory     BeanFactory
     * @param beanClassLoader ClassLoader
     */
    protected ImportSelectorSupport(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        super(resourceLoader, environment, beanFactory, beanClassLoader);
    }

}
