package bayonet.test.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class ActuatorAccessor {

    private final RestClient restClient;

    public ActuatorAccessor(@Qualifier("insecureClientHttpRequest") ClientHttpRequestFactory clientHttpRequestFactory) {
        this.restClient = RestClient.builder()
                .baseUrl("https://localhost:8443")
                .requestFactory(clientHttpRequestFactory)
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
