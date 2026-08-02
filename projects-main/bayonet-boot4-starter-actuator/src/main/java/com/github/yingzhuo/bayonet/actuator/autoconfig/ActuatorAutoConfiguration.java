package com.github.yingzhuo.bayonet.actuator.autoconfig;

import com.github.yingzhuo.bayonet.actuator.SecurityProvidersEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Actuator 增强自动配置。
 * <p>注册 {@link SecurityProvidersEndpoint} Bean，提供查看已安装 JCE Security Provider 的端点。</p>
 *
 * @author 应卓
 * @see SecurityProvidersEndpoint
 * @since 4.1.1
 */
@AutoConfiguration
public class ActuatorAutoConfiguration {

    /**
     * 注册 Security Provider 端点。
     *
     * @return {@link SecurityProvidersEndpoint}（非 {@code null}）
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityProvidersEndpoint securityProvidersEndpoint() {
        return new SecurityProvidersEndpoint();
    }
}
