package com.github.yingzhuo.bayonet.autoconfig;

import com.github.yingzhuo.bayonet.secret.StoreTypeConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ConverterAutoConfiguration {

    @Bean
    public StoreTypeConverter storeTypeConverter() {
        return new StoreTypeConverter();
    }

}
