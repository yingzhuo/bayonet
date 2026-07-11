package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpringUtils {

    public static ApplicationContext getApplicationContext() {
        if (Hook.applicationContext == null) {
            throw new IllegalStateException("ApplicationContext has not been initialized");
        }
        return Hook.applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return getApplicationContext().getBean(beanName, clazz);
    }

    public static <T> ObjectProvider<T> getBeanProvider(Class<T> clazz) {
        return getApplicationContext().getBeanProvider(clazz, true);
    }

    public static <T> ObjectProvider<T> getBeanProvider(Class<T> clazz, boolean allowEagerInit) {
        return getApplicationContext().getBeanProvider(clazz, allowEagerInit);
    }

    private static class Hook implements ApplicationListener<ApplicationReadyEvent> {
        static @Nullable ApplicationContext applicationContext;

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            Hook.applicationContext = event.getApplicationContext();
        }
    }

}
