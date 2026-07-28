package com.github.yingzhuo.bayonet.secret;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class KeyBundleImplTest {

    @Mock
    private X509Certificate certificate;

    @Mock
    private PrivateKey privateKey;

    // ============== 构造器 ==============

    @Test
    void should_create_when_paramsValid() {
        var bundle = new KeyBundleImpl(List.of(certificate), privateKey);
        assertThat(bundle).isNotNull();
    }

    @Test
    void should_throw_when_certificateChainIsNull() {
        assertThatThrownBy(() -> new KeyBundleImpl(null, privateKey))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_certificateChainIsEmpty() {
        assertThatThrownBy(() -> new KeyBundleImpl(List.of(), privateKey))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_certificateChainContainsNull() {
        var chain = new ArrayList<X509Certificate>();
        chain.add(null);
        assertThatThrownBy(() -> new KeyBundleImpl(chain, privateKey))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_privateKeyIsNull() {
        assertThatThrownBy(() -> new KeyBundleImpl(List.of(certificate), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getCertificate ==============

    @Test
    void should_return_firstCertificate() {
        var bundle = new KeyBundleImpl(List.of(certificate), privateKey);
        X509Certificate actual = bundle.getCertificate();
        assertThat(actual).isSameAs(certificate);
    }

    // ============== getCertificateChain ==============

    @Test
    void should_return_certificateChain() {
        var bundle = new KeyBundleImpl(List.of(certificate), privateKey);
        assertThat(bundle.getCertificateChain()).containsExactly(certificate);
    }

    @Test
    void should_return_unmodifiableCertificateChain() {
        var bundle = new KeyBundleImpl(List.of(certificate), privateKey);
        assertThatThrownBy(() -> bundle.getCertificateChain().add(certificate))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void should_notAffectInternalState_when_inputListIsMutatedAfterConstruction() {
        var mutableList = new ArrayList<>(List.of(certificate));
        var bundle = new KeyBundleImpl(mutableList, privateKey);

        mutableList.clear();

        assertThat(bundle.getCertificateChain()).hasSize(1);
    }

    // ============== getPrivateKey ==============

    @Test
    void should_return_privateKey() {
        var bundle = new KeyBundleImpl(List.of(certificate), privateKey);
        PrivateKey actual = bundle.getPrivateKey();
        assertThat(actual).isSameAs(privateKey);
    }

    // ============== getKeyPair ==============

    @Test
    void should_return_keyPair() {
        var bundle = new KeyBundleImpl(List.of(certificate), privateKey);
        assertThat(bundle.getKeyPair()).isNotNull();
    }

}