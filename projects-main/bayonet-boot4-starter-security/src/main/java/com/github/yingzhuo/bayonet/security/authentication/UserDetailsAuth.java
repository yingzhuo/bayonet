package com.github.yingzhuo.bayonet.security.authentication;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 基于 {@link UserDetails} 的认证令牌实现。
 * <p>同时实现 {@link Authentication} 和 {@link UserDetails} 接口，
 * 包装 Spring Security 的 {@link User} 对象，用作认证成功后的令牌。</p>
 *
 * <p>不可变对象，{@link #setAuthenticated(boolean)} 始终抛出
 * {@link UnsupportedOperationException}。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public class UserDetailsAuth implements Authentication, UserDetails {

    @Serial
    private static final long serialVersionUID = 2367206193190539740L;

    @Getter
    private final UserDetails user;

    /**
     * 构造器
     *
     * @param user {@link UserDetails} 对象，不可为 {@code null}
     */
    public UserDetailsAuth(UserDetails user) {
        Assert.notNull(user, "user must not be null");
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public @Nullable Object getCredentials() {
        return user.getPassword();
    }

    /**
     * 无额外详情，始终返回 {@code null}。
     *
     * @return {@code null}
     */
    @Override
    public @Nullable Object getDetails() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return user.getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        return user.isEnabled()
                && user.isAccountNonExpired()
                && user.isCredentialsNonExpired()
                && user.isAccountNonLocked();
    }

    /**
     * 保持不可变性，始终抛出 {@link UnsupportedOperationException}。
     *
     * @param isAuthenticated 忽略
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException("setAuthenticated is unsupported");
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String toString() {
        var authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
        return "UserDetailsAuth{username='%s', authorities=[%s]}".formatted(getUsername(), authorities);
    }
}
