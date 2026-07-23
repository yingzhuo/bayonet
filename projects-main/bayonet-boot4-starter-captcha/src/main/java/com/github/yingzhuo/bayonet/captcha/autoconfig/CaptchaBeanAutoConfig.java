package com.github.yingzhuo.bayonet.captcha.autoconfig;

import com.github.yingzhuo.bayonet.captcha.factory.CaptchaGenerator;
import com.github.yingzhuo.bayonet.captcha.factory.SpecCaptchaGenerator;
import com.github.yingzhuo.bayonet.captcha.manager.CaptchaManager;
import com.github.yingzhuo.bayonet.captcha.manager.MapCaptchaManager;
import com.github.yingzhuo.bayonet.captcha.manager.SaveKeyGenerator;
import com.github.yingzhuo.bayonet.captcha.manager.UUIDSaveKeyGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CaptchaBeanAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public CaptchaGenerator<?> specCaptchaGenerator() {
        return new SpecCaptchaGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public SaveKeyGenerator uuidSaveKeyGenerator() {
        return UUIDSaveKeyGenerator.getInstance();
    }

    @Bean
    @ConditionalOnMissingBean
    public CaptchaManager mapCaptchaManager() {
        return new MapCaptchaManager();
    }

}
