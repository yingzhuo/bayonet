package com.github.yingzhuo.bayonet.jwt.validator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 基于 {@link Algorithm} 的默认 JWT 验证器。
 * <p>使用 java-jwt 库验证 token，将异常映射为 {@link ValidatingResult} 枚举。</p>
 *
 * <pre>{@code
 * var validator = new DefaultJwtValidator(algorithm);
 * ValidatingResult result = validator.validateToken(token);
 * }</pre>
 */
public class DefaultJwtValidator implements JwtValidator {

    private final Algorithm algorithm;
    private final VerificationCustomizer verificationCustomizer;

    /**
     * 构造器
     *
     * @param algorithm 签名算法，不能为 {@code null}
     */
    public DefaultJwtValidator(Algorithm algorithm) {
        this(algorithm, null);
    }

    /**
     * 构造器（支持自定义验证配置）
     *
     * @param algorithm              签名算法，不能为 {@code null}
     * @param verificationCustomizer 验证配置定制器，可为 {@code null}
     */
    public DefaultJwtValidator(Algorithm algorithm, @Nullable VerificationCustomizer verificationCustomizer) {
        Assert.notNull(algorithm, "algorithm must not be null");
        this.algorithm = algorithm;
        this.verificationCustomizer = Objects.requireNonNullElse(verificationCustomizer, v -> {
        });
    }

    @Override
    public ValidatingResult validate(String token) {
        Assert.notNull(token, "token must not be null");

        try {
            var verification = JWT.require(algorithm);
            verificationCustomizer.customize(verification);
            verification.build().verify(token);
        } catch (MissingClaimException ex) {
            return ValidatingResult.INVALID_CLAIM;
        } catch (IncorrectClaimException ex) {
            var claimName = ex.getClaimName();
            if ("nbf".equals(claimName) || "iat".equals(claimName)) {
                return ValidatingResult.INVALID_TIME;
            }
            return ValidatingResult.INVALID_CLAIM;
        } catch (TokenExpiredException ex) {
            return ValidatingResult.INVALID_TIME;
        } catch (SignatureVerificationException ex) {
            return ValidatingResult.INVALID_SIGNATURE;
        } catch (JWTVerificationException exception) {
            return ValidatingResult.INVALID_JWT_FORMAT;
        }
        return ValidatingResult.OK;
    }

}
