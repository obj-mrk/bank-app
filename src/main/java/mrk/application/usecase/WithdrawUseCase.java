package mrk.application.usecase;

import lombok.RequiredArgsConstructor;
import mrk.application.usecase.command.WithdrawCommand;
import mrk.common.errors.impl.NotFoundException;
import mrk.domain.model.Transaction;
import mrk.domain.port.AccountRepository;
import mrk.domain.port.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WithdrawUseCase {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction execute(WithdrawCommand cmd) {
        if (cmd.idempotencyKey() != null && !cmd.idempotencyKey().isBlank()) {
            var existing = transactionRepository.findByIdempotencyKey(cmd.idempotencyKey());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        var account = accountRepository.findByIdForUpdate(cmd.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + cmd.accountId()));

        account.withdraw(cmd.amount());

        accountRepository.save(account);

        var txn = Transaction.createWithdrawal(
                UUID.randomUUID(),
                account.getId(),
                cmd.amount(),
                cmd.initiatedByUserId(),
                cmd.idempotencyKey(),
                Instant.now()
        );

        txn.markPosted();
        transactionRepository.save(txn);

        return txn;
    }
}

