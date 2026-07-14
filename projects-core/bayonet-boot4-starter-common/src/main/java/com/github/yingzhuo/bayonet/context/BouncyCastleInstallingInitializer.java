package com.github.yingzhuo.bayonet.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.security.Security;

@Slf4j
public class BouncyCastleInstallingInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String PROVIDER_NAME = "BC";
    private static final String PROVIDER_CLASS_NAME = "org.bouncycastle.jce.provider.BouncyCastleProvider";

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        try {
            if (Security.getProvider(PROVIDER_NAME) != null) {
                return;
            }

            var clazz = Class.forName(PROVIDER_CLASS_NAME);
            Security.addProvider((java.security.Provider) clazz.getConstructor().newInstance());
        } catch (ClassNotFoundException ignored) {
            log.warn("BouncyCastle JCE provider not found. Skipping.");
        } catch (Exception e) {
            log.warn("Failed to install BouncyCastle provider", e);
        }
    }

}
