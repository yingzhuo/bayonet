package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CRC32UtilsTest {

    // ============== crc32Value ==============

    private static long crc32Reference(byte[] data) {
        var crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }

    @Test
    void should_return_zero_when_dataEmpty() {
        assertThat(CRC32Utils.crc32Value(new byte[0])).isZero();
    }

    @Test
    void should_return_correctValue_when_dataNotEmpty() {
        var data = "hello".getBytes(StandardCharsets.UTF_8);
        var expected = crc32Reference(data);
        assertThat(CRC32Utils.crc32Value(data)).isEqualTo(expected);
    }

    @Test
    void should_return_correctValue_when_utf8Chinese() {
        var data = "你好".getBytes(StandardCharsets.UTF_8);
        var expected = crc32Reference(data);
        assertThat(CRC32Utils.crc32Value(data)).isEqualTo(expected);
    }

    // ============== crc32Hex ==============

    @Test
    void should_throw_when_dataIsNull() {
        assertThatThrownBy(() -> CRC32Utils.crc32Value(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_return_8charHex_when_dataNotEmpty() {
        var hex = CRC32Utils.crc32Hex("hello".getBytes(StandardCharsets.UTF_8));
        assertThat(hex).hasSize(8);
    }

    @Test
    void should_return_zeroPadded_when_valueSmall() {
        // 极小值的 CRC32 应补前导零至 8 位
        var hex = CRC32Utils.crc32Hex(new byte[0]);
        assertThat(hex).isEqualTo("00000000");
    }

    @Test
    void should_return_expectedHex_when_dataKnown() {
        // "hello" 的 CRC32 = 0x3610A686
        var hex = CRC32Utils.crc32Hex("hello".getBytes(StandardCharsets.UTF_8));
        assertThat(hex).isEqualTo("3610a686");
    }

    @Test
    void should_throw_when_hexDataIsNull() {
        assertThatThrownBy(() -> CRC32Utils.crc32Hex(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
