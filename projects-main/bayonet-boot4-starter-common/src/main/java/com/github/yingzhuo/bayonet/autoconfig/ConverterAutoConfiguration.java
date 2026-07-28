package com.github.yingzhuo.bayonet.autoconfig;

import com.github.yingzhuo.bayonet.converter.KeyStoreTypeConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ConverterAutoConfiguration {

    @Bean
    public KeyStoreTypeConverter keyStoreTypeConverter() {
        return new KeyStoreTypeConverter();
    }

}
