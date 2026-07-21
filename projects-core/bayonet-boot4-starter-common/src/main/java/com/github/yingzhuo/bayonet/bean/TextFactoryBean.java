package com.github.yingzhuo.bayonet.bean;

import com.github.yingzhuo.bayonet.utility.StringUtils;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 从资源文件加载文本内容的 {@link FactoryBean}。
 *
 * <p>从 Spring 资源路径加载文本文件，解析其中的占位符（{@code ${...}}），
 * 并可选地进行整体去空白或每行去空白操作。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * &#64;Bean
 * public TextFactoryBean myText() {
 *     var bean = new TextFactoryBean();
 *     bean.setLocation("classpath:my-text.txt");
 *     bean.setTrim(true);
 *     return bean;
 * }
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
public class TextFactoryBean implements FactoryBean<String>, EnvironmentAware, ResourceLoaderAware, InitializingBean {

    private ResourceLoader resourceLoader;
    private Environment environment;
    private @Setter String location;
    private @Setter Charset charset = StandardCharsets.UTF_8;
    private @Setter boolean trim;
    private @Setter boolean trimEachLine;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "resourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "environment must not be null");
        this.environment = environment;
    }

    /**
     * 设置字符集。
     *
     * @param charset 字符集，不能为 {@code null}
     */
    public void setCharset(Charset charset) {
        Assert.notNull(charset, "charset must not be null");
        this.charset = charset;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.hasText(location, "location must not be empty");
    }

    /**
     * 从资源文件加载文本内容。
     *
     * <p>读取指定资源位置的文本内容，解析 Spring 占位符。
     * 若 {@link #trim} 为 {@code true}，去除结果的首尾空白；
     * 若 {@link #trimEachLine} 为 {@code true}，去除每行首尾空白。</p>
     *
     * @return 文本内容（资源不存在时抛出异常）
     * @throws Exception 资源读取或占位符解析失败时抛出
     */
    @Override
    public @Nullable String getObject() throws Exception {
        var text = resourceLoader.getResource(location).getContentAsString(charset);
        text = environment.resolvePlaceholders(text);

        if (trim) {
            text = text.trim();
        }
        if (trimEachLine) {
            text = StringUtils.trimEachLine(text);
        }
        return text;
    }

    /**
     * 返回 Bean 类型。
     *
     * @return {@link String} 的 {@link Class}
     */
    @Override
    public Class<?> getObjectType() {
        return String.class;
    }

}
