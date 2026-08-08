package bayonet.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationBootJackson3 {

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer(Environment environment) {
        var prod = environment.acceptsProfiles(Profiles.of("prod | production"));
        return builder -> builder.configure(INDENT_OUTPUT, !prod);
    }

}
