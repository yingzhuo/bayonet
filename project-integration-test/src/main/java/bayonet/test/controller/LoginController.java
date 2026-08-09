package bayonet.test.controller;

import com.github.yingzhuo.bayonet.jwt.service.JwtCreator;
import com.github.yingzhuo.bayonet.jwt.service.JwtData;
import com.github.yingzhuo.bayonet.security.annotation.CurrentUserDetails;
import com.github.yingzhuo.bayonet.utility.UUIDUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class LoginController {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtCreator jwtCreator;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) {
        var username = loginRequest.getUsername();
        var password = loginRequest.getPassword();

        Assert.hasText(username, "username must not be blank");
        Assert.hasText(password, "password must not be blank");

        var user = userDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("bad credentials");
        }

        var data = JwtData.newInstance()
                .addPayload("username", username)
                .addPayload("authorities",
                        user.getAuthorities()
                                .stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(","))
                )
                .addPayload("nonce", UUIDUtils.versionFourLong())
                .addPayloadIssuedAtNow();
        var token = jwtCreator.create(data);

        return Map.of("jwt", token);
    }

    @GetMapping("/test-jwt")
    public Map<String, Object> testJwt(
            @CurrentUserDetails UserDetails user,
            Authentication authentication
    ) {
        log.debug("user: {}", user);
        log.debug("authentication: {}", authentication);
        return Map.of();
    }

    // -------

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

}
