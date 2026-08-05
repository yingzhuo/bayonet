package com.github.yingzhuo.bayonet.jwt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * JWT 负载（payload）接口。
 * <p>提供 JWT 标准注册声明的查询方法。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public interface Payload {

    /**
     * 获取 {@code iss} 声明的值，若不存在返回 {@code null}。
     *
     * @return 签发者（Issuer）值或 {@code null}
     */
    String getIssuer();

    /**
     * 获取 {@code sub} 声明的值，若不存在返回 {@code null}。
     *
     * @return 主题（Subject）值或 {@code null}
     */
    String getSubject();

    /**
     * 获取 {@code aud} 声明的值，若不存在返回 {@code null}。
     *
     * @return 受众（Audience）值或 {@code null}
     */
    List<String> getAudience();

    /**
     * 获取 {@code exp} 声明的值，若不存在返回 {@code null}。
     *
     * @return 过期时间的 {@link LocalDateTime} 值或 {@code null}
     */
    LocalDateTime getExpiresAt();

    /**
     * 获取 {@code exp} 声明的 {@link Instant} 表示（使用系统默认时区），若不存在返回 {@code null}。
     *
     * @return 过期时间的 {@link Instant} 表示或 {@code null}
     */
    default Instant getExpiresAtAsInstant() {
        return getExpiresAt() != null ? getExpiresAt().atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    /**
     * 获取 {@code nbf} 声明的值，若不存在返回 {@code null}。
     *
     * @return 生效时间的 {@link LocalDateTime} 值或 {@code null}
     */
    LocalDateTime getNotBefore();

    /**
     * 获取 {@code nbf} 声明的 {@link Instant} 表示（使用系统默认时区），若不存在返回 {@code null}。
     *
     * @return 生效时间的 {@link Instant} 表示或 {@code null}
     */
    default Instant getNotBeforeAsInstant() {
        return getNotBefore() != null ? getNotBefore().atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    /**
     * 获取 {@code iat} 声明的值，若不存在返回 {@code null}。
     *
     * @return 签发时间的 {@link LocalDateTime} 值或 {@code null}
     */
    LocalDateTime getIssuedAt();

    /**
     * 获取 {@code iat} 声明的 {@link Instant} 表示（使用系统默认时区），若不存在返回 {@code null}。
     *
     * @return 签发时间的 {@link Instant} 表示或 {@code null}
     */
    default Instant getIssuedAtAsInstant() {
        return getIssuedAt() != null ? getIssuedAt().atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    /**
     * 获取 {@code jti} 声明的值，若不存在返回 {@code null}。
     *
     * @return JWT 唯一标识（JWT ID）值或 {@code null}
     */
    String getId();

    /**
     * 按名称获取声明。
     * <p>若声明未在负载中指定，返回一个 {@code null} 声明，其所有方法均返回 {@code null}。</p>
     *
     * @param name 声明名称
     * @return 声明（非 {@code null}）
     */
    Claim getClaim(String name);

    /**
     * 获取负载中定义的所有声明。
     *
     * @return 包含所有声明的 {@link Map}（非 {@code null}）
     */
    Map<String, Claim> getClaims();
}
