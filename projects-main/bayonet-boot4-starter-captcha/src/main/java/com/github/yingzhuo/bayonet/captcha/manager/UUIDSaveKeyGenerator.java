package com.github.yingzhuo.bayonet.captcha.manager;

import com.github.yingzhuo.bayonet.utility.UUIDUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 基于 {@link UUID#randomUUID()} 的 {@link SaveKeyGenerator} 实现。
 *
 * <p>使用随机 UUID 作为验证码存储 key，去掉连字符后返回 32 位十六进制字符串。
 * 线程安全，无需额外配置。</p>
 *
 * @author 应卓
 * @see SaveKeyGenerator
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated(forRemoval = true)
public final class UUIDSaveKeyGenerator implements SaveKeyGenerator {

    /**
     * 获取 {@link UUIDSaveKeyGenerator} 单例实例。
     *
     * @return 单例实例
     */
    public static UUIDSaveKeyGenerator getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public String generate() {
        return UUIDUtils.versionFourShort();
    }

    // ------

    private static class LazyHolder {
        private static final UUIDSaveKeyGenerator INSTANCE = new UUIDSaveKeyGenerator();
    }
}
