package bayonet.test;

import com.github.yingzhuo.bayonet.security.configurer.AdditionalDebugAuthFilter;
import com.github.yingzhuo.bayonet.security.configurer.AdditionalSecurityFilter;
import com.github.yingzhuo.bayonet.security.filter.DebugTokenBasedAuthFilter;
import com.github.yingzhuo.bayonet.security.filter.LoggingFilter;
import com.github.yingzhuo.bayonet.security.filter.TokenBasedAuthFilter;
import com.github.yingzhuo.bayonet.security.memory.InMemoryUserDetailsService;
import com.github.yingzhuo.bayonet.security.token.HttpHeaderTokenResolver;
import com.github.yingzhuo.bayonet.security.token.TokenConverter;
import com.github.yingzhuo.bayonet.security.token.TokenResolver;
import com.github.yingzhuo.bayonet.utility.PropertiesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import java.util.Properties;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@AdditionalDebugAuthFilter
@AdditionalSecurityFilter(value = LoggingFilter.class, positionFilterType = DisableEncodeUrlFilter.class)
@AdditionalSecurityFilter(value = TokenBasedAuthFilter.class)
public class ApplicationBootSecurity {

    @Bean
    public Properties fixedUsersProperties() {
        return PropertiesUtils.loadProperties("classpath:debugging-users.properties");
    }

    @Bean
    public UserDetailsService userDetails(@Qualifier("fixedUsersProperties") Properties users) {
        return new InMemoryUserDetailsService(users);
    }

    @Bean
    public DebugTokenBasedAuthFilter debuggingTokenBasedAuthFilter(@Qualifier("fixedUsersProperties") Properties users, TokenResolver tokenResolver) {
        var filter = new DebugTokenBasedAuthFilter(users);
        filter.setTokenResolver(tokenResolver);
        return filter;
    }

    @Bean
    public TokenBasedAuthFilter tokenBasedAuthFilter(TokenResolver tokenResolver, TokenConverter tokenConverter) {
        var filter = new TokenBasedAuthFilter();
        filter.setTokenResolver(tokenResolver);
        filter.setTokenConverter(tokenConverter);
        return filter;
    }

    @Bean
    public TokenResolver tokenResolver() {
        return new HttpHeaderTokenResolver("X-Token", "", 0);
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
                                        "/actuator/beans", "/actuator/env", "/actuator/prometheus").permitAll()
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
}
