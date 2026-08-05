package bayonet.test.security;

import com.github.yingzhuo.bayonet.jwt.service.JwtValidator;
import com.github.yingzhuo.bayonet.jwt.service.ValidatingResult;
import com.github.yingzhuo.bayonet.security.authentication.RichUserDetails;
import com.github.yingzhuo.bayonet.security.token.TokenConverter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class JwtTokenConverter implements TokenConverter {

    private final JwtValidator jwtValidator;

    @Override
    public @Nullable UserDetails convert(String token) throws AuthenticationException {
        var result = jwtValidator.validate(token);

        if (result.status() != ValidatingResult.Status.OK) {
            return null;
        }

        var decoded = result.descriptor();
        Assert.state(decoded != null, "failed to decode token");

        return RichUserDetails
                .builder()
                .username(decoded.getClaim("username").asString())
                .authoritiesFromCommaSplitString(decoded.getClaim("authorities").asString())
                .build();
    }

}
