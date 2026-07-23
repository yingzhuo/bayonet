package com.github.yingzhuo.bayonet.freemarker.autoconfig;

import com.github.yingzhuo.bayonet.freemarker.renderer.FreemarkerStringRendererImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * FreeMarker 模板渲染器的配置属性。
 *
 * <p>前缀为 {@code bayonet.freemarker-template}，支持以下配置项：</p>
 * <ul>
 *   <li>{@code bayonet.freemarker-template.enabled} — 是否启用（默认 {@code true}）</li>
 *   <li>{@code bayonet.freemarker-template.default-encoding} — 编码（默认 {@code UTF-8}）</li>
 *   <li>{@code bayonet.freemarker-template.template-loader-paths} — 模板路径数组（默认 {@code classpath:/templates/}）</li>
 *   <li>{@code bayonet.freemarker-template.suffix} — 模板文件后缀（默认 {@code .ftl}）</li>
 * </ul>
 *
 * @author 应卓
 * @see FreemarkerTemplateAutoConfiguration
 * @see FreemarkerStringRendererImpl
 * @since 4.1.1
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "bayonet.freemarker-template")
public class FreemarkerTemplateProperties implements InitializingBean {

    private boolean enabled = true;
    private String defaultEncoding = "UTF-8";
    private String[] templateLoaderPaths = new String[]{"classpath:/templates/"};
    private String suffix = ".ftl";

    @Override
    public void afterPropertiesSet() {
        if (!StringUtils.hasText(suffix)) {
            suffix = "";
        }
        if (!StringUtils.hasText(defaultEncoding)) {
            defaultEncoding = "UTF-8";
        }
        if (templateLoaderPaths == null || templateLoaderPaths.length == 0) {
            templateLoaderPaths = new String[]{"classpath:/templates/"};
        }
    }

}
