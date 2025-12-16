package mrk.application.usecase;

import lombok.RequiredArgsConstructor;
import mrk.application.port.ClockProvider;
import mrk.application.port.IdGenerator;
import mrk.application.service.IdempotencyService;
import mrk.application.usecase.command.TransferCommand;
import mrk.common.errors.impl.NotFoundException;
import mrk.domain.model.Transaction;
import mrk.domain.port.AccountRepository;
import mrk.domain.port.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferUseCase {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final IdempotencyService idempotencyService;
    private final IdGenerator idGenerator;
    private final ClockProvider clock;

    @Transactional
    public Transaction execute(TransferCommand cmd) {
        // Идемпотентность (если ключ передан)
        var existing = idempotencyService.findExisting(cmd.idempotencyKey());
        if (existing.isPresent()) return existing.get();

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
                idGenerator.next(),
                from.getId(),
                to.getId(),
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

