package com.github.yingzhuo.bayonet.security.password;

import cn.hutool.crypto.SmUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 基于国密 SM3 算法的 {@link PasswordEncoder} 实现。
 *
 * <p>使用 Hutool 的 {@link SmUtil#sm3} 进行密码哈希。
 * 对于 {@code null} 或空字符串的密码，{@link #matches} 直接返回 {@code false}。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public class SM3PasswordEncoder implements PasswordEncoder {

    /**
     * 对原始密码进行 SM3 哈希编码。
     *
     * @param rawPassword 原始密码，可为 {@code null}
     * @return SM3 哈希值，输入为 {@code null} 时返回 {@code null}
     */
    @Override
    public @Nullable String encode(@Nullable CharSequence rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        return SmUtil.sm3(rawPassword.toString());
    }

    /**
     * 校验原始密码是否与已编码的密码匹配。
     * <p>使用 {@link MessageDigest#isEqual} 进行恒等时间比较，防止时序攻击。</p>
     * <p>{@code null} 或空字符串的密码直接返回 {@code false}。</p>
     *
     * @param rawPassword     原始密码，可为 {@code null}
     * @param encodedPassword 已编码的密码，可为 {@code null}
     * @return 匹配返回 {@code true}，否则 {@code false}
     */
    @Override
    public boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }

        var encodedNow = encode(rawPassword);
        return MessageDigest.isEqual(
                encodedNow.getBytes(StandardCharsets.UTF_8),
                encodedPassword.getBytes(StandardCharsets.UTF_8)
        );
    }

}
