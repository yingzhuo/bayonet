package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;

/**
 * TCP 端口连通性检测工具类。
 * <p>通过尝试建立 TCP Socket 连接判断目标地址的指定端口是否可达。
 * 适用于服务可用性探测、依赖服务健康检查等场景。</p>
 *
 * <pre>{@code
 * boolean reachable = SocketUtils.isReachable("example.com", 80, Duration.ofSeconds(5));
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocketUtils {

    /**
     * 检测目标地址是否可达。
     * <p>在指定超时时间内尝试建立 TCP 连接，连接成功返回 {@code true}，失败返回 {@code false}。</p>
     *
     * @param address         目标主机名或 IP 地址（非 {@code null}）
     * @param port            目标端口（1-65535）
     * @param timeoutInMillis 超时时间（毫秒）
     * @return 连接成功返回 {@code true}，否则 {@code false}
     */
    public static boolean isReachable(String address, int port, int timeoutInMillis) {
        Assert.notNull(address, "address must not be null");

        try (var socket = new Socket()) {
            socket.connect(new InetSocketAddress(address, port), timeoutInMillis);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 检测目标地址是否可达（{@link Duration} 超时版本）。
     *
     * @param address 目标主机名或 IP 地址（非 {@code null}）
     * @param port    目标端口（1-65535）
     * @param timeout 超时时间
     * @return 连接成功返回 {@code true}，否则 {@code false}
     * @see #isReachable(String, int, int)
     */
    public static boolean isReachable(String address, int port, Duration timeout) {
        return isReachable(address, port, (int) timeout.toMillis());
    }

}
