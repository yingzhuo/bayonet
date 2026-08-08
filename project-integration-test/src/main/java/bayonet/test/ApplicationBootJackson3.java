package bayonet.test;

import bayonet.test.tool.RuntimeHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationBootJackson3 {

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer(Environment environment) {
        return builder -> builder.configure(INDENT_OUTPUT, !RuntimeHelper.isProdProfileActive(environment));
    }

}
