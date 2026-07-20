package bayonet.test;

import com.github.yingzhuo.bayonet.webcli.annotation.ImportUnsafeClientHttpRequestFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@ImportUnsafeClientHttpRequestFactory
public class ApplicationBootBean {

//    @Bean("unsafeClientHttpRequest")
//    public JdkClientHttpRequestFactoryBean jdkClientHttpRequestFactoryBean() {
//        var factoryBean = new JdkClientHttpRequestFactoryBean();
//        factoryBean.setTrustAllIfNoTrustStore(true);
//        return factoryBean;
//    }

}
