package com.github.yingzhuo.bayonet.secret;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PemKeyBundleFactoryBeanTest {

    private PemKeyBundleFactoryBean createValidBean() {
        var fb = new PemKeyBundleFactoryBean();
        fb.setPemLocation("classpath:cert.pem");
        return fb;
    }

    // ============== afterPropertiesSet ==============

    @Test
    void should_pass_validation_when_pemLocation_is_valid() {
        var fb = createValidBean();
        // should not throw
        fb.afterPropertiesSet();
    }

    @Test
    void should_throw_when_pemLocation_is_empty() {
        var fb = createValidBean();
        fb.setPemLocation("");

        assertThatThrownBy(fb::afterPropertiesSet)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pemLocation");
    }

    @Test
    void should_throw_when_pemLocation_is_blank() {
        var fb = createValidBean();
        fb.setPemLocation("   ");

        assertThatThrownBy(fb::afterPropertiesSet)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pemLocation");
    }

    @Test
    void should_accept_null_keypass() {
        var fb = createValidBean();
        fb.setKeypass(null);
        // should not throw
        fb.afterPropertiesSet();
    }

    @Test
    void should_accept_empty_keypass() {
        var fb = createValidBean();
        fb.setKeypass("");
        // should not throw
        fb.afterPropertiesSet();
    }

    // ============== getObjectType ==============

    @Test
    void getObjectType_should_return_KeyBundle() {
        var fb = new PemKeyBundleFactoryBean();
        assertThat(fb.getObjectType()).isEqualTo(KeyBundle.class);
    }

    // ============== isSingleton ==============

    @Test
    void isSingleton_should_return_true_by_default() {
        var fb = new PemKeyBundleFactoryBean();
        assertThat(fb.isSingleton()).isTrue();
    }

}
