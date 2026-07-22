package com.github.yingzhuo.bayonet.webmvc.support.ret;

import com.github.yingzhuo.bayonet.webmvc.support.ContentDispositionType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttachmentRetTest {

    private static final InputStream TEST_INPUT_STREAM = new ByteArrayInputStream("hello".getBytes());

    // ============== 基础构建 ==============

    @Test
    void should_build_with_inputStream_and_filename() {
        var result = AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .filename("test.txt")
                .build();

        assertThat(result).isNotNull();
        assertThat(result.inputStream()).isEqualTo(TEST_INPUT_STREAM);
        assertThat(result.filename()).isEqualTo("test.txt");
    }

    @Test
    void should_return_defaults() {
        var result = AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .filename("test.txt")
                .build();

        assertThat(result.contentType()).isEqualTo("application/octet-stream");
        assertThat(result.contentDispositionType()).isEqualTo(ContentDispositionType.ATTACHMENT);
        assertThat(result.httpStatus()).isEqualTo(HttpStatus.OK);
    }

    // ============== 自定义字段 ==============

    @Test
    void should_set_contentType() {
        var result = AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .filename("test.txt")
                .contentType("application/pdf")
                .build();

        assertThat(result.contentType()).isEqualTo("application/pdf");
    }

    @Test
    void should_set_contentDispositionType() {
        var result = AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .filename("test.txt")
                .contentDispositionType(ContentDispositionType.INLINE)
                .build();

        assertThat(result.contentDispositionType()).isEqualTo(ContentDispositionType.INLINE);
    }

    @Test
    void should_set_httpStatus() {
        var result = AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .filename("test.txt")
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();

        assertThat(result.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void should_support_chained_calls() {
        var result = AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .filename("report.pdf")
                .contentType("application/pdf")
                .contentDispositionType(ContentDispositionType.ATTACHMENT)
                .httpStatus(HttpStatus.CREATED)
                .build();

        assertThat(result.inputStream()).isEqualTo(TEST_INPUT_STREAM);
        assertThat(result.filename()).isEqualTo("report.pdf");
        assertThat(result.contentType()).isEqualTo("application/pdf");
        assertThat(result.contentDispositionType()).isEqualTo(ContentDispositionType.ATTACHMENT);
        assertThat(result.httpStatus()).isEqualTo(HttpStatus.CREATED);
    }

    // ============== NPE ==============

    @Test
    void should_throw_when_inputStream_is_null() {
        assertThatThrownBy(() -> AttachmentRet.builder().inputStream(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("inputStream");
    }

    @Test
    void should_throw_when_filename_is_null() {
        assertThatThrownBy(() -> AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .filename(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("filename");
    }

    @Test
    void should_throw_when_contentType_is_null() {
        assertThatThrownBy(() -> AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .filename("test.txt")
                .contentType(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_throw_when_contentDispositionType_is_null() {
        assertThatThrownBy(() -> AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .filename("test.txt")
                .contentDispositionType(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_throw_when_httpStatus_is_null() {
        assertThatThrownBy(() -> AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .filename("test.txt")
                .httpStatus(null))
                .isInstanceOf(NullPointerException.class);
    }

    // ============== 必填项校验 ==============

    @Test
    void should_throw_when_build_without_inputStream() {
        assertThatThrownBy(() -> AttachmentRet.builder()
                .filename("test.txt")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("inputStream");
    }

    @Test
    void should_throw_when_build_without_filename() {
        assertThatThrownBy(() -> AttachmentRet.builder()
                .inputStream(TEST_INPUT_STREAM)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("filename");
    }

    @Test
    void should_throw_when_build_without_both() {
        assertThatThrownBy(() -> AttachmentRet.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("inputStream");
    }

}
