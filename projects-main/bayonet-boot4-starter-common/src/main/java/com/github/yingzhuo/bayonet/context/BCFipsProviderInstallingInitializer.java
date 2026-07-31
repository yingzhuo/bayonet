package com.github.yingzhuo.bayonet.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.security.Security;

@Slf4j
public class BCFipsProviderInstallingInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String PROVIDER_NAME = "BCFIPS";
    private static final String PROVIDER_CLASS_NAME = "org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            if (Security.getProvider(PROVIDER_NAME) != null) {
                return;
            }

            if (Security.getAlgorithms("BC") != null) {
                Security.removeProvider("BC");
            }

            var clazz = Class.forName(PROVIDER_CLASS_NAME);
            Security.addProvider((java.security.Provider) clazz.getConstructor().newInstance());
            log.debug("BouncyCastle FIPS provider initialization complete.");
        } catch (ClassNotFoundException ignored) {
            log.debug("BouncyCastle FIPS provider not found. Skipping.");
        } catch (Exception e) {
            log.warn("Failed to install BouncyCastle provider", e);
        }
    }

}
