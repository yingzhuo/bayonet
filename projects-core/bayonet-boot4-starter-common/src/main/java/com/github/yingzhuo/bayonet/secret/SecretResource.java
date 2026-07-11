package com.github.yingzhuo.bayonet.secret;

import org.springframework.core.io.AbstractResource;

import java.io.InputStream;

public abstract class SecretResource<T> extends AbstractResource {

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException();
    }

    public abstract T getSecret();

}
