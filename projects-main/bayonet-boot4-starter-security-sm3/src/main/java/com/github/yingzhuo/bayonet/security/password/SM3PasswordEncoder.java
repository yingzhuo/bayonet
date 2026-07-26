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
public class SM3PasswordEncoder implements NamedPasswordEncoder {

    @Override
    public String getName() {
        return "SM3";
    }

    @Override
    public @Nullable String encode(@Nullable CharSequence rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        return SmUtil.sm3(rawPassword.toString());
    }

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
