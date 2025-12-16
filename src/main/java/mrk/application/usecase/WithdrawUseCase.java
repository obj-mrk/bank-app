package mrk.application.usecase;

import lombok.RequiredArgsConstructor;
import mrk.application.port.ClockProvider;
import mrk.application.port.IdGenerator;
import mrk.application.service.IdempotencyService;
import mrk.application.usecase.command.WithdrawCommand;
import mrk.common.errors.impl.NotFoundException;
import mrk.domain.model.Transaction;
import mrk.domain.port.AccountRepository;
import mrk.domain.port.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawUseCase {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final IdempotencyService idempotencyService;
    private final IdGenerator idGenerator;
    private final ClockProvider clock;

    @Transactional
    public Transaction execute(WithdrawCommand cmd) {
        // Идемпотентность (если ключ передан)
        var existing = idempotencyService.findExisting(cmd.idempotencyKey());
        if (existing.isPresent()) return existing.get();

        var account = accountRepository.findByIdForUpdate(cmd.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + cmd.accountId()));

        account.withdraw(cmd.amount());

        accountRepository.save(account);

        var txn = Transaction.createWithdrawal(
                idGenerator.next(),
                account.getId(),
                cmd.amount(),
                cmd.initiatedByUserId(),
                cmd.idempotencyKey(),
                clock.now()
        );

        txn.markPosted();
        transactionRepository.save(txn);

        return txn;
    }
}

