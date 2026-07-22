package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HexUtilsTest {

    private static final byte[] BYTES = "hello".getBytes(StandardCharsets.UTF_8);
    private static final String HEX = "68656c6c6f";

    // ============== encodeToString ==============

    @Test
    void should_encode_when_bytesNotEmpty() {
        assertThat(HexUtils.encodeToString(BYTES)).isEqualTo(HEX);
    }

    @Test
    void should_encode_when_bytesEmpty() {
        assertThat(HexUtils.encodeToString(new byte[0])).isEmpty();
    }

    @Test
    void should_encode_when_bytesContainsAllValues() {
        var all = new byte[256];
        for (int i = 0; i < 256; i++) all[i] = (byte) i;
        var hex = HexUtils.encodeToString(all);
        assertThat(hex).hasSize(512);
        assertThat(hex).startsWith("00").endsWith("ff");
    }

    @Test
    void should_throw_when_encode_null() {
        assertThatThrownBy(() -> HexUtils.encodeToString(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== decodeToBytes ==============

    @Test
    void should_decode_when_hexStringValid() {
        assertThat(HexUtils.decodeToBytes(HEX)).containsExactly(BYTES);
    }

    @Test
    void should_decode_when_hexStringEmpty() {
        assertThat(HexUtils.decodeToBytes("")).isEmpty();
    }

    @Test
    void should_decode_when_hexStringHasUpperCase() {
        var upper = HEX.toUpperCase();
        assertThat(HexUtils.decodeToBytes(upper)).containsExactly(BYTES);
    }

    @Test
    void should_throw_when_decode_null() {
        assertThatThrownBy(() -> HexUtils.decodeToBytes(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_decode_oddLength() {
        assertThatThrownBy(() -> HexUtils.decodeToBytes("abc"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_decode_invalidChars() {
        assertThatThrownBy(() -> HexUtils.decodeToBytes("xyz123"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== round-trip ==============

    @Test
    void should_roundtrip_when_encodeThenDecode() {
        var original = "你好, world!".getBytes(StandardCharsets.UTF_8);
        var encoded = HexUtils.encodeToString(original);
        var decoded = HexUtils.decodeToBytes(encoded);
        assertThat(decoded).containsExactly(original);
    }

}
