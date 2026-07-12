package com.github.yingzhuo.bayonet.jwt.creator;

import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * JWT 数据构建器。
 * <p>提供流式 API 构建 JWT 的 header 和 payload 声明。</p>
 *
 * <pre>{@code
 * JwtData data = JwtData.newInstance()
 *     .addPayloadSubject("user123")
 *     .addPayloadIssuer("bayonet")
 *     .addPayloadExpiresAtFuture(Duration.ofHours(1));
 * }</pre>
 */
public final class JwtData implements JwtConstants {

    private final Map<String, Object> headerMap = new HashMap<>();
    private final Map<String, Object> payloadMap = new HashMap<>();

    public JwtData() {
        addHeaderType("JWT");
    }

    /**
     * 创建新的 {@link JwtData} 实例。
     *
     * @return 新的 JwtData 实例
     */
    public static JwtData newInstance() {
        return new JwtData();
    }

    private static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 设置 header 中的令牌类型。
     *
     * @param type 令牌类型
     * @return this
     */
    public JwtData addHeaderType(String type) {
        headerMap.put(HEADER_TYPE, type);
        return this;
    }

    /**
     * 设置 header 中的密钥标识。
     *
     * @param id 密钥标识
     * @return this
     */
    public JwtData addHeaderKeyId(String id) {
        headerMap.put(HEADER_KEY_ID, id);
        return this;
    }

    /**
     * 设置 header 中的密钥标识（通过 {@link Supplier} 延迟生成）。
     *
     * @param keyIdSupplier 密钥标识提供者
     * @return this
     * @throws IllegalArgumentException 若 {@code keyIdSupplier} 为 {@code null}
     */
    public JwtData addHeaderKeyId(Supplier<String> keyIdSupplier) {
        Assert.notNull(keyIdSupplier, "keyIdSupplier is null");
        return addHeaderKeyId(keyIdSupplier.get());
    }

    /**
     * 设置 header 中的内容类型。
     *
     * @param contentType 内容类型
     * @return this
     * @throws IllegalArgumentException 若 {@code contentType} 为空
     */
    public JwtData addHeaderContentType(String contentType) {
        Assert.hasText(contentType, "contentType is null or blank");
        headerMap.put(HEADER_CONTENT_TYPE, contentType);
        return this;
    }

    /**
     * 设置 header 中的签名算法。
     *
     * @param algorithm 算法名称
     * @return this
     * @deprecated JWT header 中的 {@code alg} 应由签名库自动设置，无需手动指定
     */
    @Deprecated
    public JwtData addHeaderAlgorithm(String algorithm) {
        Assert.hasText(algorithm, "algorithm is null or blank");
        headerMap.put(HEADER_ALGORITHM, algorithm);
        return this;
    }

    /**
     * 设置 payload 中的签发者。
     *
     * @param issuer 签发者
     * @return this
     * @throws IllegalArgumentException 若 {@code issuer} 为空
     */
    public JwtData addPayloadIssuer(String issuer) {
        Assert.hasText(issuer, "issuer is null or blank");
        payloadMap.put(PAYLOAD_ISSUER, issuer);
        return this;
    }

    /**
     * 设置 payload 中的主题。
     *
     * @param subject 主题
     * @return this
     * @throws IllegalArgumentException 若 {@code subject} 为空
     */
    public JwtData addPayloadSubject(String subject) {
        Assert.hasText(subject, "subject is null or blank");
        payloadMap.put(PAYLOAD_SUBJECT, subject);
        return this;
    }

    /**
     * 设置 payload 中的受众。
     *
     * @param audience 受众（可变参数）
     * @return this
     * @throws IllegalArgumentException 若 {@code audience} 为 {@code null}
     */
    public JwtData addPayloadAudience(String... audience) {
        Assert.notNull(audience, "audience is null");
        payloadMap.put(PAYLOAD_AUDIENCE, audience);
        return this;
    }

    /**
     * 设置 payload 中的过期时间。
     *
     * @param time 过期时间
     * @return this
     * @throws IllegalArgumentException 若 {@code time} 为 {@code null}
     */
    public JwtData addPayloadExpiresAt(LocalDateTime time) {
        Assert.notNull(time, "time is null");
        payloadMap.put(PAYLOAD_EXPIRES, toDate(time));
        return this;
    }

    /**
     * 设置 payload 中的过期时间（从当前时间起的一段时间后）。
     *
     * @param duration 有效期时长
     * @return this
     * @throws IllegalArgumentException 若 {@code duration} 为 {@code null}
     */
    public JwtData addPayloadExpiresAtFuture(Duration duration) {
        Assert.notNull(duration, "duration is null");
        payloadMap.put(PAYLOAD_EXPIRES, toDate(LocalDateTime.now().plus(duration)));
        return this;
    }

    /**
     * 设置 payload 中的生效时间。
     *
     * @param time 生效时间
     * @return this
     * @throws IllegalArgumentException 若 {@code time} 为 {@code null}
     */
    public JwtData addPayloadNotBefore(LocalDateTime time) {
        Assert.notNull(time, "time is null");
        payloadMap.put(PAYLOAD_NOT_BEFORE, toDate(time));
        return this;
    }

    /**
     * 设置 payload 中的生效时间（从当前时间起的一段时间后）。
     *
     * @param duration 等待时长
     * @return this
     * @throws IllegalArgumentException 若 {@code duration} 为 {@code null}
     */
    public JwtData addPayloadNotBeforeAtFuture(Duration duration) {
        Assert.notNull(duration, "duration is null");
        payloadMap.put(PAYLOAD_NOT_BEFORE, toDate(LocalDateTime.now().plus(duration)));
        return this;
    }

    /**
     * 设置 payload 中的签发时间。
     *
     * @param time 签发时间
     * @return this
     * @throws IllegalArgumentException 若 {@code time} 为 {@code null}
     */
    public JwtData addPayloadIssuedAt(LocalDateTime time) {
        Assert.notNull(time, "time is null");
        payloadMap.put(PAYLOAD_ISSUED_AT, toDate(time));
        return this;
    }

    /**
     * 设置 payload 中的签发时间为当前时间。
     *
     * @return this
     */
    public JwtData addPayloadIssuedAtNow() {
        return addPayloadIssuedAt(LocalDateTime.now());
    }

    /**
     * 设置 payload 中的 JWT ID。
     *
     * @param jwtId JWT ID
     * @return this
     * @throws IllegalArgumentException 若 {@code jwtId} 为 {@code null}
     */
    public JwtData addPayloadJwtId(Object jwtId) {
        Assert.notNull(jwtId, "jwtId is null");
        payloadMap.put(PAYLOAD_JWT_ID, jwtId);
        return this;
    }

    /**
     * 设置 payload 中的 JWT ID（通过 {@link Supplier} 延迟生成）。
     *
     * @param jwtIdSupplier JWT ID 提供者
     * @return this
     * @throws IllegalArgumentException 若 {@code jwtIdSupplier} 为 {@code null}
     */
    public JwtData addPayloadJwtId(Supplier<Object> jwtIdSupplier) {
        Assert.notNull(jwtIdSupplier, "jwtIdSupplier is null");
        return addPayloadJwtId(jwtIdSupplier.get());
    }

    /**
     * 添加自定义 header。
     *
     * @param name  header 名称
     * @param value header 值
     * @return this
     * @throws IllegalArgumentException 若 {@code name} 为空或 {@code value} 为 {@code null}
     */
    public JwtData addHeader(String name, Object value) {
        Assert.hasText(name, "name is null or blank");
        Assert.notNull(value, "value is null");
        headerMap.put(name, value);
        return this;
    }

    /**
     * 添加自定义 payload。
     *
     * @param name  payload 名称
     * @param value payload 值
     * @return this
     * @throws IllegalArgumentException 若 {@code name} 为空或 {@code value} 为 {@code null}
     */
    public JwtData addPayload(String name, Object value) {
        Assert.hasText(name, "name is null or blank");
        Assert.notNull(value, "value is null");
        payloadMap.put(name, value);
        return this;
    }

    /**
     * 获取 header 的不可变 Map。
     *
     * @return header Map
     */
    public Map<String, Object> getHeaderMap() {
        return Collections.unmodifiableMap(this.headerMap);
    }

    /**
     * 获取 payload 的不可变 Map。
     *
     * @return payload Map
     */
    public Map<String, Object> getPayloadMap() {
        return Collections.unmodifiableMap(this.payloadMap);
    }

}
