package mrk.persistence.adapter;

import lombok.RequiredArgsConstructor;
import mrk.persistence.entity.UserEntity;
import mrk.persistence.repo.UserJpaRepository;
import mrk.domain.model.User;
import mrk.domain.port.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(this::toDomain);
    }

    private User toDomain(UserEntity entity) {
        return User.rehydrate(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getName(),
                entity.getRole(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
