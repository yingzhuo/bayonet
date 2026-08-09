package com.github.yingzhuo.bayonet.security.authentication;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * {@link RichUserDetails} 的不可变默认实现。
 *
 * @author 应卓
 * @see RichUserDetails
 * @since 4.1.1
 */
final class RichUserDetailsImpl implements RichUserDetails {

    private final @Nullable String id;
    private final @Nullable LocalDate dob;
    private final @Nullable String email;
    private final @Nullable Object gender;
    private final String username;
    private final @Nullable String password;
    private final List<GrantedAuthority> authorities;
    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean credentialsNonExpired;
    private final boolean accountNonLocked;

    RichUserDetailsImpl(
            @Nullable String id,
            @Nullable LocalDate dob,
            @Nullable String email,
            @Nullable Object gender,
            String username,
            @Nullable String password,
            Collection<? extends GrantedAuthority> authorities,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked) {
        this.id = id;
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.authorities = List.copyOf(authorities);
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public @Nullable String getId() {
        return id;
    }

    @Override
    public @Nullable LocalDate getDob() {
        return dob;
    }

    @Override
    public @Nullable String getEmail() {
        return email;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getGender() {
        return (T) gender;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "RichUserDetailsImpl{username='" + username + "', enabled=" + enabled + '}';
    }
}
