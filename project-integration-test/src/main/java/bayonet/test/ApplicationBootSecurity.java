package bayonet.test;

import com.github.yingzhuo.bayonet.security.authentication.RichUserDetails;
import com.github.yingzhuo.bayonet.security.configurer.AdditionalDebugAuthFilter;
import com.github.yingzhuo.bayonet.security.configurer.AdditionalSecurityFilter;
import com.github.yingzhuo.bayonet.security.filter.DebugTokenBasedAuthFilter;
import com.github.yingzhuo.bayonet.security.filter.LoggingFilter;
import com.github.yingzhuo.bayonet.security.filter.TokenBasedAuthFilter;
import com.github.yingzhuo.bayonet.security.token.HttpHeaderTokenResolver;
import com.github.yingzhuo.bayonet.security.token.TokenConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@AdditionalDebugAuthFilter(skipIfAnyProfileActivated = "prod")
@AdditionalSecurityFilter(value = LoggingFilter.class, positionFilterType = DisableEncodeUrlFilter.class)
@AdditionalSecurityFilter(value = TokenBasedAuthFilter.class)
public class ApplicationBootSecurity {

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(createInMemoryUsers());
    }

    @Bean
    public DebugTokenBasedAuthFilter debugTokenBasedAuthFilter() {
        var filter = new DebugTokenBasedAuthFilter(createInMemoryUsers());
        filter.setTokenResolver(new HttpHeaderTokenResolver("X-Token"));
        return filter;
    }

    @Bean
    public TokenBasedAuthFilter tokenBasedAuthFilter(TokenConverter tokenConverter) {
        var filter = new TokenBasedAuthFilter();
        filter.setTokenResolver(new HttpHeaderTokenResolver("X-Token"));
        filter.setTokenConverter(tokenConverter);
        return filter;
    }

    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    @Bean
    public HttpFirewall httpFirewall() {
        var bean = new StrictHttpFirewall();
        bean.setAllowSemicolon(true);
        return bean;
    }

    @Bean
    public SecurityFilterChain securityFilterChainDefault(HttpSecurity http) {
        return http
                .securityMatcher("/**")
                .anonymous(Customizer.withDefaults())
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .jee(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .passwordManagement(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .headers(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(c ->
                        c.requestMatchers("/error").permitAll()
                                .requestMatchers(GET, "/actuator", "/actuator/info", "/actuator/health",
                                        "/actuator/beans", "/actuator/env", "/actuator/securityproviders").permitAll()
                                .requestMatchers("/actuator/shutdown", "/actuator/restart").denyAll()
                                .requestMatchers("/user/login").permitAll()
                                .requestMatchers("/user/test-jwt").authenticated()
                                .anyRequest().denyAll()
                )
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return customizer -> customizer.debug(false);
    }

    // ------

    private List<UserDetails> createInMemoryUsers() {
        return List.of(
                RichUserDetails.builder()
                        .username("admin")
                        .roles("ADMIN", "USER")
                        .build()
        );
    }

}
