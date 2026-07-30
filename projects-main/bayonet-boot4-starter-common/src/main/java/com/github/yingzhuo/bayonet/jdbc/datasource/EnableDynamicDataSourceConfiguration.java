package com.github.yingzhuo.bayonet.jdbc.datasource;

import org.springframework.context.annotation.Bean;

class EnableDynamicDataSourceConfiguration {

    @Bean
    public DataSourceSwitchingAspect dataSourceSwitchingAspect() {
        return new DataSourceSwitchingAspect();
    }

}
