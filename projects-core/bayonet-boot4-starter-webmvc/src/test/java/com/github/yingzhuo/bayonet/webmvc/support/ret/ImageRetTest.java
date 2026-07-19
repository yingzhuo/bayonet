package com.github.yingzhuo.bayonet.webmvc.support.ret;

import com.github.yingzhuo.bayonet.webmvc.support.ContentDispositionType;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageRetTest {

    private static final BufferedImage TEST_IMAGE = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

    // ============== 基础构建 ==============

    @Test
    void should_build_with_image() {
        var result = ImageRet.builder()
                .image(TEST_IMAGE)
                .build();

        assertThat(result).isNotNull();
        assertThat(result.image()).isEqualTo(TEST_IMAGE);
    }

    @Test
    void should_return_defaults() {
        var result = ImageRet.builder()
                .image(TEST_IMAGE)
                .build();

        assertThat(result.maxAge()).isEqualTo(-1);
        assertThat(result.contentType()).isEqualTo("image/png");
        assertThat(result.filename()).isEqualTo("image.png");
        assertThat(result.contentDispositionType()).isEqualTo(ContentDispositionType.INLINE);
    }

    // ============== 自定义字段 ==============

    @Test
    void should_set_maxAge() {
        var result = ImageRet.builder()
                .image(TEST_IMAGE)
                .maxAge(3600)
                .build();

        assertThat(result.maxAge()).isEqualTo(3600);
    }

    @Test
    void should_set_contentType() {
        var result = ImageRet.builder()
                .image(TEST_IMAGE)
                .contentType("image/jpeg")
                .build();

        assertThat(result.contentType()).isEqualTo("image/jpeg");
    }

    @Test
    void should_set_filename() {
        var result = ImageRet.builder()
                .image(TEST_IMAGE)
                .filename("photo.jpg")
                .build();

        assertThat(result.filename()).isEqualTo("photo.jpg");
    }

    @Test
    void should_set_contentDispositionType() {
        var result = ImageRet.builder()
                .image(TEST_IMAGE)
                .contentDispositionType(ContentDispositionType.ATTACHMENT)
                .build();

        assertThat(result.contentDispositionType()).isEqualTo(ContentDispositionType.ATTACHMENT);
    }

    @Test
    void should_set_negative_maxAge() {
        var result = ImageRet.builder()
                .image(TEST_IMAGE)
                .maxAge(-1)
                .build();

        assertThat(result.maxAge()).isNegative();
    }

    @Test
    void should_set_zero_maxAge() {
        var result = ImageRet.builder()
                .image(TEST_IMAGE)
                .maxAge(0)
                .build();

        assertThat(result.maxAge()).isZero();
    }

    @Test
    void should_support_chained_calls() {
        var result = ImageRet.builder()
                .image(TEST_IMAGE)
                .maxAge(600)
                .contentType("image/webp")
                .filename("photo.webp")
                .contentDispositionType(ContentDispositionType.ATTACHMENT)
                .build();

        assertThat(result.image()).isEqualTo(TEST_IMAGE);
        assertThat(result.maxAge()).isEqualTo(600);
        assertThat(result.contentType()).isEqualTo("image/webp");
        assertThat(result.filename()).isEqualTo("photo.webp");
        assertThat(result.contentDispositionType()).isEqualTo(ContentDispositionType.ATTACHMENT);
    }

    // ============== NPE ==============

    @Test
    void should_throw_when_image_is_null() {
        assertThatThrownBy(() -> ImageRet.builder().image(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("image");
    }

    @Test
    void should_throw_when_contentType_is_null() {
        assertThatThrownBy(() -> ImageRet.builder()
                .image(TEST_IMAGE)
                .contentType(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_throw_when_filename_is_null() {
        assertThatThrownBy(() -> ImageRet.builder()
                .image(TEST_IMAGE)
                .filename(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_throw_when_contentDispositionType_is_null() {
        assertThatThrownBy(() -> ImageRet.builder()
                .image(TEST_IMAGE)
                .contentDispositionType(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_throw_when_build_without_image() {
        assertThatThrownBy(() -> ImageRet.builder().build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("image");
    }

}
