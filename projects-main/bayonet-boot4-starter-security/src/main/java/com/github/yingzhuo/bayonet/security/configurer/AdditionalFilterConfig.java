package com.github.yingzhuo.bayonet.security.configurer;

import jakarta.servlet.Filter;

/**
 * 附加过滤器配置记录。
 * <p>承载 {@link AdditionalSecurityFilter @AdditionalSecurityFilter} 注解解析后的配置信息，
 * 包含过滤器类型、定位参考过滤器类型和定位提示。</p>
 *
 * @param filterType         要添加的过滤器类型
 * @param positionFilterType 定位参考的过滤器类型
 * @param hint               定位提示（AT / BEFORE / AFTER）
 * @author 应卓
 * @since 4.1.1
 */
public record AdditionalFilterConfig(
        Class<? extends Filter> filterType,
        Class<? extends Filter> positionFilterType,
        FilterPositionHint hint
) {
}
