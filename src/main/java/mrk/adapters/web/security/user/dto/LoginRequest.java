package mrk.adapters.web.security.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String email,
                           @NotBlank String password) {
}
