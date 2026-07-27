package com.github.yingzhuo.bayonet.jwt.algorithm;

import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Spring {@link FactoryBean}，用于创建 {@link SM2Algorithm} 实例。
 *
 * <p>通过 Base64 编码的公私钥字符串配置 SM2 签名算法。
 * 实现 {@link InitializingBean}，在属性设置后自动清理密钥字符串中的空白字符。</p>
 *
 * @author 应卓
 * @see SM2Algorithm
 * @since 4.1.1
 */
public class SM2AlgorithmFactoryBean implements FactoryBean<SM2Algorithm>, InitializingBean {

    private @Setter String publicKeyText;
    private @Setter String privateKeyText;

    /**
     * 创建 {@link SM2Algorithm} 实例。
     *
     * @return {@link SM2Algorithm}（非 {@code null}）
     */
    @Override
    public SM2Algorithm getObject() {
        return new SM2Algorithm(publicKeyText, privateKeyText);
    }

    /**
     * 验证并清理密钥字符串。
     * <p>去除密钥字符串中的所有空白字符。(包含换行)</p>
     */
    @Override
    public void afterPropertiesSet() {
        Assert.hasText(publicKeyText, "public key text must not be empty");
        Assert.hasText(privateKeyText, "private key text must not be empty");
        this.publicKeyText = StringUtils.trimAllWhitespace(publicKeyText);
        this.privateKeyText = StringUtils.trimAllWhitespace(privateKeyText);
    }

    /**
     * 返回 {@link SM2Algorithm} 的 {@link Class}。
     *
     * @return {@link SM2Algorithm} 的 {@link Class}
     */
    @Override
    public Class<?> getObjectType() {
        return SM2Algorithm.class;
    }
}
