package mrk.application.usecase;

import lombok.RequiredArgsConstructor;
import mrk.application.port.AccountNumberGenerator;
import mrk.application.port.ClockProvider;
import mrk.application.port.IdGenerator;
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

    private final IdGenerator idGenerator;
    private final AccountNumberGenerator numberGenerator;
    private final ClockProvider clock;

    @Transactional
    public Account execute(OpenAccountCommand cmd) {
        var user = userRepository.findById(cmd.userId())
                .orElseThrow(() -> new NotFoundException("User not found: " + cmd.userId()));

        if (!user.isActive()) {
            throw new InvalidUserException("Cannot open account for user in status " + user.getStatus());
        }

        var account = Account.createAccount(
                idGenerator.next(),
                cmd.userId(),
                cmd.type(),
                numberGenerator.next(),
                cmd.initialBalance(),
                cmd.creditLimit(),
                cmd.dailyLimit(),
                clock.now()
        );

        return accountRepository.save(account);
    }
}
