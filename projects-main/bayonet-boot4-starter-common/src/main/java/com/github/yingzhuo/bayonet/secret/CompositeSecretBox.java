package com.github.yingzhuo.bayonet.secret;

import com.github.yingzhuo.bayonet.utility.collection.ArrayUtils;
import com.github.yingzhuo.bayonet.utility.collection.SortingUtils;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.io.Serial;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * 组合多个 {@link SecretBox} 的代理实现。
 * <p>从其中获取密钥/证书时按排序后的顺序依次尝试各代理，第一个能返回结果的即为最终结果；
 * 若某代理不存在该别名（抛出 {@link NoSuchElementException}），则继续尝试下一个。</p>
 *
 * @author 应卓
 * @see SecretBox
 * @see KeyStoreSecretBox
 * @since 4.1.1
 */
public final class CompositeSecretBox implements SecretBox {

    @Serial
    private final static long serialVersionUID = -8533156255292017710L;

    private final List<SecretBox> delegates;

    /**
     * 构造器（从 Collection）
     *
     * @param delegates 被代理的 {@link SecretBox}，不能为 {@code null}，不能包含 {@code null} 元素
     * @throws IllegalArgumentException 若 {@code delegates} 为 {@code null} 或包含 {@code null}
     */
    public CompositeSecretBox(Collection<SecretBox> delegates) {
        Assert.notNull(delegates, "delegates must not be null");
        Assert.noNullElements(delegates, "delegates must not contain null elements");

        var sorted = new ArrayList<>(delegates);
        SortingUtils.sort(sorted);
        this.delegates = Collections.unmodifiableList(sorted);
    }

    /**
     * 构造器（变参）
     *
     * @param delegates 被代理的 {@link SecretBox}
     */
    public CompositeSecretBox(SecretBox... delegates) {
        this(Arrays.asList(delegates));
    }

    /**
     * 创建组合代理（变参便捷方法）
     *
     * @param delegates 被代理的 {@link SecretBox}
     * @return 组合 {@link SecretBox}
     */
    public static SecretBox of(SecretBox... delegates) {
        if (ArrayUtils.isEmpty(delegates)) {
            return new CompositeSecretBox();
        }
        if (ArrayUtils.size(delegates) == 1) {
            return delegates[0];
        }
        return new CompositeSecretBox(delegates);
    }

    /**
     * 组合代理不支持该方法。
     *
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public String getStorePassword() {
        throw new UnsupportedOperationException("CompositeSecretBox does not support getStorePassword()");
    }

    @Override
    public boolean containsAlias(String alias) {
        for (var box : delegates) {
            if (box.containsAlias(alias)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getAliases() {
        return delegates.stream()
                .flatMap(box -> box.getAliases().stream())
                .distinct()
                .toList();
    }

    @Override
    public <T extends SecretKey> T getSecretKey(String alias) {
        return find(box -> box.getSecretKey(alias), "alias not found: " + alias);
    }

    @Override
    public List<X509Certificate> getCertificateChain(String alias) {
        return find(box -> box.getCertificateChain(alias), "alias not found: " + alias);
    }

    @Override
    public <T extends Certificate> T getCertificate(String alias) {
        return find(box -> box.getCertificate(alias), "alias not found: " + alias);
    }

    @Override
    public LocalDateTime getCertificateNotBefore(String alias) {
        return find(box -> box.getCertificateNotBefore(alias), "alias not found: " + alias);
    }

    @Override
    public LocalDateTime getCertificateNotAfter(String alias) {
        return find(box -> box.getCertificateNotAfter(alias), "alias not found: " + alias);
    }

    @Override
    public boolean isCertificateValid(String alias) {
        return find(box -> box.isCertificateValid(alias), "alias not found: " + alias);
    }

    @Override
    public <T extends PublicKey> T getPublicKey(String alias) {
        return find(box -> box.getPublicKey(alias), "alias not found: " + alias);
    }

    @Override
    public <T extends PrivateKey> T getPrivateKey(String alias) {
        return find(box -> box.getPrivateKey(alias), "alias not found: " + alias);
    }

    @Override
    public KeyPair getKeyPair(String alias) {
        return find(box -> box.getKeyPair(alias), "alias not found: " + alias);
    }

    // ------

    private <T> T find(Function<SecretBox, T> extractor, String notFoundMessage) {
        for (var box : delegates) {
            try {
                return extractor.apply(box);
            } catch (NoSuchElementException ignored) {
                // 该代理无此别名，尝试下一个
            }
        }
        throw new NoSuchElementException(notFoundMessage);
    }
}
