package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventPublisherUtilsTest {

    @Test
    void should_throw_when_eventIsNull() {
        assertThatThrownBy(() -> EventPublisherUtils.publishEvent(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_publishPojoEvent_when_springContextRunning() {
        var ctx = new SpringApplicationBuilder(Config.class)
                .web(WebApplicationType.NONE)
                .run();
        try {
            EventPublisherUtils.publishEvent(new MyEvent("test"));
            assertThat(Config.LISTENER_INVOKED).isTrue();
        } finally {
            ctx.close();
        }
    }

    // ------

    record MyEvent(String value) {
    }

    @Configuration
    static class Config {

        static final AtomicBoolean LISTENER_INVOKED = new AtomicBoolean(false);

        @Bean
        Object listener() {
            return new Object() {
                @EventListener
                void onEvent(MyEvent event) {
                    LISTENER_INVOKED.set(true);
                }
            };
        }
    }

}
