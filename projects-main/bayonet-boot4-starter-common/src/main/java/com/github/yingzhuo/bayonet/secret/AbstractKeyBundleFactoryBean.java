package com.github.yingzhuo.bayonet.secret;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractKeyBundleFactoryBean implements FactoryBean<KeyBundle>, InitializingBean {

    @Override
    public Class<?> getObjectType() {
        return KeyBundle.class;
    }

}
