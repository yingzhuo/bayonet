package com.github.yingzhuo.bayonet.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.security.Security;

@Slf4j
public class BouncyCastleInstallingInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        try {
            if (Security.getProvider(BouncyCastleConstants.BC_PROVIDER_NAME) != null) {
                return;
            }

            var clazz = Class.forName(BouncyCastleConstants.BC_PROVIDER_CLASS_NAME);
            Security.addProvider((java.security.Provider) clazz.getConstructor().newInstance());
            log.debug("BouncyCastle JCE provider initialization complete.");
        } catch (ClassNotFoundException ignored) {
            log.debug("BouncyCastle JCE provider not found. Skipping.");
        } catch (Exception e) {
            log.warn("Failed to install BouncyCastle provider", e);
        }
    }
}
