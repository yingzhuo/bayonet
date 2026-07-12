package com.github.yingzhuo.bayonet.security.filtercfg;

import jakarta.servlet.Filter;

/**
 * 过滤器配置器。
 * <p>实现此接口并注册为 Spring Bean，即可通过 {@link com.github.yingzhuo.bayonet.security.autoconfig.SecurityFilterAutoDSL SecurityFilterAutoDSL}
 * 自动将过滤器添加到 Security 过滤器链的指定位置。</p>
 *
 * <pre>{@code
 * @Component
 * public class MyFilterConfigurer implements FilterConfigurer {
 *     public Filter getFilter() { return new MyFilter(); }
 *     public Class<? extends Filter> getPositionFilterClass() { return UsernamePasswordAuthenticationFilter.class; }
 *     public PositionHint getPositionHint() { return PositionHint.BEFORE; }
 * }
 * }</pre>
 */
public interface FilterConfigurer {

    /**
     * 返回要添加的过滤器实例。
     *
     * @return 过滤器实例
     */
    Filter getFilter();

    /**
     * 返回定位参考过滤器的类型。
     * <p>过滤器将相对于此类型所对应的过滤器进行定位。</p>
     *
     * @return 参考过滤器 Class
     */
    Class<? extends Filter> getPositionFilterClass();

    /**
     * 返回定位提示，决定在参考过滤器的 {@link PositionHint#BEFORE 前面}、{@link PositionHint#AFTER 后面} 还是 {@link PositionHint#AT 位置} 添加。
     *
     * @return 定位提示
     */
    PositionHint getPositionHint();

    // ------

    /**
     * 过滤器定位提示。
     */
    enum PositionHint {
        /**
         * 在参考过滤器之前添加。
         */
        BEFORE,
        /**
         * 在参考过滤器之后添加。
         */
        AFTER,
        /**
         * 替换参考过滤器。
         */
        AT
    }

}
