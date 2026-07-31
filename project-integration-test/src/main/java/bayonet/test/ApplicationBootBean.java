package bayonet.test;

import com.github.yingzhuo.bayonet.jwt.algorithm.SM2Algorithm;
import com.github.yingzhuo.bayonet.secret.KeyStoreType;
import com.github.yingzhuo.bayonet.secret.SecretBox;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class ApplicationBootBean {

    @Bean
    public SecretBox secretBox() {
        return SecretBox.builder()
                .resource(new ClassPathResource("config/secret-box.bcfks"))
                .type(KeyStoreType.BCFKS)
                .storepass("123456")
                .build();
    }

    @Bean
    public SM2Algorithm hello(SecretBox secretBox) {
        var kp = secretBox.getKeyPair("SM2");
        return new SM2Algorithm(kp.getPublic(), kp.getPrivate());
    }

}
