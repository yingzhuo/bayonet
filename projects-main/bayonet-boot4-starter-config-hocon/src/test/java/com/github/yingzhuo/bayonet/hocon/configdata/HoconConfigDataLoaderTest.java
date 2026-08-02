package com.github.yingzhuo.bayonet.hocon.configdata;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.core.io.Resource;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HoconConfigDataLoaderTest {

    private final HoconConfigDataLoader loader = new HoconConfigDataLoader();

    @Test
    void should_load_conf_into_config_data() throws Exception {
        var tempFile = Files.createTempFile("test-", ".conf").toFile();
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), "app.name = bayonet\nserver.port = 8080");

        var resource = mock(Resource.class);
        when(resource.getURL()).thenReturn(tempFile.toURI().toURL());
        when(resource.getFilename()).thenReturn("test.conf");

        var configData = loader.load(mock(ConfigDataLoaderContext.class), new HoconConfigDataResource(resource));

        assertThat(configData.getPropertySources()).hasSize(1);
        var propertySource = configData.getPropertySources().get(0);
        assertThat(propertySource.getName()).isEqualTo("test.conf");
        assertThat(propertySource.getProperty("app.name")).isEqualTo("bayonet");
        assertThat(propertySource.getProperty("server.port")).isEqualTo(8080);
    }
}
