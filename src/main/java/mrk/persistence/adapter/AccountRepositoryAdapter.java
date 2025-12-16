package mrk.persistence.adapter;

import lombok.RequiredArgsConstructor;
import mrk.persistence.entity.AccountEntity;
import mrk.persistence.entity.UserEntity;
import mrk.persistence.mapper.AccountMapper;
import mrk.persistence.repo.AccountJpaRepository;
import mrk.persistence.repo.UserJpaRepository;
import mrk.domain.model.Account;
import mrk.domain.port.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<Account> findById(UUID id) {
        return accountJpaRepository.findById(id)
                .map(AccountMapper::toDomain);
    }

    @Override
    public Optional<Account> findByIdForUpdate(UUID id) {
        return accountJpaRepository.findByIdForUpdate(id)
                .map(AccountMapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        // Загружаем UserEntity для связи
        UserEntity userEntity = userJpaRepository.findById(account.getUserId())
                .orElseThrow(() -> new IllegalStateException("User entity not found for account"));

        AccountEntity entity = accountJpaRepository.findById(account.getId())
                .orElse(null);

        entity = AccountMapper.toEntity(account, entity, userEntity);

        AccountEntity saved = accountJpaRepository.save(entity);

        return AccountMapper.toDomain(saved);
    }
}
