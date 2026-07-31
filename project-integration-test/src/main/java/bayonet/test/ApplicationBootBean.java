package bayonet.test;

import com.github.yingzhuo.bayonet.secret.KeyStoreType;
import com.github.yingzhuo.bayonet.secret.SecretBox;
import com.github.yingzhuo.bayonet.utility.AES;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.crypto.SecretKey;

@Configuration
public class ApplicationBootBean {

    @Bean
    public SecretBox secretBox() {
        return SecretBox.builder()
                .resource(new ClassPathResource("config/secret-box.pfx"))
                .type(KeyStoreType.PKCS12)
                .storepass("123456")
                .alias("aes")
                .build();
    }

    @Bean
    public AES aesBean(SecretBox secretBox) {
        return new AES(secretBox.<SecretKey>getSecretKey("aes"));
    }

}
