package com.github.yingzhuo.bayonet.validation.jsr380;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ============== 默认配置（8-32, 大写+小写+数字+特殊字符） ==============

    @Test
    void should_pass_when_defaultRulesMet() {
        var violations = validator.validateValue(PasswordBean.class, "defaultRules", "Abc12345!@#");
        assertThat(violations).isEmpty();
    }

    @Test
    void should_fail_when_tooShort() {
        var violations = validator.validateValue(PasswordBean.class, "defaultRules", "Ab1!");
        assertThat(violations).isNotEmpty();
    }

    @Test
    void should_fail_when_tooLong() {
        var violations = validator.validateValue(PasswordBean.class, "defaultRules", "A".repeat(40));
        assertThat(violations).isNotEmpty();
    }

    @Test
    void should_fail_when_noUpperCase() {
        var violations = validator.validateValue(PasswordBean.class, "defaultRules", "abc12345!@#");
        assertThat(violations).isNotEmpty();
    }

    @Test
    void should_fail_when_noLowerCase() {
        var violations = validator.validateValue(PasswordBean.class, "defaultRules", "ABC12345!@#");
        assertThat(violations).isNotEmpty();
    }

    @Test
    void should_fail_when_noDigit() {
        var violations = validator.validateValue(PasswordBean.class, "defaultRules", "Abcdefgh!@#");
        assertThat(violations).isNotEmpty();
    }

    @Test
    void should_fail_when_noSpecialChar() {
        var violations = validator.validateValue(PasswordBean.class, "defaultRules", "Abcdefgh1");
        assertThat(violations).isNotEmpty();
    }

    // ============== 关闭部分校验 ==============

    @Test
    void should_pass_when_upperCaseNotRequired() {
        var violations = validator.validateValue(PasswordBean.class, "noUpper", "abc12345!@#");
        assertThat(violations).isEmpty();
    }

    @Test
    void should_pass_when_lowerCaseNotRequired() {
        var violations = validator.validateValue(PasswordBean.class, "noLower", "ABC12345!@#");
        assertThat(violations).isEmpty();
    }

    @Test
    void should_pass_when_digitNotRequired() {
        var violations = validator.validateValue(PasswordBean.class, "noDigit", "Abcdefgh!@#");
        assertThat(violations).isEmpty();
    }

    @Test
    void should_pass_when_specialNotRequired() {
        var violations = validator.validateValue(PasswordBean.class, "noSpecial", "Abcdefgh1");
        assertThat(violations).isEmpty();
    }

    // ============== 自定义长度 ==============

    @Test
    void should_pass_when_customMinLengthMet() {
        var violations = validator.validateValue(PasswordBean.class, "customMinMax", "Ab1!defg");
        assertThat(violations).isEmpty();  // 8 chars >= min=4
    }

    @Test
    void should_fail_when_belowCustomMinLength() {
        var violations = validator.validateValue(PasswordBean.class, "customMinMax", "Ab1!");
        assertThat(violations).isNotEmpty();  // 4 chars < min=6
    }

    // ============== 自定义特殊字符集合 ==============

    @Test
    void should_pass_when_customSpecialChars() {
        var violations = validator.validateValue(PasswordBean.class, "customSpecial", "Abc12345@#");
        assertThat(violations).isEmpty();  // @# 在自定义集合中
    }

    @Test
    void should_fail_when_customSpecialChars_notMet() {
        var violations = validator.validateValue(PasswordBean.class, "customSpecial", "Abc12345!");
        assertThat(violations).isNotEmpty();  // ! 不在自定义集合 "@#" 中
    }

    // ============== null 值（视为有效） ==============

    @Test
    void should_pass_when_valueIsNull() {
        var violations = validator.validateValue(PasswordBean.class, "defaultRules", null);
        assertThat(violations).isEmpty();
    }

    // ============== empty / blank ==============

    @Test
    void should_fail_when_valueIsEmpty() {
        var violations = validator.validateValue(PasswordBean.class, "defaultRules", "");
        assertThat(violations).isNotEmpty();
    }

    @Test
    void should_fail_when_valueIsBlank() {
        var violations = validator.validateValue(PasswordBean.class, "defaultRules", "   ");
        assertThat(violations).isNotEmpty();
    }

    // ============== POJO 辅助类 ==============

    static class PasswordBean {

        @Password
        String defaultRules;

        @Password(requireUpperCase = false)
        String noUpper;

        @Password(requireLowerCase = false)
        String noLower;

        @Password(requireDigit = false)
        String noDigit;

        @Password(requireSpecialChar = false)
        String noSpecial;

        @Password(minLength = 6, maxLength = 20)
        String customMinMax;

        @Password(specialChars = "@#")
        String customSpecial;

    }

}
