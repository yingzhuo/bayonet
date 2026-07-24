package com.github.yingzhuo.bayonet.secret;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeyStoreUtilsTest {

    private static final String ALIAS = "test-alias";
    private static final String KEYPASS = "keypass";
    @Mock
    private KeyStore keyStore;
    @Mock
    private X509Certificate certificate;
    @Mock
    private PrivateKey privateKey;

    // ============== loadKeyStore ==============

    @Test
    void should_throw_when_loadKeyStore_inputStreamIsNull() {
        assertThatThrownBy(() -> KeyStoreUtils.loadKeyStore(null, KeyStoreType.PKCS12, "pass"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_not_throw_when_loadKeyStore_typeIsNull() {
        // null type 使用默认值 PKCS12，不会因此抛 IllegalArgumentException
        assertThatThrownBy(() -> KeyStoreUtils.loadKeyStore(InputStream.nullInputStream(), null, "pass"))
                .isNotInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadKeyStore_storepassIsNull() {
        assertThatThrownBy(() -> KeyStoreUtils.loadKeyStore(InputStream.nullInputStream(), KeyStoreType.PKCS12, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_loadKeyStore_invalidStream() {
        var invalid = new ByteArrayInputStream("not-a-keystore".getBytes());
        assertThatThrownBy(() -> KeyStoreUtils.loadKeyStore(invalid, KeyStoreType.PKCS12, "pass"))
                .isInstanceOf(UncheckedIOException.class);
    }

    // ============== getKey ==============

    @Test
    void should_throw_when_getKey_keyStoreIsNull() {
        assertThatThrownBy(() -> KeyStoreUtils.getKey(null, ALIAS, KEYPASS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_getKey_aliasIsEmpty() {
        assertThatThrownBy(() -> KeyStoreUtils.getKey(keyStore, "", KEYPASS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_getKey_keypassIsNull() {
        assertThatThrownBy(() -> KeyStoreUtils.getKey(keyStore, ALIAS, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_getKey_notFound() throws Exception {
        when(keyStore.getKey(ALIAS, KEYPASS.toCharArray())).thenReturn(null);
        assertThatThrownBy(() -> KeyStoreUtils.getKey(keyStore, ALIAS, KEYPASS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getPrivateKey ==============

    @Test
    void should_return_privateKey() throws Exception {
        when(keyStore.getKey(ALIAS, KEYPASS.toCharArray())).thenReturn(privateKey);
        PrivateKey result = KeyStoreUtils.getPrivateKey(keyStore, ALIAS, KEYPASS);
        assertThat(result).isSameAs(privateKey);
    }

    // ============== getCertificate ==============

    @Test
    void should_throw_when_getCertificate_keyStoreIsNull() {
        assertThatThrownBy(() -> KeyStoreUtils.getCertificate(null, ALIAS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_getCertificate_aliasIsEmpty() {
        assertThatThrownBy(() -> KeyStoreUtils.getCertificate(keyStore, ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_getCertificate_notFound() throws Exception {
        when(keyStore.getCertificate(ALIAS)).thenReturn(null);
        assertThatThrownBy(() -> KeyStoreUtils.getCertificate(keyStore, ALIAS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getPublicKey ==============

    @Test
    void should_return_publicKey() throws Exception {
        when(keyStore.getCertificate(ALIAS)).thenReturn(certificate);
        PublicKey publicKey = mock(PublicKey.class);
        when(certificate.getPublicKey()).thenReturn(publicKey);

        PublicKey result = KeyStoreUtils.getPublicKey(keyStore, ALIAS);
        assertThat(result).isSameAs(publicKey);
    }

    // ============== getCertificateChain ==============

    @Test
    void should_return_certificateChain() throws Exception {
        var chain = new Certificate[]{certificate};
        when(keyStore.getCertificateChain(ALIAS)).thenReturn(chain);

        var result = KeyStoreUtils.getCertificateChain(keyStore, ALIAS);
        assertThat(result).containsExactly(certificate);
    }

    @Test
    void should_return_emptyList_when_chainIsNull() throws Exception {
        when(keyStore.getCertificateChain(ALIAS)).thenReturn(null);
        assertThat(KeyStoreUtils.getCertificateChain(keyStore, ALIAS)).isEmpty();
    }

    // ============== getKeyPair ==============

    @Test
    void should_return_keyPair() throws Exception {
        when(keyStore.getCertificate(ALIAS)).thenReturn(certificate);
        PublicKey publicKey = mock(PublicKey.class);
        when(certificate.getPublicKey()).thenReturn(publicKey);
        when(keyStore.getKey(ALIAS, KEYPASS.toCharArray())).thenReturn(privateKey);

        var pair = KeyStoreUtils.getKeyPair(keyStore, ALIAS, KEYPASS);
        assertThat(pair.getPublic()).isSameAs(publicKey);
        assertThat(pair.getPrivate()).isSameAs(privateKey);
    }

    // ============== getSecretKey ==============

    @Test
    void should_return_secretKey() throws Exception {
        SecretKey secretKey = mock(SecretKey.class);
        when(keyStore.getKey(ALIAS, KEYPASS.toCharArray())).thenReturn(secretKey);

        SecretKey result = KeyStoreUtils.getSecretKey(keyStore, ALIAS, KEYPASS);
        assertThat(result).isSameAs(secretKey);
    }

    // ============== getAliases ==============

    @Test
    void should_return_aliases() throws Exception {
        var enumeration = new Enumeration<String>() {
            private int count;

            @Override
            public boolean hasMoreElements() {
                return count < 2;
            }

            @Override
            public String nextElement() {
                return count++ == 0 ? "alias1" : "alias2";
            }
        };

        when(keyStore.aliases()).thenReturn(enumeration);

        var aliases = KeyStoreUtils.getAliases(keyStore);
        assertThat(aliases).containsExactly("alias1", "alias2");
    }

    @Test
    void should_return_unmodifiableAliases() throws Exception {
        when(keyStore.aliases()).thenReturn(new Enumeration<>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public String nextElement() {
                return null;
            }
        });

        var aliases = KeyStoreUtils.getAliases(keyStore);
        assertThatThrownBy(() -> aliases.add("x"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // ============== containsAlias ==============

    @Test
    void should_return_true_when_aliasExists() throws Exception {
        when(keyStore.containsAlias(ALIAS)).thenReturn(true);
        assertThat(KeyStoreUtils.containsAlias(keyStore, ALIAS)).isTrue();
    }

    @Test
    void should_return_false_when_aliasNotExists() throws Exception {
        when(keyStore.containsAlias(ALIAS)).thenReturn(false);
        assertThat(KeyStoreUtils.containsAlias(keyStore, ALIAS)).isFalse();
    }

    @Test
    void should_throw_when_containsAlias_keyStoreIsNull() {
        assertThatThrownBy(() -> KeyStoreUtils.containsAlias(null, ALIAS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_containsAlias_aliasIsEmpty() {
        assertThatThrownBy(() -> KeyStoreUtils.containsAlias(keyStore, ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getSigAlgName / getSigAlgOID ==============

    @Test
    void should_return_sigAlgName() throws Exception {
        when(keyStore.getCertificate(ALIAS)).thenReturn(certificate);
        when(certificate.getSigAlgName()).thenReturn("SHA256WithRSA");

        assertThat(KeyStoreUtils.getSigAlgName(keyStore, ALIAS)).isEqualTo("SHA256WithRSA");
    }

    @Test
    void should_return_sigAlgOID() throws Exception {
        when(keyStore.getCertificate(ALIAS)).thenReturn(certificate);
        when(certificate.getSigAlgOID()).thenReturn("1.2.840.113549.1.1.11");

        assertThat(KeyStoreUtils.getSigAlgOID(keyStore, ALIAS)).isEqualTo("1.2.840.113549.1.1.11");
    }

    @Test
    void should_throw_when_certificateNotX509_forSigAlg() throws Exception {
        var genericCert = mock(Certificate.class);
        when(keyStore.getCertificate(ALIAS)).thenReturn(genericCert);

        assertThatThrownBy(() -> KeyStoreUtils.getSigAlgName(keyStore, ALIAS))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
