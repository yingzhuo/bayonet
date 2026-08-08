package bayonet.test.tool;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RuntimeHelper {

    public static boolean isDevProfileActive(Environment environment) {
        return environment.acceptsProfiles(Profiles.of("dev"));
    }

    public static boolean isProdProfileActive(Environment environment) {
        return environment.acceptsProfiles(Profiles.of("prod"));
    }

}
