package com.github.yingzhuo.bayonet.security.password;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.security.crypto.password.AbstractValidatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 基于国密 SM3 算法的 {@link PasswordEncoder} 实现。
 *
 * <p>基于 BouncyCastle 轻量级 API（{@link SM3Digest}）直接实现 SM3 哈希，输出小写 Hex 编码。
 * 对于 {@code null} 或空字符串的密码，{@link #matches} 直接返回 {@code false}。</p>
 *
 * @author 应卓
 * @see SM3Digest
 * @since 4.1.1
 */
public class SM3PasswordEncoder extends AbstractValidatingPasswordEncoder {

    @Override
    protected String encodeNonNullPassword(String rawPassword) {
        var bytes = rawPassword.getBytes(StandardCharsets.UTF_8);
        var digest = new SM3Digest();
        digest.update(bytes, 0, bytes.length);

        var hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return Hex.toHexString(hash);
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
