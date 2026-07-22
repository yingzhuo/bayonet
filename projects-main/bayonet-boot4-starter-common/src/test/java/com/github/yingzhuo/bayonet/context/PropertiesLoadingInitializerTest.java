package com.github.yingzhuo.bayonet.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertiesLoadingInitializerTest {

    private final PropertiesLoadingInitializer initializer = new PropertiesLoadingInitializer();

    @Mock ConfigurableApplicationContext ctx;
    @Mock ConfigurableEnvironment environment;
    @Mock MutablePropertySources propertySources;

    private Resource mockResourceNotFound() {
        var r = mock(Resource.class);
        lenient().when(r.exists()).thenReturn(false);
        return r;
    }

    private Resource mockResourceFound(String url) throws Exception {
        var r = mock(Resource.class);
        lenient().when(r.exists()).thenReturn(true);
        lenient().when(r.isReadable()).thenReturn(true);
        lenient().when(r.getURL()).thenReturn(new java.net.URL(url));
        lenient().when(r.getInputStream()).thenReturn(urlToInputStream(url));
        return r;
    }

    private static java.io.InputStream urlToInputStream(String url) throws Exception {
        return new java.net.URL(url).openStream();
    }

    private void setupDefaultMocks() {
        when(ctx.getEnvironment()).thenReturn(environment);
        when(environment.getProperty("spring.application.name")).thenReturn(null);
    }

    @Test
    void should_do_nothing_when_no_config_found() {
        var notFound = mockResourceNotFound();
        when(ctx.getResource(anyString())).thenReturn(notFound);
        setupDefaultMocks();

        initializer.initialize(ctx);

        verify(ctx).getEnvironment();
        verify(propertySources, never()).addFirst(any());
    }

    @Test
    void should_load_from_file_default_when_found() throws Exception {
        var tempFile = Files.createTempFile("test-", ".properties").toFile();
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), "app.name=bayonet", StandardCharsets.UTF_8);

        var notFound = mockResourceNotFound();
        var found = mockResourceFound(tempFile.toURI().toURL().toString());

        when(ctx.getResource(anyString())).thenReturn(notFound);
        when(ctx.getResource("file:default.properties")).thenReturn(found);
        setupDefaultMocks();
        when(environment.getPropertySources()).thenReturn(propertySources);

        initializer.initialize(ctx);

        verify(propertySources).addFirst(argThat(ps -> "file:default.properties".equals(ps.getName())));
        verify(ctx, never()).getResource("file:config/default.properties");
    }

    @Test
    void should_fallback_to_next_location() throws Exception {
        var tempFile = Files.createTempFile("test-", ".properties").toFile();
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), "app.version=2.0", StandardCharsets.UTF_8);

        var notFound = mockResourceNotFound();
        var found = mockResourceFound(tempFile.toURI().toURL().toString());

        when(ctx.getResource(anyString())).thenReturn(notFound);
        when(ctx.getResource("file:config/default.properties")).thenReturn(found);
        setupDefaultMocks();
        when(environment.getPropertySources()).thenReturn(propertySources);

        initializer.initialize(ctx);

        verify(propertySources).addFirst(argThat(ps -> "file:config/default.properties".equals(ps.getName())));
        verify(ctx, never()).getResource("classpath:default.properties");
    }

    @Test
    void should_handle_ioException_gracefully() throws Exception {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.isReadable()).thenReturn(true);
        when(resource.getInputStream()).thenThrow(new IOException("read error"));

        when(ctx.getResource(anyString())).thenReturn(resource);
        setupDefaultMocks();

        assertThatCode(() -> initializer.initialize(ctx)).doesNotThrowAnyException();
    }

    @Test
    void should_use_applicationName_when_spring_dot_application_dot_name_is_set() throws Exception {
        var tempFile = Files.createTempFile("test-", ".properties").toFile();
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), "app.name=bayonet", StandardCharsets.UTF_8);

        var notFound = mockResourceNotFound();
        var found = mockResourceFound(tempFile.toURI().toURL().toString());

        when(ctx.getResource(anyString())).thenReturn(notFound);
        when(ctx.getResource("classpath:myapp.properties")).thenReturn(found);
        when(ctx.getEnvironment()).thenReturn(environment);
        when(environment.getPropertySources()).thenReturn(propertySources);
        when(environment.getProperty("spring.application.name")).thenReturn("myapp");

        initializer.initialize(ctx);

        verify(propertySources).addFirst(argThat(ps -> "classpath:myapp.properties".equals(ps.getName())));
    }

}
