package com.github.yingzhuo.bayonet.freemarker.renderer;

import org.jspecify.annotations.Nullable;

/**
 * FreeMarker 字符串模板渲染器。
 *
 * <p>将指定的 FreeMarker 模板渲染为字符串结果。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * String result = renderer.render("greeting", Map.of("name", "World"));
 * }</pre>
 *
 * @author 应卓
 * @see FreemarkerStringRendererImpl
 * @since 4.1.1
 */
@FunctionalInterface
public interface FreemarkerStringRenderer {

    /**
     * 渲染模板（无数据模型）。
     *
     * @param templateName 模板名称，不含后缀
     * @return 渲染后的字符串
     */
    default String render(String templateName) {
        return render(templateName, null);
    }

    /**
     * 渲染模板。
     *
     * @param templateName 模板名称，不含后缀
     * @param data         数据模型，可为 {@code null}
     * @return 渲染后的字符串
     */
    String render(String templateName, @Nullable Object data);

}
