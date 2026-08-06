package com.github.yingzhuo.bayonet.secret;

import org.springframework.core.convert.converter.Converter;

/**
 * {@link CharSequence} 到 {@link StoreType} 的 Spring 转换器。
 * <p>实现 {@link Converter} 接口，用于 Spring 类型转换体系中将字符串配置值自动转换为 {@link StoreType} 枚举。</p>
 *
 * @author 应卓
 * @see StoreType#toKeyStore(String)
 * @since 4.1.1
 */
public class StoreTypeConverter implements Converter<CharSequence, StoreType> {

    @Override
    public StoreType convert(CharSequence source) {
        return StoreType.toKeyStore(source.toString());
    }

}
