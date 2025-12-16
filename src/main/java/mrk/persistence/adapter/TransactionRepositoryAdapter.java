package mrk.persistence.adapter;

import lombok.RequiredArgsConstructor;
import mrk.persistence.entity.AccountEntity;
import mrk.persistence.entity.TransactionEntity;
import mrk.persistence.entity.UserEntity;
import mrk.persistence.mapper.TransactionMapper;
import mrk.persistence.repo.AccountJpaRepository;
import mrk.persistence.repo.TransactionJpaRepository;
import mrk.persistence.repo.UserJpaRepository;
import mrk.domain.model.Transaction;
import mrk.domain.port.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository transactionJpaRepository;
    private final AccountJpaRepository accountJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public Transaction save(Transaction txn) {

        AccountEntity from = null;
        if (txn.getFromAccountId() != null) {
            from = accountJpaRepository.findById(txn.getFromAccountId())
                    .orElseThrow(() -> new IllegalStateException("From account not found"));
        }

        AccountEntity to = null;
        if (txn.getToAccountId() != null) {
            to = accountJpaRepository.findById(txn.getToAccountId())
                    .orElseThrow(() -> new IllegalStateException("To account not found"));
        }

        UserEntity user = null;
        if (txn.getInitiatedByUserId() != null) {
            user = userJpaRepository.findById(txn.getInitiatedByUserId())
                    .orElseThrow(() -> new IllegalStateException("User not found for transaction"));
        }

        TransactionEntity entity = transactionJpaRepository.findById(txn.getId())
                .orElse(null);

        entity = TransactionMapper.toEntity(txn, entity, from, to, user);

        TransactionEntity saved = transactionJpaRepository.save(entity);

        return TransactionMapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findByIdempotencyKey(String idempotencyKey) {
        return transactionJpaRepository.findByIdempotencyKey(idempotencyKey)
                .map(TransactionMapper::toDomain);
    }
}
