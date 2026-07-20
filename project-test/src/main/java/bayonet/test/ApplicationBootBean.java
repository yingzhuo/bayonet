package bayonet.test;

import com.github.yingzhuo.bayonet.webcli.factory.JdkClientHttpRequestFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBootBean {

    @Bean("unsafeClientHttpRequest")
    public JdkClientHttpRequestFactoryBean jdkClientHttpRequestFactoryBean() {
        var factoryBean = new JdkClientHttpRequestFactoryBean();
        factoryBean.setTrustAllIfNoTrustStore(true);
        return factoryBean;
    }

}
