package com.github.yingzhuo.bayonet.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.security.Security;

/**
 * 安装 Bouncy Castle FIPS Provider 的应用上下文初始化器。
 * <p>通过反射按类名加载 {@code BouncyCastleFipsProvider}，避免编译期硬依赖 FIPS 类。
 * 安装前若已存在常规 BC Provider，则先移除以避免算法冲突。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
@Slf4j
public class BCFipsProviderInstallingInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            if (Security.getProvider(BouncyCastleConstants.BC_FIPS_PROVIDER_NAME) != null) {
                return;
            }

            if (Security.getProvider(BouncyCastleConstants.BC_PROVIDER_NAME) != null) {
                Security.removeProvider(BouncyCastleConstants.BC_PROVIDER_NAME);
            }

            var clazz = Class.forName(BouncyCastleConstants.BC_FIPS_CLASS_NAME);
            Security.addProvider((java.security.Provider) clazz.getConstructor().newInstance());
            log.debug("BouncyCastle FIPS provider initialization complete.");
        } catch (ClassNotFoundException ignored) {
            log.debug("BouncyCastle FIPS provider not found. Skipping.");
        } catch (Exception e) {
            log.warn("Failed to install BouncyCastle provider", e);
        }
    }
}
