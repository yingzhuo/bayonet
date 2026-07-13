package com.github.yingzhuo.bayonet.security.password;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link PasswordEncoder} 工厂工具类。
 * <p>提供 {@link DelegatingPasswordEncoder} 的预配置实例，支持多编码器路由。
 * 所有密码均以 {@code {encoderId}password} 格式存储。</p>
 *
 * <pre>{@code
 * // 使用完整配置
 * var encoder = PasswordEncoderFactories.createDefault();
 * String encoded = encoder.encode("myPassword");
 * boolean match = encoder.matches("myPassword", encoded);
 *
 * // 使用精简配置
 * var minimal = PasswordEncoderFactories.createMinimal();
 * }</pre>
 *
 * @see DelegatingPasswordEncoder
 * @see BCryptPasswordEncoder
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PasswordEncoderFactories {

    private static final String DEFAULT_ENCODING_ID = "bcrypt";
    private static final String DEFAULT_MATCHES_ID = "noop";

    /**
     * 创建完整编码器集合的 {@link DelegatingPasswordEncoder}。
     * <p>支持以下编码器（按 ID 查找）：</p>
     * <ul>
     *   <li>{@code bcrypt} — BCrypt（默认编码器）</li>
     *   <li>{@code ldap} — Ldap SHA（已弃用）</li>
     *   <li>{@code MD4} — MD4（已弃用）</li>
     *   <li>{@code MD5} — MD5（已弃用）</li>
     *   <li>{@code noop} — 明文（用于 matches 兜底）</li>
     *   <li>{@code pbkdf2} — PBKDF2</li>
     *   <li>{@code scrypt} — SCrypt</li>
     *   <li>{@code SHA-1} — SHA-1（已弃用）</li>
     *   <li>{@code SHA-256} — SHA-256（已弃用）</li>
     *   <li>{@code sha256} — Standard SHA-256（已弃用）</li>
     *   <li>{@code argon2} — Argon2</li>
     * </ul>
     *
     * @return DelegatingPasswordEncoder 实例
     */
    @SuppressWarnings("deprecation")
    public static DelegatingPasswordEncoder createDefault() {
        var encoders = new HashMap<String, PasswordEncoder>();
        encoders.put(DEFAULT_ENCODING_ID, new BCryptPasswordEncoder());
        encoders.put("ldap", new LdapShaPasswordEncoder());
        encoders.put("MD4", new Md4PasswordEncoder());
        encoders.put("MD5", new MessageDigestPasswordEncoder("MD5"));
        encoders.put(DEFAULT_MATCHES_ID, NoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("SHA-1", new MessageDigestPasswordEncoder("SHA-1"));
        encoders.put("SHA-256", new MessageDigestPasswordEncoder("SHA-256"));
        encoders.put("sha256", new StandardPasswordEncoder());
        encoders.put("argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());

        return buildEncoder(DEFAULT_ENCODING_ID, DEFAULT_MATCHES_ID, encoders);
    }

    /**
     * 创建精简编码器集合的 {@link DelegatingPasswordEncoder}。
     * <p>仅包含以下编码器：</p>
     * <ul>
     *   <li>{@code bcrypt} — BCrypt（默认编码器）</li>
     *   <li>{@code MD5} — MD5（已弃用）</li>
     *   <li>{@code noop} — 明文（用于 matches 兜底）</li>
     * </ul>
     *
     * @return DelegatingPasswordEncoder 实例
     */
    @SuppressWarnings("deprecation")
    public static DelegatingPasswordEncoder createMinimal() {
        var encoders = new HashMap<String, PasswordEncoder>();
        encoders.put(DEFAULT_ENCODING_ID, new BCryptPasswordEncoder());
        encoders.put("MD5", new MessageDigestPasswordEncoder("MD5"));
        encoders.put(DEFAULT_MATCHES_ID, NoOpPasswordEncoder.getInstance());
        return buildEncoder(DEFAULT_ENCODING_ID, DEFAULT_MATCHES_ID, encoders);
    }

    private static DelegatingPasswordEncoder buildEncoder(String encodingId, String matchesId, Map<String, PasswordEncoder> encoders) {
        var passwordEncoder = new DelegatingPasswordEncoder(encodingId, encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(encoders.get(matchesId));
        return passwordEncoder;
    }

}
