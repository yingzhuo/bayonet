package com.github.yingzhuo.bayonet.toml.configdata;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.core.io.Resource;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TomlConfigDataLoaderTest {

    private final TomlConfigDataLoader loader = new TomlConfigDataLoader();

    @Test
    void should_load_toml_into_config_data() throws Exception {
        var tempFile = Files.createTempFile("test-", ".toml").toFile();
        tempFile.deleteOnExit();
        Files.writeString(tempFile.toPath(), "[app]\nname = \"bayonet\"\n\n[server]\nport = 8080");

        var resource = mock(Resource.class);
        when(resource.getInputStream()).thenReturn(Files.newInputStream(tempFile.toPath()));
        when(resource.getFilename()).thenReturn("test.toml");

        var configData = loader.load(mock(ConfigDataLoaderContext.class), new TomlConfigDataResource(resource));

        assertThat(configData.getPropertySources()).hasSize(1);
        var propertySource = configData.getPropertySources().get(0);
        assertThat(propertySource.getName()).isEqualTo("test.toml");
        assertThat(propertySource.getProperty("app.name")).isEqualTo("bayonet");
        assertThat(propertySource.getProperty("server.port")).isEqualTo(8080L);
    }
}
