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

    /**
     * 构造器
     *
     * @param logFactory 日志工厂
     */
    public BouncyCastleInstallingEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        super(logFactory);
        setOrder(HIGHEST_PRECEDENCE);
    }

    @Override
    protected void doProcess(Log log, ConfigurableEnvironment environment) {
        // BTW: JCE 的意思是 Java Cryptography Extension (Java语言密码扩展)
        try {
            if (Security.getProvider(BC_PROVIDER_NAME) != null) {
                log.debug("BouncyCastle JCE provider already installed. Skipping");
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
