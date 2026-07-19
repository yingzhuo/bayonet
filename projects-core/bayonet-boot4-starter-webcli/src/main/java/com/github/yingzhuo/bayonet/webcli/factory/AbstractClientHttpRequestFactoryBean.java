package com.github.yingzhuo.bayonet.webcli.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.http.client.ClientHttpRequestFactory;

public abstract class AbstractClientHttpRequestFactoryBean implements FactoryBean<ClientHttpRequestFactory> {

    @Override
    public final Class<?> getObjectType() {
        return ClientHttpRequestFactory.class;
    }

}
