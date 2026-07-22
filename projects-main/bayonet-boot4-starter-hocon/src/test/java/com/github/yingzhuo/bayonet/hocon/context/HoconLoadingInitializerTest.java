package com.github.yingzhuo.bayonet.hocon.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoconLoadingInitializerTest {

    private final HoconLoadingInitializer initializer = new HoconLoadingInitializer();
    @Mock
    ConfigurableApplicationContext ctx;
    @Mock
    ConfigurableEnvironment environment;
    @Mock
    MutablePropertySources propertySources;

    // ============== 无可用的配置文件 ==============

    private static Resource mockResourceNotFound() {
        var r = mock(Resource.class);
        when(r.exists()).thenReturn(false);
        return r;
    }

    // ============== 从 classpath:default.conf 加载成功 ==============

    private static Resource mockResourceFound(String url) throws Exception {
        var r = mock(Resource.class);
        when(r.exists()).thenReturn(true);
        when(r.isReadable()).thenReturn(true);
        when(r.getURL()).thenReturn(new java.net.URL(url));
        return r;
    }

    // ============== 第一个路径不存在，从第二个加载 ==============

    @Test
    void should_do_nothing_when_no_config_found() {
        var notFound = mockResourceNotFound();
        when(ctx.getResource(anyString())).thenReturn(notFound);
        when(ctx.getEnvironment()).thenReturn(environment);
        when(environment.getProperty("spring.application.name")).thenReturn(null);

        initializer.initialize(ctx);

        verify(ctx).getEnvironment();
        verify(propertySources, never()).addFirst(any());
    }

    // ============== IOException 时不应抛出异常 ==============

    @Test
    void should_load_from_classpath_when_found() throws Exception {
        var tempFile = Files.createTempFile("test-", ".conf").toFile();
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), "app.name = bayonet");

        var notFound = mockResourceNotFound();
        var foundResource = mockResourceFound(tempFile.toURI().toURL().toString());

        when(ctx.getResource(anyString())).thenReturn(notFound);
        when(ctx.getResource("classpath:default.conf")).thenReturn(foundResource);

        when(ctx.getEnvironment()).thenReturn(environment);
        when(environment.getPropertySources()).thenReturn(propertySources);

        initializer.initialize(ctx);

        verify(propertySources).addFirst(argThat(ps -> "classpath:default.conf".equals(ps.getName())));
    }

    // ============== helper ==============

    @Test
    void should_fallback_to_next_location() throws Exception {
        var tempFile = Files.createTempFile("test-", ".conf").toFile();
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), "app.version = 1.0");

        var notFound = mockResourceNotFound();
        var foundResource = mockResourceFound(tempFile.toURI().toURL().toString());

        when(ctx.getResource(anyString())).thenReturn(notFound);
        when(ctx.getResource("file:config/default.conf")).thenReturn(foundResource);

        when(ctx.getEnvironment()).thenReturn(environment);
        when(environment.getPropertySources()).thenReturn(propertySources);

        initializer.initialize(ctx);

        verify(propertySources).addFirst(argThat(ps -> "file:config/default.conf".equals(ps.getName())));
        verify(ctx, never()).getResource("classpath:default.conf");
    }

    @Test
    void should_handle_ioException_gracefully() throws Exception {
        var resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.isReadable()).thenReturn(true);
        when(resource.getURL()).thenThrow(new IOException("read error"));

        when(ctx.getResource(anyString())).thenReturn(resource);
        when(ctx.getEnvironment()).thenReturn(environment);
        when(environment.getProperty("spring.application.name")).thenReturn(null);

        assertThatCode(() -> initializer.initialize(ctx)).doesNotThrowAnyException();
    }

    @Test
    void should_use_applicationName_when_spring_dot_application_dot_name_is_set() throws Exception {
        var tempFile = Files.createTempFile("test-", ".conf").toFile();
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), "app.name = bayonet");

        var notFound = mockResourceNotFound();
        var found = mockResourceFound(tempFile.toURI().toURL().toString());

        when(ctx.getResource(anyString())).thenReturn(notFound);
        when(ctx.getResource("classpath:myapp.conf")).thenReturn(found);
        when(ctx.getEnvironment()).thenReturn(environment);
        when(environment.getPropertySources()).thenReturn(propertySources);
        when(environment.getProperty("spring.application.name")).thenReturn("myapp");

        initializer.initialize(ctx);

        verify(propertySources).addFirst(argThat(ps -> "classpath:myapp.conf".equals(ps.getName())));
    }

}
