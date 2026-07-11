package com.github.yingzhuo.bayonet.secret;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.nio.file.Path;
import java.security.KeyStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeyStoreFactoryBeanTest {

    private static final String STOREPASS = "storepass";
    private static Path keystorePath;
    private final ResourceLoader loader = new DefaultResourceLoader();

    @BeforeAll
    static void setUp(@TempDir Path tempDir) throws Exception {
        keystorePath = tempDir.resolve("test.p12");
        var pb = new ProcessBuilder(
                "keytool", "-genkeypair",
                "-alias", "testkey",
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-keystore", keystorePath.toString(),
                "-storetype", "PKCS12",
                "-storepass", STOREPASS,
                "-dname", "CN=Test",
                "-validity", "365"
        );
        assertThat(pb.start().waitFor()).as("keytool -genkeypair").isZero();
    }

    private static KeyStoreFactoryBean createValidBean(ResourceLoader loader) {
        var bean = new KeyStoreFactoryBean();
        bean.setLocation("file:" + keystorePath);
        bean.setStorepass(STOREPASS);
        bean.setResourceLoader(loader);
        return bean;
    }

    // ============== getObjectType ==============

    @Test
    void should_return_keyStoreType() {
        assertThat(new KeyStoreFactoryBean().getObjectType()).isSameAs(KeyStore.class);
    }

    // ============== 成功加载 ==============

    @Test
    void should_load_when_validPkcs12() throws Exception {
        assertThat(createValidBean(loader).getObject()).isNotNull();
    }

    @Test
    void should_load_when_explicitPkcs12Type() throws Exception {
        var bean = createValidBean(loader);
        bean.setKeyStoreType(KeyStoreType.PKCS12);
        assertThat(bean.getObject()).isNotNull();
    }

    // ============== 参数校验 ==============

    @Test
    void should_throw_when_keyStoreTypeIsNull() {
        var bean = createValidBean(loader);
        bean.setKeyStoreType(null);
        assertThatThrownBy(bean::getObject)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_locationIsNull() {
        var bean = new KeyStoreFactoryBean();
        bean.setStorepass(STOREPASS);
        bean.setResourceLoader(loader);
        assertThatThrownBy(bean::getObject)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_locationIsEmpty() {
        var bean = new KeyStoreFactoryBean();
        bean.setLocation("");
        bean.setStorepass(STOREPASS);
        bean.setResourceLoader(loader);
        assertThatThrownBy(bean::getObject)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_storepassIsNull() {
        var bean = new KeyStoreFactoryBean();
        bean.setLocation("file:" + keystorePath);
        bean.setResourceLoader(loader);
        assertThatThrownBy(bean::getObject)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_storepassIsEmpty() {
        var bean = new KeyStoreFactoryBean();
        bean.setLocation("file:" + keystorePath);
        bean.setStorepass("");
        bean.setResourceLoader(loader);
        assertThatThrownBy(bean::getObject)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_resourceLoaderIsNull() {
        var bean = new KeyStoreFactoryBean();
        bean.setLocation("file:" + keystorePath);
        bean.setStorepass(STOREPASS);
        assertThatThrownBy(bean::getObject)
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== 加载失败 ==============

    @Test
    void should_throw_when_locationNotExists() {
        var bean = new KeyStoreFactoryBean();
        bean.setLocation("file:/nonexistent/nope.p12");
        bean.setStorepass(STOREPASS);
        bean.setResourceLoader(loader);
        assertThatThrownBy(bean::getObject)
                .isInstanceOf(Exception.class);
    }

    @Test
    void should_throw_when_storepassWrong() {
        var bean = new KeyStoreFactoryBean();
        bean.setLocation("file:" + keystorePath);
        bean.setStorepass("wrongpass");
        bean.setResourceLoader(loader);
        assertThatThrownBy(bean::getObject)
                .isInstanceOf(Exception.class);
    }

}
