package com.github.yingzhuo.bayonet.security.password;

import com.github.yingzhuo.bayonet.utility.spi.SpringFactoriesUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 带命名的 {@link PasswordEncoder} 接口。
 *
 * @author 应卓
 * @see SpringFactoriesUtils
 * @see com.github.yingzhuo.bayonet.security.password.PasswordEncoderFactories
 * @since 4.1.1
 */
public interface NamedPasswordEncoder extends PasswordEncoder {

    /**
     * 返回编码器逻辑名称
     *
     * @return 编码器逻辑名称
     */
    String getName();

}
