package com.github.yingzhuo.bayonet.security.autoconfig;

import com.github.yingzhuo.bayonet.security.password.PasswordEncoderFactories;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@AutoConfiguration
public class SecurityBeanAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createMinimal();
    }

}
