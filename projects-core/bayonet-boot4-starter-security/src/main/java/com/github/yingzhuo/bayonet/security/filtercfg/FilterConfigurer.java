package com.github.yingzhuo.bayonet.security.filtercfg;

import com.github.yingzhuo.bayonet.security.autoconfig.SecurityFilterAutoDSL;
import jakarta.servlet.Filter;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

/**
 * 过滤器配置器。
 * <p>实现此接口并注册为 Spring Bean，即可通过 {@link SecurityFilterAutoDSL }
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
 * @author 应卓
 */
public interface FilterConfigurer {

    /**
     * 创建默认配置构建器。
     *
     * @return 构建器
     */
    static Builder builder() {
        return new Builder();
    }

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

    // ------

    /**
     * 默认实现，使用 record 承载不可变数据。
     */
    record SimpleFilterConfigurer(
            Filter filter,
            Class<? extends Filter> positionFilterClass,
            PositionHint positionHint
    ) implements FilterConfigurer {

        public Filter getFilter() {
            return filter();
        }

        public Class<? extends Filter> getPositionFilterClass() {
            return positionFilterClass();
        }

        public PositionHint getPositionHint() {
            return positionHint();
        }
    }

    // ------

    /**
     * {@link FilterConfigurer} 构建器。
     */
    class Builder {

        private @Nullable Filter filter;
        private @Nullable Class<? extends Filter> positionFilterClass;
        private @Nullable PositionHint positionHint;

        public Builder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public Builder positionFilterClass(Class<? extends Filter> positionFilterClass) {
            this.positionFilterClass = positionFilterClass;
            return this;
        }

        public Builder positionHint(PositionHint positionHint) {
            this.positionHint = positionHint;
            return this;
        }

        public FilterConfigurer build() {
            Assert.notNull(filter, "filter must not be null");
            Assert.notNull(positionFilterClass, "positionFilterClass must not be null");
            Assert.notNull(positionHint, "positionHint must not be null");
            return new SimpleFilterConfigurer(filter, positionFilterClass, positionHint);
        }
    }

}
