package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropertiesUtilsTest {

    private static Properties createProperties() {
        var props = new Properties();
        props.setProperty("key1", "value1");
        props.setProperty("key2", "value2");
        return props;
    }

    // ============== toStringObjectMap ==============

    @Test
    void should_return_emptyMap_when_propertiesIsNull_toStringObjectMap() {
        assertThat(PropertiesUtils.toStringObjectMap(null)).isEmpty();
    }

    @Test
    void should_return_emptyMap_when_propertiesIsEmpty_toStringObjectMap() {
        assertThat(PropertiesUtils.toStringObjectMap(new Properties())).isEmpty();
    }

    @Test
    void should_return_map_when_propertiesHasEntries_toStringObjectMap() {
        var result = PropertiesUtils.toStringObjectMap(createProperties());
        assertThat(result).containsExactly(
                Map.entry("key1", "value1"),
                Map.entry("key2", "value2")
        );
    }

    @Test
    void should_return_unmodifiableMap_toStringObjectMap() {
        var result = PropertiesUtils.toStringObjectMap(createProperties());
        assertThatThrownBy(() -> result.put("k", "v"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // ============== toStringMap ==============

    @Test
    void should_return_emptyMap_when_propertiesIsNull_toStringMap() {
        assertThat(PropertiesUtils.toStringMap(null)).isEmpty();
    }

    @Test
    void should_return_emptyMap_when_propertiesIsEmpty_toStringMap() {
        assertThat(PropertiesUtils.toStringMap(new Properties())).isEmpty();
    }

    @Test
    void should_return_map_when_propertiesHasEntries_toStringMap() {
        var result = PropertiesUtils.toStringMap(createProperties());
        assertThat(result).containsExactly(
                Map.entry("key1", "value1"),
                Map.entry("key2", "value2")
        );
    }

    @Test
    void should_return_unmodifiableMap_toStringMap() {
        var result = PropertiesUtils.toStringMap(createProperties());
        assertThatThrownBy(() -> result.put("k", "v"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // ============== toMapPropertySource ==============

    @Test
    void should_create_MapPropertySource_when_propertiesValid() {
        var source = PropertiesUtils.toMapPropertySource("test", createProperties());
        assertThat(source.getName()).isEqualTo("test");
        assertThat(source.getSource()).containsExactly(
                Map.entry("key1", "value1"),
                Map.entry("key2", "value2")
        );
    }

    @Test
    void should_create_MapPropertySource_when_propertiesNull() {
        var source = PropertiesUtils.toMapPropertySource("test", null);
        assertThat(source.getName()).isEqualTo("test");
        assertThat(source.getSource()).isEmpty();
    }

    @Test
    void should_throw_when_nameIsBlank() {
        assertThatThrownBy(() -> PropertiesUtils.toMapPropertySource("", createProperties()))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
