package com.github.yingzhuo.bayonet.secret;

import org.springframework.core.io.AbstractResource;

public abstract class SecretResource<T> extends AbstractResource {

    @Override
    public String getDescription() {
        return this.toString();
    }

    public abstract T getSecret();

}
