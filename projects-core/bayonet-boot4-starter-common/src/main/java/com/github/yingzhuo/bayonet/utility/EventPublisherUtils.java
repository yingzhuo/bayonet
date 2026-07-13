package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

/**
 * Spring {@link ApplicationEventPublisher} 工具类。
 * <p>提供便捷的静态方法发布应用事件。底层委托给 {@link SpringUtils#getApplicationContext()} 获取 Publisher。
 * 在 Spring 应用上下文就绪前调用将抛出 {@link IllegalStateException}。</p>
 *
 * <pre>{@code
 * EventPublisherUtils.publishEvent(new UserRegisteredEvent(userId));
 * }</pre>
 *
 * @see ApplicationEventPublisher
 * @see SpringUtils
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventPublisherUtils {

    /**
     * 获取 {@link ApplicationEventPublisher} 实例。
     * <p>由当前 {@link org.springframework.context.ApplicationContext ApplicationContext} 充当 Publisher。
     * 需在 Spring 应用上下文就绪后调用。</p>
     *
     * @return ApplicationEventPublisher 实例（非 {@code null}）
     * @throws IllegalStateException 若应用上下文尚未就绪
     */
    public static ApplicationEventPublisher getApplicationEventPublisher() {
        return SpringUtils.getApplicationContext();
    }

    /**
     * 发布应用事件。
     * <p>支持任意 POJO 作为事件对象（Spring 4.2+ 特性），也兼容 {@link org.springframework.context.ApplicationEvent ApplicationEvent} 子类。</p>
     *
     * @param event 事件对象（非 {@code null}）
     * @throws IllegalStateException 若应用上下文尚未就绪
     */
    public static void publishEvent(Object event) {
        Assert.notNull(event, "event must not be null");
        getApplicationEventPublisher().publishEvent(event);
    }

}
