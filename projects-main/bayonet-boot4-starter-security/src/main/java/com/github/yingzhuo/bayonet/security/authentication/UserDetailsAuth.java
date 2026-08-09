package com.github.yingzhuo.bayonet.security.authentication;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.core.style.ToStringCreator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.Collection;

/**
 * 基于 {@link UserDetails} 的认证令牌实现。
 * <p>同时实现 {@link Authentication} 和 {@link UserDetails} 接口，
 * 包装 Spring Security 的 {@link User} 对象，用作认证成功后的令牌。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public class UserDetailsAuth implements Authentication, UserDetails {

    @Serial
    private static final long serialVersionUID = 2367206193190539740L;

    @Getter
    private final UserDetails user;

    @Setter
    private @Nullable Object details;

    private boolean authenticated = true;

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

    @Override
    public @Nullable Object getDetails() {
        return this.details;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return user.getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        this.authenticated = isAuthenticated;
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
        var creator = new ToStringCreator(this);
        creator.append("Credentials", "[***]");
        creator.append("Authenticated", isAuthenticated());
        creator.append("Granted Authorities", getAuthorities());
        creator.append("Details", getDetails());
        return creator.toString();
    }

}
