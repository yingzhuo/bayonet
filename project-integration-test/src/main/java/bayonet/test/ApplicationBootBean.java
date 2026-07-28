package bayonet.test;

import com.github.yingzhuo.bayonet.secret.SecretKeyFactories;
import com.github.yingzhuo.bayonet.utility.AES;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class ApplicationBootBean {

    @Bean
    public SecretKey aesSecretKey() {
        return SecretKeyFactories.loadFromKeyStore("classpath:config/secretdb.pfx", null, "123456", "aes", null);
    }

    @Bean
    public AES aesBean(@Qualifier("aesSecretKey") SecretKey aesSecretKey) {
        return new AES(aesSecretKey);
    }

}
