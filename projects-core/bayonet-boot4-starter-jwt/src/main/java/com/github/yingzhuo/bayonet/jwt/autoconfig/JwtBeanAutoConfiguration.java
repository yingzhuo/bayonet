package com.github.yingzhuo.bayonet.jwt.autoconfig;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.jwt.service.*;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class JwtBeanAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Algorithm.class)
    public BlacklistChecker blacklistChecker() {
        return (rawToken, decodedToken) -> false;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Algorithm.class)
    public JwtCreator jwtCreator(Algorithm algorithm, @Autowired(required = false) @Nullable JtiGenerator jtiGenerator) {
        return new DefaultJwtCreator(algorithm, jtiGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Algorithm.class)
    public JwtValidator jwtValidator(Algorithm algorithm, @Autowired(required = false) @Nullable VerificationCustomizer verificationCustomizer, @Autowired(required = false) @Nullable BlacklistChecker blacklistChecker) {
        return new DefaultJwtValidator(algorithm, verificationCustomizer, blacklistChecker);
    }

}
