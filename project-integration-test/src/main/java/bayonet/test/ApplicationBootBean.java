package bayonet.test;

import com.github.yingzhuo.bayonet.secret.KeyStoreType;
import com.github.yingzhuo.bayonet.secret.SecretBox;
import com.github.yingzhuo.bayonet.utility.AES;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.crypto.SecretKey;

@Configuration
public class ApplicationBootBean {

    @Bean
    public SecretKey aesSecretKey() {
        return SecretBox.builder()
                .resource(new ClassPathResource("config/secretdb.pfx"))
                .type(KeyStoreType.PKCS12)
                .storepass("123456")
                .alias("aes")
                .build()
                .getSecretKey("aes");
    }

    @Bean
    public AES aesBean(@Qualifier("aesSecretKey") SecretKey aesSecretKey) {
        return new AES(aesSecretKey);
    }

}
