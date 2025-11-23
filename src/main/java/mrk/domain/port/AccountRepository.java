package mrk.domain.port;

import mrk.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Optional<Account> findById(UUID id);

    Optional<Account> findByIdForUpdate(UUID id);

    Account save(Account account);
}
