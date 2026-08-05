package com.github.yingzhuo.bayonet.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link JwtDescriptor} 基于 {@link DecodedJWT} 的默认实现。
 * <p>装饰 {@link DecodedJWT}，将其中各方法映射到 {@link JwtDescriptor} 接口。</p>
 *
 * @author 应卓
 * @see JwtDescriptor
 * @see DecodedJWT
 * @since 4.1.1
 */
public class DefaultJwtDescriptor implements JwtDescriptor {

    private final DecodedJWT decodedJwt;

    /**
     * 构造器。
     *
     * @param decodedJwt 已解码的 JWT（非 {@code null}）
     */
    public DefaultJwtDescriptor(DecodedJWT decodedJwt) {
        Assert.notNull(decodedJwt, "decodedJwt must not be null");
        this.decodedJwt = decodedJwt;
    }

    private static Claim toClaim(com.auth0.jwt.interfaces.Claim claim) {
        return new Claim() {
            @Override
            public boolean isNull() {
                return claim.isNull();
            }

            @Override
            public boolean isMissing() {
                return claim.isMissing();
            }

            @Override
            public Boolean asBoolean() {
                return claim.asBoolean();
            }

            @Override
            public Integer asInt() {
                return claim.asInt();
            }

            @Override
            public Long asLong() {
                return claim.asLong();
            }

            @Override
            public Double asDouble() {
                return claim.asDouble();
            }

            @Override
            public String asString() {
                return claim.asString();
            }

            @Override
            public @Nullable LocalDateTime asLocalDateTime() {
                var date = claim.asDate();
                return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
            }

            @Override
            public <T> T[] asArray(Class<T> clazz) {
                return claim.asArray(clazz);
            }

            @Override
            public <T> List<T> asList(Class<T> clazz) {
                return claim.asList(clazz);
            }

            @Override
            public Map<String, Object> asMap() {
                return claim.asMap();
            }
        };
    }

    @Override
    public String getJwtItself() {
        return decodedJwt.getToken();
    }

    @Override
    public String getHeaderPart() {
        return decodedJwt.getHeader();
    }

    @Override
    public String getPayloadPart() {
        return decodedJwt.getPayload();
    }

    // Header

    @Override
    public String getSignaturePart() {
        return decodedJwt.getSignature();
    }

    @Override
    public String getAlgorithm() {
        return decodedJwt.getAlgorithm();
    }

    @Override
    public @Nullable String getType() {
        return decodedJwt.getType();
    }

    @Override
    public @Nullable String getContentType() {
        return decodedJwt.getContentType();
    }

    @Override
    public @Nullable String getKeyId() {
        return decodedJwt.getKeyId();
    }

    // Payload

    @Override
    public Claim getHeaderClaim(String name) {
        return toClaim(decodedJwt.getHeaderClaim(name));
    }

    @Override
    public @Nullable String getIssuer() {
        return decodedJwt.getIssuer();
    }

    @Override
    public @Nullable String getSubject() {
        return decodedJwt.getSubject();
    }

    @Override
    public @Nullable List<String> getAudience() {
        return decodedJwt.getAudience();
    }

    @Override
    public @Nullable LocalDateTime getExpiresAt() {
        var date = decodedJwt.getExpiresAt();
        return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    @Override
    public @Nullable LocalDateTime getNotBefore() {
        var date = decodedJwt.getNotBefore();
        return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    @Override
    public @Nullable LocalDateTime getIssuedAt() {
        var date = decodedJwt.getIssuedAt();
        return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    @Override
    public @Nullable String getId() {
        return decodedJwt.getId();
    }

    @Override
    public Claim getClaim(String name) {
        return toClaim(decodedJwt.getClaim(name));
    }

    // ------

    @Override
    public Map<String, Claim> getClaims() {
        return decodedJwt.getClaims().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toClaim(e.getValue())));
    }
}
