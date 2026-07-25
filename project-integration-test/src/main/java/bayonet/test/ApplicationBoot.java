package bayonet.test;

import com.github.yingzhuo.bayonet.webcli.util.RequestFactoryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class ApplicationBoot implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        var client = RestClient.builder()
                .requestFactory(RequestFactoryUtils.createInsecureSimple(null, null))
                .baseUrl("https://localhost:8443/actuator")
                .build();

        var json = client.get()
                .uri("/info")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .requiredBody(String.class);

        log.debug(json);
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBoot.class, args);
    }

}
