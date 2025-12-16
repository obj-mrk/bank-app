package mrk.adapters.web.security.auth;

import lombok.RequiredArgsConstructor;
import mrk.persistence.entity.UserEntity;
import mrk.common.errors.impl.ValidationException;
import mrk.domain.model.enums.UserRole;
import mrk.persistence.repo.UserJpaRepository;
import mrk.adapters.web.security.jwt.JwtService;
import mrk.adapters.web.security.user.CustomUserDetails;
import mrk.adapters.web.security.user.dto.AuthResponse;
import mrk.adapters.web.security.user.dto.LoginRequest;
import mrk.adapters.web.security.user.dto.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userJpaRepository.existsByEmail(request.email())) {
            throw new ValidationException("Email already registered");
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setId(UUID.randomUUID());

        userEntity.setEmail(request.email());
        userEntity.setPassword(passwordEncoder.encode(request.password()));
        userEntity.setName(request.name());
        userEntity.setPhone(request.phone());
        userEntity.setAddress(request.address());
        userEntity.setRole(UserRole.USER);
        userJpaRepository.save(userEntity);

        String token = jwtService.generateToken(new CustomUserDetails(userEntity.getId(), userEntity.getEmail(), userEntity.getPassword(), userEntity.getRole()));
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserEntity userEntity = userJpaRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(new CustomUserDetails(userEntity.getId(), userEntity.getEmail(), userEntity.getPassword(), userEntity.getRole()));
        return new AuthResponse(token);
    }
}
