package com.github.yingzhuo.bayonet.validation.jsr380;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * JSR 380 字符串包含校验注解。
 * <p>验证字符串是否命中指定列表中的任意一个值，支持配置是否大小写敏感。
 * 配合 {@link jakarta.validation.constraints.NotNull} 使用时，
 * 需额外添加 {@code @NotNull} 来校验空值。</p>
 *
 * <pre>{@code
 * // 大小写敏感（默认）
 * @StringIn({"read", "write", "delete"})
 * private String operation;
 * }</pre>
 *
 * @author 应卓
 * @see StringInValidator
 * @since 4.1.1
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StringInValidator.class)
@Documented
public @interface StringIn {

    /**
     * 校验失败时的提示消息。
     * <p>支持国际化，默认从 {@code ValidationMessages.properties} 中获取。</p>
     *
     * @return 消息模板
     */
    String message() default "{string.in.invalid}";

    /**
     * 分组。
     *
     * @return 分组
     */
    Class<?>[] groups() default {};

    /**
     * Payload。
     *
     * @return Payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 允许的字符串列表。
     *
     * @return 允许的字符串列表
     */
    String[] value();

    /**
     * 是否大小写敏感。
     *
     * @return 是否大小写敏感
     */
    boolean caseSensitive() default true;
}
