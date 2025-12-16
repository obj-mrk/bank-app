package mrk.adapters.web.security.user;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public final class AuthUserIdExtractor {
    private  AuthUserIdExtractor() {}

    public static UUID userId(Authentication authentication) {
        var principal = (CustomUserDetails) authentication.getPrincipal();
        return principal.getId();
    }
}
