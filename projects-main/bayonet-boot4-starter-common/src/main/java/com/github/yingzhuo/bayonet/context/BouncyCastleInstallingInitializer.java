package com.github.yingzhuo.bayonet.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.security.Security;

/**
 * 安装 Bouncy Castle Provider 的应用上下文初始化器。
 * <p>通过反射按类名加载 {@code BouncyCastleProvider}，避免编译期硬依赖 Bouncy Castle 类。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
@Slf4j
public class BouncyCastleInstallingInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String BC_PROVIDER_NAME = "BC";
    private static final String BC_PROVIDER_CLASS_NAME = "org.bouncycastle.jce.provider.BouncyCastleProvider";

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        try {
            if (Security.getProvider(BC_PROVIDER_NAME) != null) {
                return;
            }

            var clazz = Class.forName(BC_PROVIDER_CLASS_NAME);
            Security.addProvider((java.security.Provider) clazz.getConstructor().newInstance());
            log.debug("BouncyCastle JCE provider initialization complete.");
        } catch (ClassNotFoundException ignored) {
            log.debug("BouncyCastle JCE provider not found. Skipping.");
        } catch (Exception e) {
            log.warn("Failed to install BouncyCastle provider", e);
        }
    }
}
