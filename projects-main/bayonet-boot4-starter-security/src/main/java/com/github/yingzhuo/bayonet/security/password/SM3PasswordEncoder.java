package com.github.yingzhuo.bayonet.security.password;

import com.github.yingzhuo.bayonet.utility.SM3Utils;
import org.springframework.security.crypto.password.AbstractValidatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 基于国密 SM3 算法的 {@link PasswordEncoder} 实现。
 *
 * <p>内部使用 {@link SM3Utils} 计算 SM3 哈希，输出小写 Hex 编码。
 * 对于 {@code null} 或空字符串的密码，{@link #matches} 直接返回 {@code false}。</p>
 *
 * @author 应卓
 * @see SM3Utils
 * @since 4.1.1
 */
public class SM3PasswordEncoder extends AbstractValidatingPasswordEncoder {

    @Override
    protected String encodeNonNullPassword(String rawPassword) {
        return SM3Utils.digestHex(rawPassword);
    }

    @Override
    protected boolean matchesNonNull(String rawPassword, String encodedPassword) {
        var encodedAgain = encode(rawPassword);
        return MessageDigest.isEqual(
                encodedAgain.getBytes(UTF_8),
                encodedPassword.getBytes(UTF_8)
        );
    }
}
