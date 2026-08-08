package bayonet.test;

import bayonet.test.security.ExceptionHandlers;
import bayonet.test.tool.RuntimeHelper;
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
import org.springframework.core.env.Environment;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import static bayonet.test.security.PredefinedUsers.createDefaults;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;

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
        return new InMemoryUserDetailsManager(createDefaults());
    }

    @Bean
    public DebugTokenBasedAuthFilter debugTokenBasedAuthFilter() {
        var filter = new DebugTokenBasedAuthFilter(createDefaults());
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
    public RoleHierarchy roleHierarchy() {
        var text = """
                ROLE_ADMIN > ROLE_USER
                """;
        return RoleHierarchyImpl.fromHierarchy(text);
    }

    @Bean
    public SecurityFilterChain securityFilterChainDefault(HttpSecurity http, ExceptionHandlers exceptionHandlers) {
        return http
                .securityMatcher("/**")
                .anonymous(withDefaults())
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .jee(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .passwordManagement(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .headers(withDefaults())
                .authorizeHttpRequests(c ->
                        c.requestMatchers("/error").permitAll()
                                .requestMatchers(GET, "/actuator", "/actuator/info", "/actuator/health",
                                        "/actuator/beans", "/actuator/env", "/actuator/securityproviders").permitAll()
                                .requestMatchers("/actuator/shutdown", "/actuator/restart").denyAll()
                                .requestMatchers("/user/login").permitAll()
                                .requestMatchers("/user/test-jwt").authenticated()
                                .anyRequest().denyAll()
                )
                .exceptionHandling(c -> {
                    c.authenticationEntryPoint(exceptionHandlers)
                            .accessDeniedHandler(exceptionHandlers);
                })
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(Environment environment) {
        return customizer -> customizer.debug(RuntimeHelper.isDevProfileActive(environment));
    }

}
