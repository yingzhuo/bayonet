package com.github.yingzhuo.bayonet.secret;

import com.github.yingzhuo.bayonet.utility.ResourceUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * 从 KeyStore 加载 {@link SecretKey} 的静态工具类。
 *
 * <p>提供便捷的静态方法，简化从 KeyStore 文件中获取对称密钥的流程。
 * 内部使用 {@link KeyStoreUtils} 进行 KeyStore 操作。</p>
 *
 * @author 应卓
 * @see KeyStoreUtils
 * @see KeyStoreType
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecretKeyFactories {

    /**
     * 从 KeyStore 文件中加载 {@link SecretKey}。
     *
     * @param location  KeyStore 资源位置，支持 classpath:/、file:/ 等 Spring 资源路径（非空）
     * @param type      KeyStore 类型，为 {@code null} 时使用默认类型 {@link KeyStoreType#PKCS12}
     * @param storepass KeyStore 密码（非空）
     * @param alias     密钥别名（非空）
     * @return {@link SecretKey}（非 {@code null}）
     * @throws IllegalArgumentException 若参数非法或 KeyStore 操作失败
     * @throws IllegalStateException    若指定别名下未找到对称密钥
     */
    public static SecretKey loadFromKeyStore(String location, @Nullable KeyStoreType type, String storepass, String alias) {
        return loadFromKeyStore(location, type, storepass, alias, null);
    }

    /**
     * 从 KeyStore 文件中加载 {@link SecretKey}。
     *
     * @param location  KeyStore 资源位置，支持 classpath:/、file:/ 等 Spring 资源路径（非空）
     * @param type      KeyStore 类型，为 {@code null} 时使用默认类型 {@link KeyStoreType#PKCS12}
     * @param storepass KeyStore 密码（非空）
     * @param alias     密钥别名（非空）
     * @param keypass   密钥密码，为 {@code null} 时使用 {@code storepass}
     * @return {@link SecretKey}（非 {@code null}）
     * @throws IllegalArgumentException 若参数非法或 KeyStore 操作失败
     * @throws IllegalStateException    若指定别名下未找到对称密钥
     */
    public static SecretKey loadFromKeyStore(String location, @Nullable KeyStoreType type, String storepass, String alias, @Nullable String keypass) {
        keypass = Objects.requireNonNullElse(keypass, storepass);

        Assert.hasText(location, "location must not be empty");
        Assert.hasText(storepass, "storepass must not be empty");
        Assert.hasText(alias, "alias must not be empty");

        var storepassChars = storepass.toCharArray();
        var keypassChars = keypass != null ? keypass.toCharArray() : storepassChars;

        try (var stream = ResourceUtils.loadAsInputStream(location)) {
            var keyStore = KeyStoreUtils.loadKeyStore(stream, type, storepass);
            return KeyStoreUtils.getSecretKey(keyStore, alias, keypass);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            Arrays.fill(storepassChars, '\0');
            if (keypassChars != storepassChars) {
                Arrays.fill(keypassChars, '\0');
            }
        }
    }
}
