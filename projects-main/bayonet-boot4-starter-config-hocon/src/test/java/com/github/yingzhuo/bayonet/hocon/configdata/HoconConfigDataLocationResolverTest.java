package com.github.yingzhuo.bayonet.hocon.configdata;

import org.apache.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.boot.context.config.Profiles;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HoconConfigDataLocationResolverTest {

    private ResourceLoader resourceLoader;
    private HoconConfigDataLocationResolver resolver;

    @BeforeEach
    void setUp() {
        var logFactory = mock(DeferredLogFactory.class);
        when(logFactory.getLog(HoconConfigDataLocationResolver.class)).thenReturn(mock(Log.class));
        resourceLoader = mock(ResourceLoader.class);
        resolver = new HoconConfigDataLocationResolver(logFactory, resourceLoader);
    }

    @Test
    void should_be_resolvable_when_location_has_hocon_prefix() {
        assertThat(resolver.isResolvable(null, ConfigDataLocation.of("hocon:classpath:/app.conf"))).isTrue();
    }

    @Test
    void should_not_be_resolvable_when_location_has_no_hocon_prefix() {
        assertThat(resolver.isResolvable(null, ConfigDataLocation.of("classpath:/app.conf"))).isFalse();
    }

    @Test
    void should_resolve_directory_location_to_application_conf() {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resourceLoader.getResource("classpath:/config/application.conf")).thenReturn(resource);

        var result = resolver.resolve(null, ConfigDataLocation.of("hocon:classpath:/config/"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getResource()).isSameAs(resource);
    }

    @Test
    void should_resolve_file_location() {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resourceLoader.getResource("file:./app.conf")).thenReturn(resource);

        var result = resolver.resolve(null, ConfigDataLocation.of("hocon:file:./app.conf"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getResource()).isSameAs(resource);
    }

    @Test
    void should_return_empty_when_resource_missing_and_optional() {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(false);
        when(resourceLoader.getResource("file:./missing.conf")).thenReturn(resource);

        var result = resolver.resolve(null, ConfigDataLocation.of("optional:hocon:file:./missing.conf"));

        assertThat(result).isEmpty();
    }

    @Test
    void should_throw_when_resource_missing_and_not_optional() {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(false);
        when(resourceLoader.getResource("file:./missing.conf")).thenReturn(resource);

        assertThatThrownBy(() -> resolver.resolve(null, ConfigDataLocation.of("hocon:file:./missing.conf")))
                .isInstanceOf(ConfigDataResourceNotFoundException.class);
    }

    @Test
    void should_resolve_profile_specific_files() {
        var devResource = mock(Resource.class);
        when(devResource.exists()).thenReturn(true);
        var prodResource = mock(Resource.class);
        when(prodResource.exists()).thenReturn(true);
        when(resourceLoader.getResource("classpath:/config/application-dev.conf")).thenReturn(devResource);
        when(resourceLoader.getResource("classpath:/config/application-prod.conf")).thenReturn(prodResource);

        var profiles = mock(Profiles.class);
        when(profiles.iterator()).thenReturn(List.of("dev", "prod").iterator());

        var result = resolver.resolveProfileSpecific(null, ConfigDataLocation.of("hocon:classpath:/config/"), profiles);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(r -> r.getResource()).containsExactly(devResource, prodResource);
    }
}
