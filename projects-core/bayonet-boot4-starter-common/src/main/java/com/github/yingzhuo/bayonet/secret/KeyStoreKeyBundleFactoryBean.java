package com.github.yingzhuo.bayonet.secret;

import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/**
 * {@link FactoryBean} 用于从 KeyStore 加载 {@link KeyBundle}。
 * <p>通过 setter 注入 KeyStore 类型、路径、密码和证书别名，
 * {@link #afterPropertiesSet()} 校验必填参数，
 * {@link #getObject()} 委托 {@link KeyBundleFactories#loadFromStore} 加载。</p>
 *
 * <pre>{@code
 * &#64;Bean
 * public KeyStoreKeyBundleFactoryBean keyBundle() {
 *     var fb = new KeyStoreKeyBundleFactoryBean();
 *     fb.setStoreType(KeyStoreType.PKCS12);
 *     fb.setStoreLocation("classpath:keystore.p12");
 *     fb.setStorepass("changeit");
 *     fb.setAlias("mykey");
 *     return fb;
 * }
 * }</pre>
 *
 * @see KeyBundleFactories#loadFromStore
 */
@Setter
public class KeyStoreKeyBundleFactoryBean extends AbstractKeyBundleFactoryBean {

    private KeyStoreType storeType;
    private String storeLocation;
    private String storepass;
    private String alias;
    private @Nullable String keypass;

    @Override
    public KeyBundle getObject() {
        return KeyBundleFactories.loadFromStore(
                storeLocation,
                storeType,
                storepass,
                alias,
                keypass
        );
    }

    /**
     * 校验必填参数。
     * <p>校验 {@code storeType}、{@code storeLocation}、{@code alias}、{@code storepass} 四项必填参数。
     * {@code keypass} 为空时交由 {@link KeyBundleFactories#loadFromStore} 兜底。</p>
     *
     * @throws IllegalArgumentException 若有必填参数为 {@code null} 或空
     */
    @Override
    public void afterPropertiesSet() {
        Assert.notNull(storeType, "storeType must not be null");
        Assert.hasText(storeLocation, "storeLocation must not be empty");
        Assert.hasText(storepass, "storepass must not be empty");
        Assert.hasText(alias, "alias must not be empty");
    }

}
