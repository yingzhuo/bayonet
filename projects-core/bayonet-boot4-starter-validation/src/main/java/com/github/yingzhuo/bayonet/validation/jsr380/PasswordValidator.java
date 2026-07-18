package com.github.yingzhuo.bayonet.validation.jsr380;

import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.Nullable;

/**
 * {@link Password} 注解的校验器实现。
 * <p>校验密码长度及大写字母、小写字母、数字、特殊字符的包含情况。
 * 根据 JSR 380 规范，{@code null} 值视为有效（由 {@link jakarta.validation.constraints.NotNull} 处理）。</p>
 *
 * @author 应卓
 */
public class PasswordValidator extends AbstractValidator<Password, String> {

    private int minLength;
    private int maxLength;
    private boolean requireUpperCase;
    private boolean requireLowerCase;
    private boolean requireDigit;
    private boolean requireSpecialChar;
    private @Nullable String specialChars;

    @Override
    public void initialize(Password annotation) {
        this.minLength = annotation.minLength();
        this.maxLength = annotation.maxLength();
        this.requireUpperCase = annotation.requireUpperCase();
        this.requireLowerCase = annotation.requireLowerCase();
        this.requireDigit = annotation.requireDigit();
        this.requireSpecialChar = annotation.requireSpecialChar();
        this.specialChars = annotation.specialChars();
    }

    @Override
    public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.length() < minLength || value.length() > maxLength) {
            return false;
        }

        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : value.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (specialChars.indexOf(c) >= 0) hasSpecial = true;
        }

        if (requireUpperCase && !hasUpper) return false;
        if (requireLowerCase && !hasLower) return false;
        if (requireDigit && !hasDigit) return false;
        return !requireSpecialChar || hasSpecial;
    }
}
