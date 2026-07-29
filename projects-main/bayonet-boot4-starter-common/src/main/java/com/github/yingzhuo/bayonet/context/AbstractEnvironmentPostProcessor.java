package com.github.yingzhuo.bayonet.context;

import org.apache.commons.logging.Log;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * {@link EnvironmentPostProcessor} 抽象基类。
 * <p>模板方法封装 {@link DeferredLogFactory} 的创建，子类只需实现 {@link #doProcess(Log, ConfigurableEnvironment)}
 * 即可使用延迟初始化的日志实例。</p>
 *
 * @author 应卓
 * @see DeferredLogFactory
 * @since 4.1.1
 */
public abstract class AbstractEnvironmentPostProcessor implements EnvironmentPostProcessor {

    protected final DeferredLogFactory logFactory;

    /**
     * 构造器
     *
     * @param logFactory 延迟日志工厂，由 Spring Boot 自动注入
     */
    protected AbstractEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.logFactory = logFactory;
    }

    /**
     * 后置处理环境（模板方法，标记为 {@code final} 禁止子类覆写）。
     *
     * @param environment 环境
     * @param application Spring 应用
     */
    @Override
    public final void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        doProcess(logFactory.getLog(getClass()), environment);
    }

    /**
     * 执行环境后置处理。
     *
     * @param log         延迟初始化的日志实例
     * @param environment 环境
     */
    protected abstract void doProcess(Log log, ConfigurableEnvironment environment);
}
