package com.github.yingzhuo.bayonet.security.password;

import cn.hutool.crypto.SmUtil;
import org.springframework.security.crypto.password.AbstractValidatingPasswordEncoder;
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
public class SM3PasswordEncoder extends AbstractValidatingPasswordEncoder implements NamedPasswordEncoder {

    @Override
    public String getName() {
        return "SM3";
    }

    @Override
    protected String encodeNonNullPassword(String rawPassword) {
        return SmUtil.sm3(rawPassword);
    }

    @Override
    protected boolean matchesNonNull(String rawPassword, String encodedPassword) {
        var encodedAgain = encode(rawPassword);
        return MessageDigest.isEqual(
                encodedAgain.getBytes(StandardCharsets.UTF_8),
                encodedPassword.getBytes(StandardCharsets.UTF_8)
        );
    }
}
