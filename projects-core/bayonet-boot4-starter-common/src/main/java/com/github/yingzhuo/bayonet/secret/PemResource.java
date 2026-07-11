package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.ssl.pem.PemContent;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

public class PemResource extends SecretResource<PemContent> {

    private final PemContent pemContent;

    public PemResource(InputStream stream) {
        Assert.notNull(stream, "stream must not be null");

        try {
            this.pemContent = PemContent.load(stream);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // NoOp
            }
        }
    }

    @Override
    public String getDescription() {
        return "Pem [" + this.pemContent + "]";
    }

    @Override
    public PemContent getSecret() {
        return this.pemContent;
    }

    public List<X509Certificate> getCertificates() {
        try {
            return pemContent.getCertificates();
        } catch (IllegalStateException e) {
            return List.of();
        }
    }

    @Nullable
    public X509Certificate getCertificate() {
        var cs = getCertificates();
        return cs.isEmpty() ? null : cs.get(0);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends PrivateKey> T getPrivateKey(@Nullable String keypass) {
        try {
            if (keypass == null) {
                return (T) pemContent.getPrivateKey();
            } else {
                return (T) pemContent.getPrivateKey(keypass);
            }
        } catch (IllegalStateException e) {
            return null;
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends PublicKey> T getPublicKey() {
        var cert = getCertificate();
        if (cert == null) return null;
        return (T) cert.getPublicKey();
    }

    @Nullable
    public KeyPair getKeyPair(@Nullable String keypass) {
        var publicKey = getPublicKey();
        var privateKey = getPrivateKey(keypass);
        if (publicKey == null || privateKey == null) return null;
        return new KeyPair(publicKey, privateKey);
    }

}
