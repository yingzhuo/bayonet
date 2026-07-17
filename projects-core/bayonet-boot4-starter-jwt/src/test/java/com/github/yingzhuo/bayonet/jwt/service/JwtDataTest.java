package com.github.yingzhuo.bayonet.jwt.service;

import com.github.yingzhuo.bayonet.jwt.service.JwtData;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtDataTest {

    // ============== 构造器 ==============

    @Test
    void should_set_defaultType_when_created() {
        var data = new JwtData();
        assertThat(data.getHeaderMap()).containsEntry("typ", "JWT");
    }

    @Test
    void should_create_via_newInstance() {
        assertThat(JwtData.newInstance()).isNotNull();
    }

    // ============== getHeaderMap / getPayloadMap 不可变 ==============

    @Test
    void should_return_unmodifiableHeaderMap() {
        assertThatThrownBy(() -> new JwtData().getHeaderMap().put("x", "y"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void should_return_unmodifiablePayloadMap() {
        assertThatThrownBy(() -> new JwtData().getPayloadMap().put("x", "y"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // ============== 自定义 header / payload ==============

    @Test
    void should_addHeader() {
        var data = new JwtData().addHeader("custom", "value");
        assertThat(data.getHeaderMap()).containsEntry("custom", "value");
    }

    @Test
    void should_throw_when_addHeader_nameIsBlank() {
        assertThatThrownBy(() -> new JwtData().addHeader("", "v"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_addHeader_valueIsNull() {
        assertThatThrownBy(() -> new JwtData().addHeader("k", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_addPayload() {
        var data = new JwtData().addPayload("custom", "value");
        assertThat(data.getPayloadMap()).containsEntry("custom", "value");
    }

    @Test
    void should_throw_when_addPayload_nameIsBlank() {
        assertThatThrownBy(() -> new JwtData().addPayload("", "v"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_addPayload_valueIsNull() {
        assertThatThrownBy(() -> new JwtData().addPayload("k", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== header 方法 ==============

    @Test
    void should_setHeaderType() {
        var data = new JwtData().addHeaderType("JOSE");
        assertThat(data.getHeaderMap()).containsEntry("typ", "JOSE");
    }

    @Test
    void should_setHeaderKeyId() {
        var data = new JwtData().addHeaderKeyId("key-1");
        assertThat(data.getHeaderMap()).containsEntry("kid", "key-1");
    }

    @Test
    void should_setHeaderKeyId_viaSupplier() {
        var data = new JwtData().addHeaderKeyId(() -> "supplied-key");
        assertThat(data.getHeaderMap()).containsEntry("kid", "supplied-key");
    }

    @Test
    void should_setHeaderContentType() {
        var data = new JwtData().addHeaderContentType("JWT");
        assertThat(data.getHeaderMap()).containsEntry("cty", "JWT");
    }

    // ============== payload 标准声明 ==============

    @Test
    void should_setPayloadIssuer() {
        var data = new JwtData().addPayloadIssuer("my-issuer");
        assertThat(data.getPayloadMap()).containsEntry("iss", "my-issuer");
    }

    @Test
    void should_setPayloadSubject() {
        var data = new JwtData().addPayloadSubject("user-001");
        assertThat(data.getPayloadMap()).containsEntry("sub", "user-001");
    }

    @Test
    void should_setPayloadAudience() {
        var data = new JwtData().addPayloadAudience("svc-a", "svc-b");
        assertThat(data.getPayloadMap()).containsKey("aud");
    }

    @Test
    void should_throw_when_audienceIsEmpty() {
        assertThatThrownBy(() -> new JwtData().addPayloadAudience())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_setPayloadExpiresAt() {
        var data = new JwtData().addPayloadExpiresAt(LocalDateTime.now().plusHours(1));
        assertThat(data.getPayloadMap()).containsKey("exp");
    }

    @Test
    void should_setPayloadExpiresAtFuture() {
        var data = new JwtData().addPayloadExpiresAtFuture(Duration.ofHours(2));
        assertThat(data.getPayloadMap()).containsKey("exp");
    }

    @Test
    void should_setPayloadNotBefore() {
        var data = new JwtData().addPayloadNotBefore(LocalDateTime.now());
        assertThat(data.getPayloadMap()).containsKey("nbf");
    }

    @Test
    void should_setPayloadNotBeforeAtFuture() {
        var data = new JwtData().addPayloadNotBeforeAtFuture(Duration.ofMinutes(5));
        assertThat(data.getPayloadMap()).containsKey("nbf");
    }

    @Test
    void should_setPayloadIssuedAt() {
        var data = new JwtData().addPayloadIssuedAt(LocalDateTime.now());
        assertThat(data.getPayloadMap()).containsKey("iat");
    }

    @Test
    void should_setPayloadIssuedAtNow() {
        var data = new JwtData().addPayloadIssuedAtNow();
        assertThat(data.getPayloadMap()).containsKey("iat");
    }

    @Test
    void should_setPayloadJwtId() {
        var data = new JwtData().addPayloadJwtId("jti-001");
        assertThat(data.getPayloadMap()).containsEntry("jti", "jti-001");
    }

    @Test
    void should_setPayloadJwtId_viaSupplier() {
        var data = new JwtData().addPayloadJwtId(() -> "generated-jti");
        assertThat(data.getPayloadMap()).containsEntry("jti", "generated-jti");
    }

    // ============== 链式调用 ==============

    @Test
    void should_support_chainedCalls() {
        var data = new JwtData()
                .addPayloadSubject("user")
                .addPayloadIssuer("bayonet")
                .addPayloadExpiresAtFuture(Duration.ofHours(1));

        assertThat(data.getPayloadMap())
                .containsEntry("sub", "user")
                .containsEntry("iss", "bayonet")
                .containsKey("exp");
    }

}
