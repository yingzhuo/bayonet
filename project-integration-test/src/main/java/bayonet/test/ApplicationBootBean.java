package bayonet.test;

import com.github.yingzhuo.bayonet.webcli.factory.InsecureHttpComponentsClientHttpRequestFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBootBean {

    @Bean("insecureClientHttpRequest")
    public InsecureHttpComponentsClientHttpRequestFactoryBean insecureHttpComponentsClientHttpRequestFactoryBean() {
        return new InsecureHttpComponentsClientHttpRequestFactoryBean();
    }

}
