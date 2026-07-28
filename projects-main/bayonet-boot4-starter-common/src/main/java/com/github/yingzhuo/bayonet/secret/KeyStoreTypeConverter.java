package com.github.yingzhuo.bayonet.secret;

import org.springframework.core.convert.converter.Converter;

/**
 * {@link CharSequence} 到 {@link KeyStoreType} 的 Spring 转换器。
 * <p>实现 {@link Converter} 接口，用于 Spring 类型转换体系中将字符串配置值自动转换为 {@link KeyStoreType} 枚举。</p>
 *
 * @author 应卓
 * @see Converter
 * @see KeyStoreType
 * @since 4.1.1
 */
public class KeyStoreTypeConverter implements Converter<CharSequence, KeyStoreType> {

    @Override
    public KeyStoreType convert(CharSequence source) {
        return KeyStoreType.toKeyStore(source.toString());
    }

}
