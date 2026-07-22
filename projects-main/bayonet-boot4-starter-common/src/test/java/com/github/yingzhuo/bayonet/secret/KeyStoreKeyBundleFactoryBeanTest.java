package com.github.yingzhuo.bayonet.secret;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeyStoreKeyBundleFactoryBeanTest {

    private KeyStoreKeyBundleFactoryBean createValidBean() {
        var fb = new KeyStoreKeyBundleFactoryBean();
        fb.setStoreType(KeyStoreType.PKCS12);
        fb.setStoreLocation("classpath:keystore.p12");
        fb.setStorepass("changeit");
        fb.setAlias("mykey");
        return fb;
    }

    // ============== afterPropertiesSet ==============

    @Test
    void should_pass_validation_when_all_fields_are_valid() {
        var fb = createValidBean();
        // should not throw
        fb.afterPropertiesSet();
    }

    @Test
    void should_throw_when_storeType_is_null() {
        var fb = createValidBean();
        fb.setStoreType(null);

        assertThatThrownBy(fb::afterPropertiesSet)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("storeType");
    }

    @Test
    void should_throw_when_storeLocation_is_empty() {
        var fb = createValidBean();
        fb.setStoreLocation("");

        assertThatThrownBy(fb::afterPropertiesSet)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("storeLocation");
    }

    @Test
    void should_throw_when_storepass_is_empty() {
        var fb = createValidBean();
        fb.setStorepass("");

        assertThatThrownBy(fb::afterPropertiesSet)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("storepass");
    }

    @Test
    void should_throw_when_alias_is_empty() {
        var fb = createValidBean();
        fb.setAlias("");

        assertThatThrownBy(fb::afterPropertiesSet)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("alias");
    }

    @Test
    void should_accept_null_keypass() {
        var fb = createValidBean();
        fb.setKeypass(null);
        // should not throw
        fb.afterPropertiesSet();
    }

    @Test
    void should_accept_blank_keypass() {
        var fb = createValidBean();
        fb.setKeypass("");
        // should not throw
        fb.afterPropertiesSet();
    }

    // ============== getObjectType ==============

    @Test
    void getObjectType_should_return_KeyBundle() {
        var fb = new KeyStoreKeyBundleFactoryBean();
        assertThat(fb.getObjectType()).isEqualTo(KeyBundle.class);
    }

    // ============== isSingleton ==============

    @Test
    void isSingleton_should_return_true_by_default() {
        var fb = new KeyStoreKeyBundleFactoryBean();
        assertThat(fb.isSingleton()).isTrue();
    }

}
