package com.github.yingzhuo.bayonet.security.password;

import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * {@link NamedPasswordEncoder} 的委托实现。
 * <p>将指定的名称与任意 {@link PasswordEncoder} 组合，使其具备 {@link NamedPasswordEncoder} 能力。
 * 常用于将外部提供的普通编码器包装为带名字的编码器，供 SPI 加载或工厂路由使用。</p>
 *
 * <pre>{@code
 * var encoder = new DelegatingNamedPasswordEncoder("my-encoder", new MyPasswordEncoder());
 * String name = encoder.getName();       // "my-encoder"
 * String encoded = encoder.encode("pass");
 * }</pre>
 *
 * @author 应卓
 * @see NamedPasswordEncoder
 * @see PasswordEncoderFactories
 * @since 4.1.1
 */
public class DelegatingNamedPasswordEncoder implements NamedPasswordEncoder {

    private final String name;
    private final PasswordEncoder delegate;

    /**
     * 构造器。
     *
     * @param name     编码器逻辑名称（非空）
     * @param delegate 被委托的 {@link PasswordEncoder}（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code name} 为空或 {@code delegate} 为 {@code null}
     */
    public DelegatingNamedPasswordEncoder(String name, PasswordEncoder delegate) {
        Assert.hasText(name, "name must not be empty");
        Assert.notNull(delegate, "delegate encoder must not be null");
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public @Nullable String encode(@Nullable CharSequence rawPassword) {
        return this.delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword) {
        return this.delegate.matches(rawPassword, encodedPassword);
    }
}
