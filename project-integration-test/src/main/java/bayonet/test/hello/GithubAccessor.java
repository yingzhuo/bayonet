package bayonet.test.hello;

import com.github.yingzhuo.bayonet.webcli.util.RequestFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class GithubAccessor {

    private final RestClient restClient;

    public GithubAccessor() {
        this.restClient = RestClient.builder()
                .requestFactory(RequestFactoryUtils.createInsecureJdk(null, null))
                .baseUrl("https://github.com")
                .build();
    }

    public String getHtmlAboutMe() {
        return restClient.get()
                .uri("/yingzhuo")
                .retrieve()
                .requiredBody(String.class);
    }
}
