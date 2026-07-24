package bayonet.test;

import bayonet.test.hello.GithubAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class ApplicationBoot implements ApplicationRunner {

    private final GithubAccessor githubAccessor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("{}", githubAccessor.getHtmlAboutMe());
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBoot.class, args);
    }

}
