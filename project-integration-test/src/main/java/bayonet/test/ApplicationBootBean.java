package bayonet.test;

import com.github.yingzhuo.bayonet.bean.ImportText;
import com.github.yingzhuo.bayonet.webcli.factory.JdkClientHttpRequestFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ImportText("classpath:1.txt")
public class ApplicationBootBean {

    @Bean("unsafeClientHttpRequestFactory")
    public JdkClientHttpRequestFactoryBean jdkClientHttpRequestFactoryBean() {
        var bean = new JdkClientHttpRequestFactoryBean();
        bean.setTrustAllIfNoTrustStore(true);
        return bean;
    }

}
