package com.github.yingzhuo.bayonet.secret;

import org.springframework.util.Assert;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

final class KeyBundleImpl implements KeyBundle {

    private final List<X509Certificate> certificateChain;
    private final PrivateKey privateKey;

    public KeyBundleImpl(List<X509Certificate> certificateChain, PrivateKey privateKey) {
        Assert.notNull(certificateChain, "certificateChain must not be null");
        Assert.notEmpty(certificateChain, "certificateChain must not be empty");
        Assert.noNullElements(certificateChain, "certificateChain must not contain null elements");
        Assert.notNull(privateKey, "privateKey must not be null");

        this.certificateChain = List.copyOf(certificateChain);
        this.privateKey = privateKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PublicKey> T getPublicKey() {
        return (T) getCertificate().getPublicKey();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PrivateKey> T getPrivateKey() {
        return (T) this.privateKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends X509Certificate> T getCertificate() {
        return (T) this.certificateChain.get(0);
    }

    @Override
    public List<X509Certificate> getCertificateChain() {
        return certificateChain;
    }
}
