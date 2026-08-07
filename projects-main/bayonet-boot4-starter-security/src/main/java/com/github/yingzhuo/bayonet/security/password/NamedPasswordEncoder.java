package com.github.yingzhuo.bayonet.security.password;

import com.github.yingzhuo.bayonet.common.Named;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 带命名的 {@link PasswordEncoder} 接口。
 *
 * @author 应卓
 * @see DelegatingNamedPasswordEncoder
 * @see PasswordEncoderFactories
 * @since 4.1.1
 */
public interface NamedPasswordEncoder extends Named, PasswordEncoder {

    /**
     * 返回编码器逻辑名称 (不允许为空值)
     *
     * @return 编码器逻辑名称
     */
    @Override
    String getName();

}
