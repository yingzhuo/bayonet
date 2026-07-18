package com.github.yingzhuo.bayonet.zxing.service;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BarCodeGeneratorImplTest {

    private final BarCodeGeneratorImpl generator = new BarCodeGeneratorImpl();

    // ============== 正常生成 ==============

    @Test
    void should_generate_barcode() {
        var image = generator.generate("1234567890", 200, 100);
        assertThat(image).isNotNull();
        assertThat(image.getWidth()).isEqualTo(200);
        assertThat(image.getHeight()).isEqualTo(100);
    }

    @Test
    void should_generate_barcode_with_long_content() {
        var image = generator.generate("ABC-12345-67890-XYZ", 400, 150);
        assertThat(image).isNotNull();
    }

    @Test
    void should_generate_barcode_with_numbers() {
        var image = generator.generate("0123456789", 300, 100);
        assertThat(image).isNotNull();
        assertThat(image.getWidth()).isEqualTo(300);
    }

    // ============== 参数校验 ==============

    @Test
    void should_throw_when_content_is_null() {
        assertThatThrownBy(() -> generator.generate(null, 200, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content");
    }

    @Test
    void should_throw_when_content_is_empty() {
        assertThatThrownBy(() -> generator.generate("", 200, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content");
    }

    @Test
    void should_throw_when_content_is_blank() {
        assertThatThrownBy(() -> generator.generate("   ", 200, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content");
    }

    @Test
    void should_throw_when_width_is_zero() {
        assertThatThrownBy(() -> generator.generate("test", 0, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("width");
    }

    @Test
    void should_throw_when_width_is_negative() {
        assertThatThrownBy(() -> generator.generate("test", -1, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("width");
    }

    @Test
    void should_throw_when_height_is_zero() {
        assertThatThrownBy(() -> generator.generate("test", 200, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("height");
    }

    @Test
    void should_throw_when_height_is_negative() {
        assertThatThrownBy(() -> generator.generate("test", 200, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("height");
    }

}
