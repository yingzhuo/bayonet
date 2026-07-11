package com.github.yingzhuo.bayonet.secret;

import org.springframework.core.io.AbstractResource;

import java.io.InputStream;

/**
 * 密钥资源的抽象基类。
 * <p>包装了 {@link java.security.KeyStore} 或 PEM 等密钥材料，
 * 使其可作为 Spring {@link org.springframework.core.io.Resource} 使用。</p>
 *
 * @param <T> 密钥材料的类型（如 {@link java.security.KeyStore} 或 {@link org.springframework.boot.ssl.pem.PemContent}）
 */
public abstract class SecretResource<T> extends AbstractResource {

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException();
    }

    /**
     * 返回密钥材料对象。
     *
     * @return 密钥材料
     */
    public abstract T getSecret();

}
