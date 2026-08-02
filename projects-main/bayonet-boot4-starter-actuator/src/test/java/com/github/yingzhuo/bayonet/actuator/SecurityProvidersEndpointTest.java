package com.github.yingzhuo.bayonet.actuator;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityProvidersEndpointTest {

    private final SecurityProvidersEndpoint endpoint = new SecurityProvidersEndpoint();

    private static boolean startsWithSun(String name) {
        return name.toLowerCase(Locale.ROOT).startsWith("sun");
    }

    @Test
    void should_exclude_sun_providers_by_default() {
        var providers = endpoint.securityProviders(null);

        assertThat(providers).isNotEmpty();
        assertThat(providers)
                .extracting(SecurityProvidersEndpoint.ProviderDescriptor::name)
                .noneMatch(SecurityProvidersEndpointTest::startsWithSun);
    }

    @Test
    void should_exclude_sun_providers_when_exclude_sun_is_true() {
        var providers = endpoint.securityProviders(Boolean.TRUE);

        assertThat(providers).isNotEmpty();
        assertThat(providers)
                .extracting(SecurityProvidersEndpoint.ProviderDescriptor::name)
                .noneMatch(SecurityProvidersEndpointTest::startsWithSun);
    }

    @Test
    void should_include_sun_providers_when_exclude_sun_is_false() {
        var providers = endpoint.securityProviders(Boolean.FALSE);

        assertThat(providers).isNotEmpty();
        assertThat(providers)
                .extracting(SecurityProvidersEndpoint.ProviderDescriptor::name)
                .anyMatch(SecurityProvidersEndpointTest::startsWithSun);
    }

    @Test
    void should_have_name_and_info() {
        var providers = endpoint.securityProviders(null);

        assertThat(providers).allSatisfy(provider -> {
            assertThat(provider.name()).isNotBlank();
            assertThat(provider.info()).isNotBlank();
        });
    }
}
