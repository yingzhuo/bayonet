package com.github.yingzhuo.bayonet.utility.net;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

/**
 * {@link SSLContext} 和 {@link SSLParameters} 的组合记录。
 *
 * <p>由 {@link SSLFactories} 返回，将 SSL 上下文及其参数打包为一个不可变对象。</p>
 *
 * @param context    SSL 上下文（非 {@code null}）
 * @param parameters SSL 参数（非 {@code null}）
 * @author 应卓
 * @see SSLFactories
 * @since 4.1.1
 */
@Deprecated(forRemoval = true)
public record ContextAndParameters(
        SSLContext context,
        SSLParameters parameters
) {
}
