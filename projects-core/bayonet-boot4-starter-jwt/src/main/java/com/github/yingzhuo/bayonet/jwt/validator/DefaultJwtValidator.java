package com.github.yingzhuo.bayonet.jwt.validator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.github.yingzhuo.bayonet.jwt.blacklist.BlacklistChecker;
import com.github.yingzhuo.bayonet.jwt.creator.JwtConstants;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 基于 {@link Algorithm} 的默认 JWT 验证器。
 * <p>验证流程分为两个阶段：</p>
 * <ol>
 *   <li><b>黑名单检查</b> — 解码 token 后查询 {@link BlacklistChecker}，若命中则返回 {@link ValidatingResult#INVALID_BLACKLISTED}</li>
 *   <li><b>签名与声明校验</b> — 使用 {@link VerificationCustomizer} 定制验证规则后，校验签名和声明</li>
 * </ol>
 *
 * <p>异常与返回值的映射关系：</p>
 * <table>
 *   <caption>异常-结果映射</caption>
 *   <tr><td>{@link JWTDecodeException}</td><td>→ {@link ValidatingResult#INVALID_JWT_FORMAT}</td></tr>
 *   <tr><td>{@link SignatureVerificationException}</td><td>→ {@link ValidatingResult#INVALID_SIGNATURE}</td></tr>
 *   <tr><td>{@link TokenExpiredException}</td><td>→ {@link ValidatingResult#INVALID_TIME}</td></tr>
 *   <tr><td>{@link MissingClaimException}</td><td>→ {@link ValidatingResult#INVALID_CLAIM}</td></tr>
 *   <tr><td>{@link IncorrectClaimException}（nbf / iat）</td><td>→ {@link ValidatingResult#INVALID_TIME}</td></tr>
 *   <tr><td>{@link IncorrectClaimException}（其他）</td><td>→ {@link ValidatingResult#INVALID_CLAIM}</td></tr>
 *   <tr><td>{@link JWTVerificationException}（其他）</td><td>→ {@link ValidatingResult#INVALID_JWT_FORMAT}</td></tr>
 * </table>
 *
 * <pre>{@code
 * var validator = new DefaultJwtValidator(algorithm);
 * ValidatingResult result = validator.validate(token);
 * }</pre>
 *
 * @see JwtValidator
 * @see ValidatingResult
 * @see VerificationCustomizer
 * @see BlacklistChecker
 */
public class DefaultJwtValidator implements JwtValidator {

    private final Algorithm algorithm;
    private final VerificationCustomizer verificationCustomizer;
    private final BlacklistChecker blacklistChecker;

    /**
     * 构造器。
     *
     * @param algorithm 签名算法（非 {@code null}）
     */
    public DefaultJwtValidator(Algorithm algorithm) {
        this(algorithm, null, null);
    }

    /**
     * 构造器。
     *
     * @param algorithm              签名算法（非 {@code null}）
     * @param verificationCustomizer 验证配置定制器，为 {@code null} 时使用无操作默认值
     */
    public DefaultJwtValidator(Algorithm algorithm, @Nullable VerificationCustomizer verificationCustomizer) {
        this(algorithm, verificationCustomizer, null);
    }

    /**
     * 构造器。
     *
     * @param algorithm              签名算法（非 {@code null}）
     * @param verificationCustomizer 验证配置定制器，为 {@code null} 时使用无操作默认值
     * @param blacklistChecker       黑名单检查器，为 {@code null} 时使用永不命中默认值
     */
    public DefaultJwtValidator(Algorithm algorithm, @Nullable VerificationCustomizer verificationCustomizer, @Nullable BlacklistChecker blacklistChecker) {
        Assert.notNull(algorithm, "algorithm must not be null");
        this.algorithm = algorithm;
        this.verificationCustomizer = Objects.requireNonNullElse(verificationCustomizer, v -> {
        });
        this.blacklistChecker = Objects.requireNonNullElse(blacklistChecker, (r, d) -> false);
    }

    @Override
    public ValidatingResult validate(String token) {
        Assert.notNull(token, "token must not be null");

        try {
            var decodedToken = JWT.decode(token);
            if (this.blacklistChecker.isBlacklisted(token, decodedToken)) {
                return ValidatingResult.INVALID_BLACKLISTED;
            }
        } catch (JWTDecodeException e) {
            return ValidatingResult.INVALID_JWT_FORMAT;
        }

        try {
            var verification = JWT.require(algorithm);
            verificationCustomizer.customize(verification);
            verification.build().verify(token);
        } catch (MissingClaimException ex) {
            return ValidatingResult.INVALID_CLAIM;
        } catch (IncorrectClaimException ex) {
            var claimName = ex.getClaimName();
            if (JwtConstants.PAYLOAD_NOT_BEFORE.equals(claimName) || JwtConstants.PAYLOAD_ISSUED_AT.equals(claimName)) {
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
