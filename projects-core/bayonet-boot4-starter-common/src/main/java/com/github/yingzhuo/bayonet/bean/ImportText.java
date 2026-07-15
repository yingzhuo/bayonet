package com.github.yingzhuo.bayonet.bean;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 从资源位置加载文本内容并注册为 Spring Bean。
 * <p>支持 {@code classpath:}、{@code file:}、{@code url:} 等 Spring Resource 协议。
 * 文本内容中的 {@code ${...}} 占位符将通过 {@link org.springframework.core.env.Environment} 解析。</p>
 *
 * <p>注册的 Bean 类型为 {@link String}，Bean 名称为 {@link #beanName()}：</p>
 *
 * <pre>{@code
 * &#64;Configuration
 * &#64;ImportText(location = "classpath:welcome.txt", beanName = "welcomeMessage")
 * public class AppConfig {
 * }
 * }</pre>
 *
 * <p>使用 {@link #value()} 简写形式：</p>
 *
 * <pre>{@code
 * &#64;Configuration
 * &#64;ImportText("classpath:welcome.txt")
 * public class AppConfig {
 * }
 * }</pre>
 *
 * <p>注解可重复使用：</p>
 *
 * <pre>{@code
 * &#64;ImportText(location = "classpath:header.txt", beanName = "header")
 * &#64;ImportText(location = "classpath:footer.txt", beanName = "footer")
 * &#64;Configuration
 * public class AppConfig {
 * }
 * }</pre>
 *
 * @see ImportTextImporting
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ImportTextImporting.class)
@Repeatable(ImportText.List.class)
public @interface ImportText {

    /**
     * 资源位置。
     * <p>Spring Resource 协议路径，如 {@code classpath:data/hello.txt}、{@code file:/tmp/data.txt}。
     * 与 {@link #value()} 互为别名。</p>
     *
     * @return 资源位置（不可为空）
     * @see #value()
     */
    @AliasFor("value")
    String location() default "";

    /**
     * 资源位置（{@link #location()} 的别名）。
     * <p>支持简写形式：{@code @ImportText("classpath:data.txt")} 等价于
     * {@code @ImportText(location = "classpath:data.txt")}。</p>
     *
     * @return 资源位置
     * @see #location()
     */
    @AliasFor("location")
    String value() default "";

    /**
     * Bean 名称。
     * <p>若为空字符串，将使用文本内容的标识哈希值作为 Bean 名称。
     * 建议始终明确指定。</p>
     *
     * @return Bean 名称（可选，默认为空）
     */
    String beanName() default "";

    /**
     * Bean 别名。
     *
     * @return 别名数组（默认为空）
     */
    String[] aliases() default {};

    /**
     * 是否标记为 primary Bean。
     *
     * @return 默认 {@code false}
     */
    boolean primary() default false;

    // ------

    /**
     * 支持 {@code @Repeatable} 的容器注解。
     *
     * @see ImportText
     */
    @Inherited
    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        /**
         * 可重复的 {@link ImportText} 数组。
         *
         * @return ImportText 数组
         */
        ImportText[] value();
    }
}
