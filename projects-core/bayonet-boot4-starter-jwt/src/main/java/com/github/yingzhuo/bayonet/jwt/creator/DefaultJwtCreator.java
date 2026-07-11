package com.github.yingzhuo.bayonet.jwt.creator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.util.Assert;

/**
 * 基于 {@link Algorithm} 的默认 JWT 创建器。
 * <p>将 {@link JwtData} 中的 header 和 payload 签名生成 JWT token。</p>
 *
 * <pre>{@code
 * var creator = new DefaultJwtCreator(algorithm);
 * String token = creator.create(JwtData.newInstance().addPayloadSubject("user123"));
 * }</pre>
 */
public class DefaultJwtCreator implements JwtCreator {

    private final Algorithm algorithm;

    /**
     * 构造器
     *
     * @param algorithm 签名算法，不能为 {@code null}
     */
    public DefaultJwtCreator(Algorithm algorithm) {
        Assert.notNull(algorithm, "algorithm must not be null");
        this.algorithm = algorithm;
    }

    @Override
    public String create(JwtData data) {
        Assert.notNull(data, "data must not be null");
        return JWT.create()
                .withHeader(data.getHeaderMap())
                .withPayload(data.getPayloadMap())
                .sign(algorithm);
    }

}
