package com.github.yingzhuo.bayonet.freemarker.autoconfig;

import com.github.yingzhuo.bayonet.freemarker.renderer.FreemarkerStringRenderer;
import com.github.yingzhuo.bayonet.freemarker.renderer.FreemarkerStringRendererImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * FreeMarker 模板渲染器的自动配置。
 *
 * <p>当 {@code bayonet.freemarker-template.enabled} 未明确设置为 {@code false} 时，
 * 自动装配 {@link FreemarkerStringRenderer} Bean。
 * 可通过 {@code application.properties} 或 {@code application.yml} 配置模板路径、编码和后缀。</p>
 *
 * <p><b>默认配置</b></p>
 * <ul>
 *   <li>模板路径：{@code classpath:/templates/}</li>
 *   <li>模板后缀：{@code .ftl}</li>
 *   <li>编码：{@code UTF-8}</li>
 * </ul>
 *
 * @author 应卓
 * @see FreemarkerStringRenderer
 * @see FreemarkerStringRendererImpl
 * @see FreemarkerTemplateProperties
 * @since 4.1.1
 */
@AutoConfiguration
@EnableConfigurationProperties(FreemarkerTemplateProperties.class)
@ConditionalOnProperty(prefix = "bayonet.freemarker-template", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FreemarkerTemplateAutoConfiguration {

    /**
     * 创建 {@link FreemarkerStringRenderer} Bean。
     *
     * @param properties 模板配置属性
     * @return 渲染器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public FreemarkerStringRenderer stringTemplateRenderer(FreemarkerTemplateProperties properties) {
        var bean = new FreemarkerStringRendererImpl();
        bean.setSuffix(properties.getSuffix());
        bean.setDefaultEncoding(properties.getDefaultEncoding());
        bean.setTemplateLoaderPaths(properties.getTemplateLoaderPaths());
        return bean;
    }

}
