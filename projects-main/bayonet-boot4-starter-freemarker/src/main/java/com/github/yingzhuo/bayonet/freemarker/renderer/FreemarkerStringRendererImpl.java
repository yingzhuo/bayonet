package com.github.yingzhuo.bayonet.freemarker.renderer;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FreeMarker 字符串模板渲染器实现。
 *
 * <p>基于 FreeMarker {@link Configuration}，支持 {@code classpath:} 和 {@code file:} 前缀的模板路径。
 * 通过 {@link #templateLoaderPaths} 配置多个模板加载路径，按顺序搜索。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * // 默认配置：classpath:/templates/，后缀 .ftl
 * var renderer = new FreemarkerStringRendererImpl();
 *
 * // 自定义路径和后缀
 * renderer.setTemplateLoaderPaths(new String[]{"classpath:/my-templates/", "file:/opt/templates/"});
 * renderer.setSuffix(".html");
 * renderer.afterPropertiesSet();
 *
 * String result = renderer.render("greeting", Map.of("name", "World"));
 * }</pre>
 *
 * @author 应卓
 * @see FreemarkerStringRenderer
 * @see Configuration
 * @since 4.1.1
 */
public class FreemarkerStringRendererImpl implements FreemarkerStringRenderer, InitializingBean {

    private Configuration cfg;

    private @Setter String defaultEncoding = "UTF-8";
    private @Setter String[] templateLoaderPaths = new String[]{"classpath:/templates/"};
    private @Setter String suffix = ".ftl";

    @Override
    public String render(String templateName, @Nullable Object data) {
        Assert.hasText(templateName, "template name is required");

        if (data == null) {
            data = Map.<String, Object>of();
        }

        try (Writer writer = new StringWriter()) {
            var template = cfg.getTemplate(templateName + suffix);
            template.process(data, writer);
            return writer.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (TemplateException e) {
            throw new IllegalStateException("FreeMarker template processing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        this.cfg = new Configuration(Configuration.VERSION_2_3_34);
        try {
            this.cfg.setTemplateLoader(getTemplateLoader());
            this.cfg.setDefaultEncoding(defaultEncoding);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private MultiTemplateLoader getTemplateLoader() throws IOException {
        Assert.notEmpty(this.templateLoaderPaths, "templateLoaderPaths must not be empty");
        Assert.noNullElements(this.templateLoaderPaths, "templateLoaderPaths must not contain null elements");

        List<TemplateLoader> loaders = new ArrayList<>();
        for (String rawPath : this.templateLoaderPaths) {
            Assert.hasText(rawPath, "templateLoaderPaths element must not be blank");

            if (rawPath.startsWith("classpath:")) {
                var path = rawPath.substring("classpath:".length());
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                if (!path.endsWith("/")) {
                    path += "/";
                }
                loaders.add(new ClassTemplateLoader(this.getClass(), path));
                continue;
            }

            if (rawPath.startsWith("file:")) {
                var path = rawPath.substring("file:".length());
                if (!path.endsWith("/")) {
                    path += "/";
                }
                loaders.add(new FileTemplateLoader(new File(path), true));
                continue;
            }

            throw new IllegalArgumentException("Unsupported path prefix in '" + rawPath
                    + "'; expected 'classpath:' or 'file:'");
        }

        return new MultiTemplateLoader(loaders.toArray(new TemplateLoader[0]));
    }
}
