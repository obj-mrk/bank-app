package mrk.application.usecase;

import lombok.RequiredArgsConstructor;
import mrk.application.usecase.command.TransferCommand;
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
public class TransferUseCase {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction execute(TransferCommand cmd) {

        if (cmd.idempotencyKey() != null && !cmd.idempotencyKey().isBlank()) {
            var existing = transactionRepository.findByIdempotencyKey(cmd.idempotencyKey());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        // Загружаем счета в фиксированном порядке, чтобы избежать дедлоков
        var firstId = cmd.fromAccountId().compareTo(cmd.toAccountId()) < 0
                ? cmd.fromAccountId()
                : cmd.toAccountId();

        var secondId = firstId.equals(cmd.fromAccountId())
                ? cmd.toAccountId()
                : cmd.fromAccountId();

        var first = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new NotFoundException("Account not found: " + firstId));
        var second = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new NotFoundException("Account not found: " + secondId));

        // Определяем, кто из них from/to после загрузки
        var from = first.getId().equals(cmd.fromAccountId()) ? first : second;
        var to = first.getId().equals(cmd.toAccountId()) ? first : second;

        // Доменная логика по переводам
        from.withdraw(cmd.amount());
        to.deposit(cmd.amount());

        accountRepository.save(from);
        accountRepository.save(to);

        var txn = Transaction.createTransfer(
                UUID.randomUUID(),
                from.getId(),
                to.getId(),
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

