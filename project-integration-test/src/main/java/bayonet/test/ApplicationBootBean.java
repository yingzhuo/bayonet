package bayonet.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.http.client.HttpRedirects;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ApplicationBootBean {

    @Autowired
    public ApplicationBootBean(SslBundles sslBundles) {
        var sslBundle = sslBundles.getBundle("hello");
        HttpClientSettings settings =
                HttpClientSettings.ofSslBundle(sslBundles.getBundle("mybundle"))
                        .withConnectTimeout(Duration.ofSeconds(10))
                        .withReadTimeout(Duration.ofMinutes(2))
                        .withRedirects(HttpRedirects.DONT_FOLLOW)
                ;


        var requestFactory = ClientHttpRequestFactoryBuilder.detect()


                .build(settings);
    }

}
