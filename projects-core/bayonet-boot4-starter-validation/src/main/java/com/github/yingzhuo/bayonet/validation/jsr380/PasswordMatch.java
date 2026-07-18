package com.github.yingzhuo.bayonet.validation.jsr380;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * JSR 380 密码匹配校验注解。
 * <p>验证两个密码字段的值是否相同（如密码和确认密码）。
 * 使用类级别注解，通过反射获取指定属性值进行比较。</p>
 *
 * <pre>{@code
 * @PasswordMatch
 * public class ChangePasswordForm {
 *     private String password;
 *     private String confirmPassword;
 * }
 * }</pre>
 *
 * @author 应卓
 */
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {

    /**
     * 校验失败时的提示消息。
     * <p>支持国际化，默认从 {@code ValidationMessages.properties} 中获取。</p>
     *
     * @return 消息模板
     */
    String message() default "{passwordMatch.invalid}";

    /**
     * 第一个密码字段的名称。
     *
     * @return 字段名
     */
    String propertyName1() default "password";

    /**
     * 第二个密码字段的名称。
     *
     * @return 字段名
     */
    String propertyName2() default "confirmPassword";

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
}
