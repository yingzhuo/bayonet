package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.*;
import java.security.cert.Certificate;

public class KeyStoreResource extends SecretResource<KeyStore> {

    private final KeyStore keyStore;

    public KeyStoreResource(KeyStoreType type, InputStream stream, String storepass) {
        Assert.notNull(type, "type must not be null");
        Assert.notNull(stream, "stream must not be null");
        Assert.notNull(storepass, "storepass must not be null");

        try {
            this.keyStore = KeyStore.getInstance(type.name());
            this.keyStore.load(stream, storepass.toCharArray());
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
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
        return "KeyStore [" + this.keyStore + "]";
    }

    @Override
    public KeyStore getSecret() {
        return this.keyStore;
    }

    public Certificate[] getCertificateChain(String alias) {
        try {
            var certs = keyStore.getCertificateChain(alias);
            if (certs == null) return new Certificate[0];
            return certs;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Nullable
    public Certificate getCertificate(String alias) {
        try {
            return keyStore.getCertificate(alias);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Key> T getKey(String alias, @Nullable String keypass) {
        keypass = keypass != null ? keypass : "";
        try {
            return (T) keyStore.getKey(alias, keypass.toCharArray());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Nullable
    public <T extends Key> T getKey(String alias) {
        return getKey(alias, null);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends PrivateKey> T getPrivateKey(String alias, @Nullable String keypass) {
        keypass = keypass != null ? keypass : "";
        try {
            return (T) keyStore.getKey(alias, keypass.toCharArray());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Nullable
    public <T extends PrivateKey> T getPrivateKey(String alias) {
        return getPrivateKey(alias, null);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends PublicKey> T getPublicKey(String alias) {
        var cert = getCertificate(alias);
        if (cert == null) return null;
        return (T) cert.getPublicKey();
    }

    @Nullable
    public KeyPair getKeyPair(String alias, @Nullable String keypass) {
        PublicKey publicKey = getPublicKey(alias);
        PrivateKey privateKey = getPrivateKey(alias, keypass);
        if (publicKey == null || privateKey == null) return null;
        return new KeyPair(publicKey, privateKey);
    }

}
