package com.github.yingzhuo.bayonet.webcli.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.http.client.ClientHttpRequestFactory;

@Deprecated(forRemoval = true)
public abstract class AbstractClientHttpRequestFactoryBean implements FactoryBean<ClientHttpRequestFactory> {

    @Override
    public final Class<?> getObjectType() {
        return ClientHttpRequestFactory.class;
    }

}
