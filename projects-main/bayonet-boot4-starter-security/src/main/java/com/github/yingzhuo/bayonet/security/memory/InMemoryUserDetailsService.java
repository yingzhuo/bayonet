package com.github.yingzhuo.bayonet.security.memory;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 基于内存的 {@link UserDetailsService} 实现。
 * <p>参考 {@code org.springframework.security.provisioning.InMemoryUserDetailsManager} 实现，
 * 退化纯 {@link UserDetailsService} 接口，不提供用户管理功能。</p>
 *
 * <p>用户信息通过构造器注入，支持以下方式：</p>
 * <ul>
 *   <li>{@link #InMemoryUserDetailsService(Collection)} — {@link Collection}&lt;{@link UserDetails}&gt;</li>
 *   <li>{@link #InMemoryUserDetailsService(UserDetails...)} — 变长参数</li>
 *   <li>{@link #InMemoryUserDetailsService(Properties)} — Properties 文件格式</li>
 * </ul>
 *
 * <p>Properties 文件格式：</p>
 * <pre>{@code
 * admin={noop}admin123,ROLE_ADMIN,ROLE_USER
 * user=pass456,ROLE_USER,enabled
 * guest=guest,ROLE_GUEST,disabled
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.1
 */
public class InMemoryUserDetailsService implements UserDetailsService {

    private final Map<String, UserDetails> users = new HashMap<>();

    /**
     * 构造器
     *
     * @param users 用户集合
     */
    public InMemoryUserDetailsService(Collection<UserDetails> users) {
        Assert.notNull(users, "users must not be null");
        for (var user : users) {
            put(user);
        }
    }

    /**
     * 构造器
     *
     * @param users 用户变长参数
     */
    public InMemoryUserDetailsService(UserDetails... users) {
        Assert.notNull(users, "users must not be null");
        for (var user : users) {
            put(user);
        }
    }

    /**
     * 构造器
     *
     * @param users Properties 配置
     */
    public InMemoryUserDetailsService(Properties users) {
        Assert.notEmpty(users, "users must not be empty");
        var names = users.propertyNames();
        var editor = new UserAttributeEditor();
        while (names.hasMoreElements()) {
            var name = (String) names.nextElement();
            editor.setAsText(users.getProperty(name));
            var attr = (UserAttribute) editor.getValue();
            Assert.notNull(attr, () -> "The entry with username '" + name + "' could not be converted to an UserDetails");
            put(createUserDetails(name, attr));
        }
    }

    private User createUserDetails(String name, UserAttribute attr) {
        return new User(name, attr.getPassword(), attr.isEnabled(), true, true, true, attr.getAuthorities());
    }

    /**
     * 设置用户集合。
     * <p>会清空已有用户。</p>
     *
     * @param users 用户集合
     */
    public void setUsers(Collection<UserDetails> users) {
        Assert.notNull(users, "users must not be null");
        this.users.clear();
        for (var user : users) {
            put(user);
        }
    }

    private void put(UserDetails user) {
        this.users.put(user.getUsername().toLowerCase(Locale.ROOT), user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var key = username.toLowerCase(Locale.ROOT);
        var user = this.users.get(key);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}
