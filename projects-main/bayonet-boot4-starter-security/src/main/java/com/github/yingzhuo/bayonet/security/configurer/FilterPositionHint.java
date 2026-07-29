package com.github.yingzhuo.bayonet.security.configurer;

/**
 * 过滤器定位提示枚举。
 * <p>指定附加过滤器相对于参考过滤器的位置：</p>
 * <ul>
 *   <li>{@link #AT} — 替换参考过滤器</li>
 *   <li>{@link #BEFORE} — 添加到参考过滤器之前</li>
 *   <li>{@link #AFTER} — 添加到参考过滤器之后</li>
 * </ul>
 *
 * @author 应卓
 * @since 4.1.1
 */
public enum FilterPositionHint {

    /**
     * 替换参考过滤器。
     * <p>将附加过滤器放置在参考过滤器的确切位置，替换参考过滤器。</p>
     */
    AT,

    /**
     * 添加到参考过滤器之前。
     * <p>将附加过滤器放置在参考过滤器之前执行。</p>
     */
    BEFORE,

    /**
     * 添加到参考过滤器之后。
     * <p>将附加过滤器放置在参考过滤器之后执行。</p>
     */
    AFTER
}
