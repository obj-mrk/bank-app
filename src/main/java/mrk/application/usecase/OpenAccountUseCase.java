package mrk.application.usecase;

import lombok.RequiredArgsConstructor;
import mrk.application.usecase.command.OpenAccountCommand;
import mrk.common.errors.impl.InvalidUserException;
import mrk.common.errors.impl.NotFoundException;
import mrk.domain.model.Account;
import mrk.domain.port.AccountRepository;
import mrk.domain.port.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenAccountUseCase {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Account execute(OpenAccountCommand cmd) {
        var user = userRepository.findById(cmd.userId())
                .orElseThrow(() -> new NotFoundException("User not found: " + cmd.userId()));

        if (!user.isActive()) {
            throw new InvalidUserException("Cannot open account for user in status " + user.getStatus());
        }

        var account = Account.createAccount(
                cmd.accountId(),
                cmd.userId(),
                cmd.type(),
                cmd.number(),
                cmd.initialBalance(),
                cmd.creditLimit(),
                cmd.dailyLimit(),
                cmd.createdAt()
        );

        return accountRepository.save(account);
    }
}
