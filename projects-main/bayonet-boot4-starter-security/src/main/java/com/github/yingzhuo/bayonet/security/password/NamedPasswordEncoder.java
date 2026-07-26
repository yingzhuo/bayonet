package com.github.yingzhuo.bayonet.security.password;

import com.github.yingzhuo.bayonet.function.Named;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 带命名的 {@link PasswordEncoder} 接口。
 *
 * <p>组合 {@link Named} 和 {@link PasswordEncoder}，使编码器实例关联一个逻辑名称。
 * 实现类通过 {@link Named#getName()} 返回编码器 ID（如 {@code "SM3"}），
 * 配合 {@link com.github.yingzhuo.bayonet.utility.SpringFactoriesUtils}
 * 可实现基于 {@code spring.factories} 的自动注册。</p>
 *
 * @author 应卓
 * @see com.github.yingzhuo.bayonet.utility.SpringFactoriesUtils
 * @see com.github.yingzhuo.bayonet.security.password.PasswordEncoderFactories
 * @since 4.1.1
 */
public interface NamedPasswordEncoder extends Named, PasswordEncoder {
}
