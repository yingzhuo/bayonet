package com.github.yingzhuo.bayonet.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

/**
 * 基于 {@link Algorithm} 的默认 JWT 创建器。
 * <p>将 {@link JwtData} 中的 header 和 payload 签名生成 JWT token。
 * 当 {@link JtiGenerator} 非 {@code null} 时，其生成的 {@code jti} 值优先级最高，
 * 会覆盖 {@link JwtData} 中通过 {@link JwtData#addPayloadJwtId(Object)} 设置的任何值。</p>
 *
 * <pre>{@code
 * var creator = new DefaultJwtCreator(algorithm);
 * String token = creator.create(JwtData.newInstance().addPayloadSubject("user123"));
 * }</pre>
 *
 * @author 应卓
 * @see JtiGenerator
 * @see JwtData
 * @since 4.1.0
 */
public class DefaultJwtCreator implements JwtCreator {

    private final Algorithm algorithm;
    private final @Nullable JtiGenerator jtiGenerator;

    /**
     * 构造器。
     *
     * @param algorithm 签名算法（非 {@code null}）
     */
    public DefaultJwtCreator(Algorithm algorithm) {
        this(algorithm, null);
    }

    /**
     * 构造器。
     *
     * @param algorithm    签名算法（非 {@code null}）
     * @param jtiGenerator JWT ID 生成器，可为 {@code null}。
     *                     非 {@code null} 时，生成的 {@code jti} 会覆盖
     *                     {@link JwtData} payload 中的同名声明
     */
    public DefaultJwtCreator(Algorithm algorithm, @Nullable JtiGenerator jtiGenerator) {
        Assert.notNull(algorithm, "algorithm must not be null");
        this.algorithm = algorithm;
        this.jtiGenerator = jtiGenerator;
    }

    @Override
    public String create(JwtData data) {
        Assert.notNull(data, "data must not be null");
        var builder = JWT.create()
                .withHeader(data.getHeaderMap())
                .withPayload(data.getPayloadMap());

        if (this.jtiGenerator != null) {
            var jti = jtiGenerator.generate();
            Assert.notNull(jti, "jtiGenerator.generate() must not return null");
            builder.withJWTId(jti);
        }

        return builder.sign(algorithm);
    }
}
