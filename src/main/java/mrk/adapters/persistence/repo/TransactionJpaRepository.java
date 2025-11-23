package mrk.adapters.persistence.repo;

import mrk.adapters.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {
    Optional<TransactionEntity> findByIdempotencyKey(String idempotencyKey);
}
