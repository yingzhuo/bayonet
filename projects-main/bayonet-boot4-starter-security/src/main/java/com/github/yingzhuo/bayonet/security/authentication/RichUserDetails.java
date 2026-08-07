package com.github.yingzhuo.bayonet.security.authentication;

import com.github.yingzhuo.bayonet.common.Identified;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 信息丰富的 {@link UserDetails}。
 * <p>在 Spring Security {@link UserDetails} 基础上扩展唯一标识（ID）、出生日期、
 * 邮箱等能力，继承 {@link Identified}，可通过 {@link #builder()} 构建实例。</p>
 *
 * @author 应卓
 * @see UserDetails
 * @see Identified
 * @see RichUserDetailsImpl
 * @since 4.1.1
 */
public sealed interface RichUserDetails extends Identified, UserDetails
        permits RichUserDetailsImpl {

    /**
     * 获取 Builder 实例。
     *
     * @return Builder 实例
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * 将 {@link #getId()} 解析为 {@code long}。
     *
     * @return ID 的 {@code long} 表示，无 ID 时返回 {@code null}
     */
    @Nullable
    default Long getIdAsLong() {
        var stringId = getId();
        return stringId == null ? null : Long.parseLong(stringId);
    }

    /**
     * 将 {@link #getId()} 解析为 {@code int}。
     *
     * @return ID 的 {@code int} 表示，无 ID 时返回 {@code null}
     */
    @Nullable
    default Integer getIdAsInteger() {
        var stringId = getId();
        return stringId == null ? null : Integer.parseInt(stringId);
    }

    /**
     * 获取出生日期。
     *
     * @return 出生日期，可为 {@code null}
     */
    @Nullable
    LocalDate getDob();

    /**
     * 获取电子邮箱。
     *
     * @return 电子邮箱，可为 {@code null}
     */
    @Nullable
    String getEmail();

    /**
     * 获取性别。
     * <p>性别类型不固定（可能是枚举或字符串），由调用方指定具体类型。</p>
     *
     * @param <T> 性别类型
     * @return 性别，可为 {@code null}
     */
    @Nullable
    <T> T getGender();

    /**
     * {@link RichUserDetails} 构建器。
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class Builder {

        private final List<GrantedAuthority> authorities = new ArrayList<>();
        private @Nullable String id;
        private @Nullable LocalDate dob;
        private @Nullable String email;
        private @Nullable Object gender;
        private @Nullable String username;
        private @Nullable String password;
        private boolean enabled = true;
        private boolean accountNonExpired = true;
        private boolean credentialsNonExpired = true;
        private boolean accountNonLocked = true;

        /**
         * 设置唯一标识。
         *
         * @param id 唯一标识，可为 {@code null}
         * @return 当前构建器
         */
        public Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        /**
         * 设置出生日期。
         *
         * @param dob 出生日期，可为 {@code null}
         * @return 当前构建器
         */
        public Builder dob(@Nullable LocalDate dob) {
            this.dob = dob;
            return this;
        }

        /**
         * 设置电子邮箱。
         *
         * @param email 电子邮箱，可为 {@code null}
         * @return 当前构建器
         */
        public Builder email(@Nullable String email) {
            this.email = email;
            return this;
        }

        /**
         * 设置性别。
         *
         * @param gender 性别（枚举或字符串等），可为 {@code null}
         * @return 当前构建器
         */
        public Builder gender(@Nullable Object gender) {
            this.gender = gender;
            return this;
        }

        /**
         * 设置用户名。
         * <p>对应 {@link UserDetails#getUsername()}，接口要求非空。</p>
         *
         * @param username 用户名（非空）
         * @return 当前构建器
         */
        public Builder username(String username) {
            Assert.hasText(username, "username must not be blank");
            this.username = username;
            return this;
        }

        /**
         * 设置密码。
         *
         * @param password 密码，可为 {@code null}
         * @return 当前构建器
         */
        public Builder password(@Nullable String password) {
            this.password = password;
            return this;
        }

        /**
         * 添加权限（字符串形式）。
         * <p>将权限字符串（如 {@code "ROLE_ADMIN"}）转换为权限并添加。</p>
         *
         * @param authorities 权限字符串
         * @return 当前构建器
         */
        public Builder authorities(String... authorities) {
            Assert.notNull(authorities, "authorities must not be null");
            Assert.noNullElements(authorities, "authorities must not contain null elements");
            var array = Arrays.stream(authorities)
                    .map(SimpleGrantedAuthority::new)
                    .toArray(GrantedAuthority[]::new);
            return authorities(array);
        }

        /**
         * 设置权限（逗号分隔字符串）。
         * <p>将逗号分隔的权限字符串（如 {@code "ROLE_A,ROLE_B"}）拆分为权限列表。</p>
         *
         * @param commaSplitAuthorities 逗号分隔的权限字符串（非空）
         * @return 当前构建器
         */
        public Builder authoritiesFromCommaSplitString(String commaSplitAuthorities) {
            Assert.notNull(commaSplitAuthorities, "commaSplitAuthorities must not be null");
            return authorities(AuthorityUtils.commaSeparatedStringToAuthorityList(commaSplitAuthorities));
        }

        /**
         * 添加权限。
         *
         * @param authorities 权限
         * @return 当前构建器
         */
        public Builder authorities(GrantedAuthority... authorities) {
            Assert.notNull(authorities, "authorities must not be null");
            this.authorities.addAll(List.of(authorities));
            return this;
        }

        /**
         * 添加权限集合。
         *
         * @param authorities 权限集合
         * @return 当前构建器
         */
        public Builder authorities(Collection<? extends GrantedAuthority> authorities) {
            Assert.notNull(authorities, "authorities must not be null");
            this.authorities.addAll(authorities);
            return this;
        }

        /**
         * 添加角色（自动加 {@code ROLE_} 前缀）。
         *
         * @param roles 角色名称
         * @return 当前构建器
         */
        public Builder roles(String... roles) {
            Assert.notNull(roles, "roles must not be null");
            for (var role : roles) {
                this.authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return this;
        }

        /**
         * 设置是否启用。
         *
         * @param enabled 是否启用
         * @return 当前构建器
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * 设置账号是否未过期。
         *
         * @param accountNonExpired 账号是否未过期
         * @return 当前构建器
         */
        public Builder accountNonExpired(boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        /**
         * 设置凭证是否未过期。
         *
         * @param credentialsNonExpired 凭证是否未过期
         * @return 当前构建器
         */
        public Builder credentialsNonExpired(boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
            return this;
        }

        /**
         * 设置账号是否未锁定。
         *
         * @param accountNonLocked 账号是否未锁定
         * @return 当前构建器
         */
        public Builder accountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        /**
         * 构建 {@link RichUserDetails} 实例。
         *
         * @return {@link RichUserDetails} 实例（非 {@code null}）
         * @throws IllegalArgumentException 若用户名为空
         */
        public RichUserDetails build() {
            Assert.hasText(username, "username must not be empty");
            return new RichUserDetailsImpl(
                    id, dob, email, gender, username, password, authorities,
                    enabled, accountNonExpired, credentialsNonExpired, accountNonLocked);
        }
    }
}
