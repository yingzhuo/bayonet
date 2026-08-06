package com.github.yingzhuo.bayonet.security.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RichUserDetailsConverterTest {

    private final RichUserDetailsConverter converter = new RichUserDetailsConverter();

    @Test
    void should_parse_id_and_username() {
        var user = converter.convert("0001,admin");

        assertThat(user.getId()).isEqualTo("0001");
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    void should_parse_authorities_in_middle() {
        var user = converter.convert("0001,admin,ROLE_ADMIN,ROLE_USER");

        assertThat(user.getId()).isEqualTo("0001");
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN", "ROLE_USER");
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void should_parse_enabled_at_last() {
        var user = converter.convert("0001,admin,ROLE_ADMIN,false");

        assertThat(user.getId()).isEqualTo("0001");
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    void should_parse_yes_as_enabled() {
        var user = converter.convert("0001,admin,yes");

        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    void should_parse_last_token_as_authority_when_not_boolean() {
        var user = converter.convert("0001,admin,ROLE_ADMIN");

        assertThat(user.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void should_ignore_whitespaces() {
        var user = converter.convert(" 0001 , admin , ROLE_ADMIN , false ");

        assertThat(user.getId()).isEqualTo("0001");
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    void should_throw_when_blank_source() {
        assertThatThrownBy(() -> converter.convert("  "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> converter.convert(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_only_id() {
        assertThatThrownBy(() -> converter.convert("0001"))
                .isInstanceOf(IllegalStateException.class);
    }
}
