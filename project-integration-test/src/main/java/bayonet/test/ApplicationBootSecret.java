package bayonet.test;

import com.github.yingzhuo.bayonet.jwt.algorithm.SM2Algorithm;
import com.github.yingzhuo.bayonet.secret.KeyStoreType;
import com.github.yingzhuo.bayonet.secret.SecretBox;
import com.github.yingzhuo.bayonet.utility.AES;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.crypto.SecretKey;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationBootSecret {

    @Bean
    public SecretBox secretBox() {
        var sb = SecretBox.fromKeyStore()
                .resource(new ClassPathResource("secret/secret-box.bcfks"))
                .type(KeyStoreType.BCFKS)
                .storepass("123456")
                .build();
        log.info("secret box: {}", sb);
        return sb;
    }

    @Bean
    public SM2Algorithm sm2Algorithm(SecretBox secretBox) {
        var kp = secretBox.getKeyPair("SM2");
        return new SM2Algorithm(kp.getPublic(), kp.getPrivate());
    }

    @Bean
    public AES aes(SecretBox secretBox) {
        return new AES(secretBox.<SecretKey>getSecretKey("AES"));
    }

}
