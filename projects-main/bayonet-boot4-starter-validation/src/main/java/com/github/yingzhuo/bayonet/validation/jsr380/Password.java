package com.github.yingzhuo.bayonet.validation.jsr380;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * JSR 380 密码校验注解。
 * <p>验证密码长度、大写字母、小写字母、数字和特殊字符的包含情况。
 * 配合 {@link jakarta.validation.constraints.NotNull} 使用时，
 * 需额外添加 {@code @NotNull} 来校验空值。</p>
 *
 * <pre>{@code
 * @Password(minLength = 8, maxLength = 32, requireUpperCase = true)
 * private String password;
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface Password {

    /**
     * 校验失败时的提示消息。
     * <p>支持国际化，默认从 {@code ValidationMessages.properties} 中获取。</p>
     *
     * @return 消息模板
     */
    String message() default "{password.invalid}";

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
     * 密码最小长度。
     *
     * @return 最小长度
     */
    int minLength() default 8;

    /**
     * 密码最大长度。
     *
     * @return 最大长度
     */
    int maxLength() default 32;

    /**
     * 是否必须包含大写字母。
     *
     * @return 是否必须包含大写字母
     */
    boolean requireUpperCase() default true;

    /**
     * 是否必须包含小写字母。
     *
     * @return 是否必须包含小写字母
     */
    boolean requireLowerCase() default true;

    /**
     * 是否必须包含数字。
     *
     * @return 是否必须包含数字
     */
    boolean requireDigit() default true;

    /**
     * 是否必须包含特殊字符。
     *
     * @return 是否必须包含特殊字符
     */
    boolean requireSpecialChar() default true;

    /**
     * 自定义特殊字符集合。
     *
     * @return 特殊字符集合字符串
     */
    String specialChars() default "!@#$%^&*()_+-=[]{}|;':\",./<>?";
}
