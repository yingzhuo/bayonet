package com.github.yingzhuo.bayonet.zxing.service;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QRCodeGeneratorImplTest {

    private final QRCodeGeneratorImpl generator = new QRCodeGeneratorImpl();

    // ============== 正常生成 ==============

    @Test
    void should_generate_qrCode() {
        var image = generator.generate("https://example.com", null, null, 300);
        assertThat(image).isNotNull();
        assertThat(image.getWidth()).isEqualTo(300);
        assertThat(image.getHeight()).isEqualTo(300);
    }

    @Test
    void should_generate_qrCode_with_long_content() {
        var image = generator.generate("https://example.com/very/long/path/with/many/segments", null, null, 300);
        assertThat(image).isNotNull();
    }

    @Test
    void should_generate_qrCode_with_chinese_content() {
        var image = generator.generate("你好世界", null, null, 300);
        assertThat(image).isNotNull();
    }

    @Test
    void should_generate_qrCode_with_special_chars() {
        var image = generator.generate("Hello! @#$%^&*()_+", null, null, 300);
        assertThat(image).isNotNull();
    }

    // ============== ErrorCorrectionLevel ==============

    @Test
    void should_generate_with_errorCorrectionLevel_L() {
        var image = generator.generate("test", null, ErrorCorrectionLevel.L, 300);
        assertThat(image).isNotNull();
    }

    @Test
    void should_generate_with_errorCorrectionLevel_M() {
        var image = generator.generate("test", null, ErrorCorrectionLevel.M, 300);
        assertThat(image).isNotNull();
    }

    @Test
    void should_generate_with_errorCorrectionLevel_Q() {
        var image = generator.generate("test", null, ErrorCorrectionLevel.Q, 300);
        assertThat(image).isNotNull();
    }

    @Test
    void should_generate_with_errorCorrectionLevel_H() {
        var image = generator.generate("test", null, ErrorCorrectionLevel.H, 300);
        assertThat(image).isNotNull();
    }

    // ============== 参数校验 ==============

    @Test
    void should_throw_when_content_is_null() {
        assertThatThrownBy(() -> generator.generate(null, null, null, 300))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content");
    }

    @Test
    void should_throw_when_content_is_empty() {
        assertThatThrownBy(() -> generator.generate("", null, null, 300))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content");
    }

    @Test
    void should_throw_when_content_is_blank() {
        assertThatThrownBy(() -> generator.generate("   ", null, null, 300))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content");
    }

    @Test
    void should_throw_when_size_is_zero() {
        assertThatThrownBy(() -> generator.generate("test", null, null, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("size");
    }

    @Test
    void should_throw_when_size_is_negative() {
        assertThatThrownBy(() -> generator.generate("test", null, null, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("size");
    }

}
