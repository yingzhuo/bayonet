package bayonet.test;

import bayonet.test.bean.ActuatorAccessor;
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

    private final ActuatorAccessor actuatorAccessor;

    @Override
    public void run(ApplicationArguments args) {
        actuatorAccessor.access();
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBoot.class, args);
    }

}
