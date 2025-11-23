package mrk.domain.port;

import mrk.domain.model.Transaction;

import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}
