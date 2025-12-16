package mrk.application.service;

import lombok.RequiredArgsConstructor;
import mrk.domain.model.Transaction;
import mrk.domain.port.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IdempotencyService {
    private final TransactionRepository transactionRepository;

    public Optional<Transaction> findExisting(String key) {
        if (key == null || key.isBlank()) return Optional.empty();
        return transactionRepository.findByIdempotencyKey(key);
    }
}
