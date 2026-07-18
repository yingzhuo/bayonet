package com.github.yingzhuo.bayonet.zxing.autoconfig;

import com.github.yingzhuo.bayonet.zxing.service.BarCodeGenerator;
import com.github.yingzhuo.bayonet.zxing.service.BarCodeGeneratorImpl;
import com.github.yingzhuo.bayonet.zxing.service.QRCodeGenerator;
import com.github.yingzhuo.bayonet.zxing.service.QRCodeGeneratorImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ZxingBeanAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BarCodeGenerator barCodeGenerator() {
        return new BarCodeGeneratorImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public QRCodeGenerator qrCodeGenerator() {
        return new QRCodeGeneratorImpl();
    }

}
