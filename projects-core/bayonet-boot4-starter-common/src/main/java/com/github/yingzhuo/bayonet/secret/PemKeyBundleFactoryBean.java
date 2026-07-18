package com.github.yingzhuo.bayonet.secret;

import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/**
 * {@link FactoryBean} 用于从 PEM 文件加载 {@link KeyBundle}。
 * <p>通过 setter 注入 PEM 文件路径和私钥密码，
 * {@link #afterPropertiesSet()} 校验必填参数，
 * {@link #getObject()} 委托 {@link KeyBundleFactories#loadFromPem} 加载。</p>
 *
 * <pre>{@code
 * &#64;Bean
 * public PemKeyBundleFactoryBean keyBundle() {
 *     var fb = new PemKeyBundleFactoryBean();
 *     fb.setPemLocation("classpath:cert.pem");
 *     fb.setKeypass("changeit");
 *     return fb;
 * }
 * }</pre>
 *
 * @see KeyBundleFactories#loadFromPem
 * @author 应卓
 */
@Setter
public class PemKeyBundleFactoryBean extends AbstractKeyBundleFactoryBean {

    private String pemLocation;
    private @Nullable String keypass;

    @Override
    public KeyBundle getObject() {
        return KeyBundleFactories.loadFromPem(pemLocation, keypass);
    }

    /**
     * 校验必填参数。
     *
     * @throws IllegalArgumentException 若 {@code pemLocation} 为空
     */
    @Override
    public void afterPropertiesSet() {
        Assert.hasText(pemLocation, "pemLocation must not be empty");
    }

}
