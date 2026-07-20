package bayonet.test;

import com.github.yingzhuo.bayonet.webcli.util.InterceptorFactories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@SpringBootApplication
public class ApplicationBoot implements ApplicationRunner {

    @Autowired
    @Qualifier("unsafeClientHttpRequestFactory")
    private ClientHttpRequestFactory clientHttpRequestFactory;

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBoot.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var cli = RestClient.builder()
                .requestFactory(clientHttpRequestFactory)
                .requestInterceptor(InterceptorFactories.createBearerAuthInterceptor("fuck_you"))
                .baseUrl("https://github.com")
                .build();

        var html = cli.get()
                .uri("/yingzhuo/bayonet")

                .retrieve()
                .requiredBody(String.class);

        log.info("\n{}\n", html);
    }
}
