package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SocketUtilsTest {

    @Test
    void should_throw_when_addressIsNull() {
        assertThatThrownBy(() -> SocketUtils.isReachable(null, 80, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("address must not be null");
    }

    @Test
    void should_returnFalse_when_portClosed() {
        // 连接本地一个大概率无服务的端口，应快速返回 Connection refused
        var result = SocketUtils.isReachable("127.0.0.1", 1, 1000);
        assertThat(result).isFalse();
    }

    @Test
    void should_returnFalse_when_timeoutIsZero() {
        // timeout=0 表示无限等待，连接本地关闭端口会立即失败而非超时
        var result = SocketUtils.isReachable("127.0.0.1", 1, 0);
        assertThat(result).isFalse();
    }

    @Test
    void should_delegate_when_usingDurationOverload() {
        var result = SocketUtils.isReachable("127.0.0.1", 1, Duration.ofMillis(500));
        assertThat(result).isFalse();
    }

}
