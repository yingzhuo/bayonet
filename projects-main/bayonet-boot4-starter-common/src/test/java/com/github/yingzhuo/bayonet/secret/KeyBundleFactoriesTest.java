package com.github.yingzhuo.bayonet.secret;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeyBundleFactoriesTest {

    // ============== loadFromPem ==============

    @Test
    void should_throw_when_loadFromPem_locationIsNull() {
        assertThatThrownBy(() -> KeyBundleFactories.loadFromPem(null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadFromPem_locationIsEmpty() {
        assertThatThrownBy(() -> KeyBundleFactories.loadFromPem("", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadFromPem_locationIsBlank() {
        assertThatThrownBy(() -> KeyBundleFactories.loadFromPem("   ", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadFromPem_resourceNotFound() {
        assertThatThrownBy(() -> KeyBundleFactories.loadFromPem("classpath:nonexistent.pem", null))
                .isInstanceOf(Exception.class);
    }

    // ============== loadFromStore ==============

    @Test
    void should_throw_when_loadFromStore_locationIsNull() {
        assertThatThrownBy(() -> KeyBundleFactories.loadFromStore(null, KeyStoreType.PKCS12, "pass", "alias", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadFromStore_locationIsEmpty() {
        assertThatThrownBy(() -> KeyBundleFactories.loadFromStore("", KeyStoreType.PKCS12, "pass", "alias", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_not_throw_when_loadFromStore_typeIsNull() {
        // null type 使用默认值 PKCS12，不会因此抛异常
        assertThatThrownBy(() -> KeyBundleFactories.loadFromStore("loc", null, "pass", "alias", null))
                .isNotInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadFromStore_storepassIsEmpty() {
        assertThatThrownBy(() -> KeyBundleFactories.loadFromStore("loc", KeyStoreType.PKCS12, "", "alias", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadFromStore_aliasIsEmpty() {
        assertThatThrownBy(() -> KeyBundleFactories.loadFromStore("loc", KeyStoreType.PKCS12, "pass", "", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
