package bayonet.test;

import com.github.yingzhuo.bayonet.security.configurer.AdditionalDebugAuthFilter;
import com.github.yingzhuo.bayonet.security.filter.DebugTokenBasedAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@AdditionalDebugAuthFilter
public class ApplicationBootSecurity {

    @Bean
    public DebugTokenBasedAuthFilter debuggingTokenBasedAuthFilter() {
        return new DebugTokenBasedAuthFilter("classpath:debugging-users.properties");
    }

}
