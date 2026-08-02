package com.github.yingzhuo.bayonet.toml.configdata;

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

class TomlConfigDataLocationResolverTest {

    private ResourceLoader resourceLoader;
    private TomlConfigDataLocationResolver resolver;

    @BeforeEach
    void setUp() {
        var logFactory = mock(DeferredLogFactory.class);
        when(logFactory.getLog(TomlConfigDataLocationResolver.class)).thenReturn(mock(Log.class));
        resourceLoader = mock(ResourceLoader.class);
        resolver = new TomlConfigDataLocationResolver(logFactory, resourceLoader);
    }

    @Test
    void should_be_resolvable_when_location_has_toml_prefix() {
        assertThat(resolver.isResolvable(null, ConfigDataLocation.of("toml:classpath:/app.toml"))).isTrue();
    }

    @Test
    void should_not_be_resolvable_when_location_has_no_toml_prefix() {
        assertThat(resolver.isResolvable(null, ConfigDataLocation.of("classpath:/app.toml"))).isFalse();
    }

    @Test
    void should_resolve_directory_location_to_application_toml() {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resourceLoader.getResource("classpath:/config/application.toml")).thenReturn(resource);

        var result = resolver.resolve(null, ConfigDataLocation.of("toml:classpath:/config/"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getResource()).isSameAs(resource);
    }

    @Test
    void should_resolve_file_location() {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resourceLoader.getResource("file:./app.toml")).thenReturn(resource);

        var result = resolver.resolve(null, ConfigDataLocation.of("toml:file:./app.toml"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getResource()).isSameAs(resource);
    }

    @Test
    void should_return_empty_when_resource_missing_and_optional() {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(false);
        when(resourceLoader.getResource("file:./missing.toml")).thenReturn(resource);

        var result = resolver.resolve(null, ConfigDataLocation.of("optional:toml:file:./missing.toml"));

        assertThat(result).isEmpty();
    }

    @Test
    void should_throw_when_resource_missing_and_not_optional() {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(false);
        when(resourceLoader.getResource("file:./missing.toml")).thenReturn(resource);

        assertThatThrownBy(() -> resolver.resolve(null, ConfigDataLocation.of("toml:file:./missing.toml")))
                .isInstanceOf(ConfigDataResourceNotFoundException.class);
    }

    @Test
    void should_resolve_profile_specific_files() {
        var devResource = mock(Resource.class);
        when(devResource.exists()).thenReturn(true);
        var prodResource = mock(Resource.class);
        when(prodResource.exists()).thenReturn(true);
        when(resourceLoader.getResource("classpath:/config/application-dev.toml")).thenReturn(devResource);
        when(resourceLoader.getResource("classpath:/config/application-prod.toml")).thenReturn(prodResource);

        var profiles = mock(Profiles.class);
        when(profiles.iterator()).thenReturn(List.of("dev", "prod").iterator());

        var result = resolver.resolveProfileSpecific(null, ConfigDataLocation.of("toml:classpath:/config/"), profiles);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(r -> r.getResource()).containsExactly(devResource, prodResource);
    }
}
