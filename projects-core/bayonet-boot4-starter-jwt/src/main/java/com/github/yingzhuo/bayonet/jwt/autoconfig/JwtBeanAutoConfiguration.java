package com.github.yingzhuo.bayonet.jwt.autoconfig;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.jwt.creator.DefaultJwtCreator;
import com.github.yingzhuo.bayonet.jwt.creator.JwtCreator;
import com.github.yingzhuo.bayonet.jwt.validator.DefaultJwtValidator;
import com.github.yingzhuo.bayonet.jwt.validator.JwtValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class JwtBeanAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Algorithm.class)
    public JwtCreator jwtCreator(Algorithm algorithm) {
        return new DefaultJwtCreator(algorithm);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Algorithm.class)
    public JwtValidator jwtValidator(Algorithm algorithm) {
        return new DefaultJwtValidator(algorithm);
    }

}
