package mrk.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mrk.domain.model.enums.TransactionState;
import mrk.domain.model.enums.TransactionType;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_txn_idempotency", columnList = "idempotency_key", unique = true)
})
@Getter
@Setter
public class TransactionEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private AccountEntity fromAccountEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private AccountEntity toAccountEntity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 3)
    private String currency = "RUB";

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by")
    private UserEntity initiatedBy;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TransactionState state = TransactionState.POSTED;

    @ManyToOne
    @JoinColumn(name = "related_txn_id")
    private TransactionEntity relatedTransactionEntity;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
