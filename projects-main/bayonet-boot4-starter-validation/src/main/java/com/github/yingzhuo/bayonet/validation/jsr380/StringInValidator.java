package com.github.yingzhuo.bayonet.validation.jsr380;

import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.Nullable;

/**
 * {@link StringIn} 注解的校验器实现。
 * <p>校验字符串是否命中指定列表中的任意一个值，支持配置是否大小写敏感。
 * 根据 JSR 380 规范，{@code null} 值视为有效（由 {@link jakarta.validation.constraints.NotNull} 处理）。</p>
 *
 * @author 应卓
 * @see StringIn
 * @since 4.1.1
 */
public class StringInValidator extends AbstractValidator<StringIn, String> {

    private String[] values;
    private boolean caseSensitive;

    @Override
    public void initialize(StringIn annotation) {
        this.values = annotation.value();
        this.caseSensitive = annotation.caseSensitive();
    }

    @Override
    public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        for (String candidate : values) {
            if (caseSensitive ? value.equals(candidate) : value.equalsIgnoreCase(candidate)) {
                return true;
            }
        }
        return false;
    }
}
