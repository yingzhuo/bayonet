package com.github.yingzhuo.bayonet.actuator;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 展示已安装 JCE Security Provider 的 Actuator 端点。
 * <p>返回 JVM 中 {@link Security#getProviders()} 注册的所有 Provider 的
 * {@code name} 和 {@code info} 描述信息。Java 9 模块化后 Provider 的版本概念已过时，故不返回。</p>
 *
 * <p><b>使用方式</b></p>
 * <pre>{@code
 * management.endpoints.web.exposure.include=securityproviders
 * }</pre>
 * 端点路径为 {@code /actuator/securityproviders}。
 *
 * @author 应卓
 * @since 4.1.1
 */
@Endpoint(id = "securityproviders")
public class SecurityProvidersEndpoint {

    /**
     * 查询已安装的 Security Provider。
     *
     * @param excludeSun 为 {@code false} 时返回全部 Provider（包含以 {@code "sun"} 开头的）；
     *                   {@code null}（默认）或 {@code true} 时排除名称以 {@code "sun"} 开头（忽略大小写）的 Provider
     *                   （如 {@code SUN}、{@code SunJCE}、{@code SunRsaSign} 等几乎所有 JDK 厂商都会提供的 Provider）
     * @return Provider 描述列表（非 {@code null}）
     */
    @ReadOperation
    public List<ProviderDescriptor> securityProviders(@Nullable Boolean excludeSun) {
        var exclude = !Boolean.FALSE.equals(excludeSun);
        return Arrays.stream(Security.getProviders())
                .filter(provider -> !exclude || !provider.getName().toLowerCase(Locale.ROOT).startsWith("sun"))
                .map(ProviderDescriptor::from)
                .toList();
    }

    /**
     * Security Provider 描述。
     *
     * @param name Provider 名称
     * @param info Provider 描述
     */
    public record ProviderDescriptor(String name, String info) {
        static ProviderDescriptor from(Provider provider) {
            return new ProviderDescriptor(provider.getName(), provider.getInfo());
        }
    }
}
