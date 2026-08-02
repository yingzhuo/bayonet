package com.github.yingzhuo.bayonet.context;

import org.apache.commons.logging.Log;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import java.security.Security;

/**
 * 安装 Bouncy Castle Provider 的环境后置处理器。
 * <p>通过反射按类名加载 {@code BouncyCastleProvider}，避免编译期硬依赖 Bouncy Castle 类。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public class BouncyCastleInstallingEnvironmentPostProcessor extends AbstractEnvironmentPostProcessor {

    private static final String BC_PROVIDER_NAME = "BC";
    private static final String BC_PROVIDER_CLASS_NAME = "org.bouncycastle.jce.provider.BouncyCastleProvider";

    public BouncyCastleInstallingEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        super(logFactory);
    }

    @Override
    protected void doProcess(Log log, ConfigurableEnvironment environment) {
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
