package bayonet.test.security;

import com.github.yingzhuo.bayonet.security.authentication.RichUserDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PredefinedUsers {

    public static List<UserDetails> createDefaults() {
        return List.of(
                RichUserDetails.builder()
                        .id("00000000000000000000000000000000")
                        .username("admin")
                        .password("{noop}admin")
                        .roles("ADMIN", "USER")
                        .build(),
                RichUserDetails.builder()
                        .id("00000000000000000000000000000001")
                        .username("yingzhuo")
                        .password("{noop}yingzhuo")
                        .roles("USER")
                        .build()
        );
    }
}
