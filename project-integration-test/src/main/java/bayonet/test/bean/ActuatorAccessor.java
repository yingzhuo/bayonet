package bayonet.test.bean;

import com.github.yingzhuo.bayonet.webcli.factory.Apache5RequestFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class ActuatorAccessor {

    private final RestClient restClient;

    public ActuatorAccessor(SslBundles sslBundles) {
//        var sslBundle = sslBundles.getBundle("client");
        var factory = Apache5RequestFactoryUtils.createInsecure();

        this.restClient = RestClient.builder()
                .baseUrl("https://localhost:8443")
                .requestFactory(factory)
                .defaultHeaders(headers -> {
                    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .build();
    }

    public void access() {
        var html = restClient.get()
                .uri("/actuator/info")
                .retrieve()
                .body(String.class);

        log.debug(html);
    }

}
