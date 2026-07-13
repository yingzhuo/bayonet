package com.github.yingzhuo.bayonet.classpath;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

/**
 * 类路径扫描工作器。
 * <p>继承 {@link ClassPathScanningCandidateComponentProvider}，重写组件候选判定逻辑。
 * 默认只接受独立（非内部匿名类）且非注解的类作为候选组件。</p>
 *
 * <pre>{@code
 * var scanner = new ClassPathScannerWorker();
 * scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
 * var beans = scanner.findCandidateComponents("com.example");
 * }</pre>
 */
public class ClassPathScannerWorker extends ClassPathScanningCandidateComponentProvider {

    /**
     * 构造器
     *
     * @param useDefaultFilters 是否启用 Spring 默认过滤器
     */
    public ClassPathScannerWorker(boolean useDefaultFilters) {
        super(useDefaultFilters);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent()
                && !beanDefinition.getMetadata().isAnnotation();
    }

}
