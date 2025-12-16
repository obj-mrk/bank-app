package mrk.domain.model;

import mrk.domain.model.enums.TransactionType;
import mrk.domain.model.enums.TransactionState;
import mrk.common.errors.impl.InvalidTransactionException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final TransactionType type;

    private final UUID fromAccountId; // может быть null
    private final UUID toAccountId;   // может быть null

    private final Money amount;
    private final String description;
    private final UUID initiatedByUserId;

    private final String idempotencyKey;
    private final Instant createdAt;

    private TransactionState state;

    // ---------- Фабрики ----------
    public static Transaction createDeposit(
            UUID id,
            UUID toAccountId,
            Money amount,
            UUID initiatedBy,
            String idempotencyKey,
            Instant createdAt
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(toAccountId);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(initiatedBy);
        Objects.requireNonNull(createdAt);

        if (!amount.isPositive()) {
            throw new InvalidTransactionException("Deposit amount must be positive");
        }

        return new Transaction(
                id,
                TransactionType.DEPOSIT,
                null,
                toAccountId,
                amount,
                "Deposit",
                initiatedBy,
                idempotencyKey,
                createdAt,
                TransactionState.PENDING
        );
    }

    public static Transaction createWithdrawal(
            UUID id,
            UUID fromAccountId,
            Money amount,
            UUID initiatedBy,
            String idempotencyKey,
            Instant createdAt
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(fromAccountId);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(initiatedBy);

        if (!amount.isPositive()) {
            throw new InvalidTransactionException("Withdrawal amount must be positive");
        }

        return new Transaction(
                id,
                TransactionType.WITHDRAWAL,
                fromAccountId,
                null,
                amount,
                "Withdrawal",
                initiatedBy,
                idempotencyKey,
                createdAt,
                TransactionState.PENDING
        );
    }

    public static Transaction createTransfer(
            UUID id,
            UUID fromAccountId,
            UUID toAccountId,
            Money amount,
            UUID initiatedBy,
            String idempotencyKey,
            Instant createdAt
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(fromAccountId);
        Objects.requireNonNull(toAccountId);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(initiatedBy);
        Objects.requireNonNull(createdAt);

        if (!amount.isPositive()) {
            throw new InvalidTransactionException("Transfer amount must be positive");
        }

        return new Transaction(
                id,
                TransactionType.TRANSFER,
                fromAccountId,
                toAccountId,
                amount,
                "Transfer",
                initiatedBy,
                idempotencyKey,
                createdAt,
                TransactionState.PENDING
        );
    }

    public static Transaction createCommission(
            UUID id,
            UUID fromAccountId,
            Money amount,
            UUID initiatedBy,
            String idempotencyKey,
            Instant createdAt,
            String description
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(fromAccountId);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(initiatedBy);

        if (!amount.isPositive()) {
            throw new InvalidTransactionException("Commission amount must be positive");
        }

        return new Transaction(
                id,
                TransactionType.COMMISSION,
                fromAccountId,
                null,
                amount,
                description == null ? "Commission" : description,
                initiatedBy,
                idempotencyKey,
                createdAt,
                TransactionState.PENDING
        );
    }

    // ---------- Конструктор ----------
    public Transaction(
            UUID id,
            TransactionType type,
            UUID fromAccountId,
            UUID toAccountId,
            Money amount,
            String description,
            UUID initiatedByUserId,
            String idempotencyKey,
            Instant createdAt,
            TransactionState initialState
    ) {
        this.id = id;
        this.type = type;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
        this.initiatedByUserId = initiatedByUserId;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.state = initialState;

        validateInvariant();
    }

    /**
     * Инвариант согласованности:
     *  - DEPOSIT: from=null, to!=null
     *  - WITHDRAWAL: from!=null, to=null
     *  - TRANSFER: from!=null, to!=null
     *  - COMMISSION: from!=null, to=null
     *  - REVERSAL: будет реализовано позже
     */

    // ---------- Инварианты -----------
    private void validateInvariant() {

        switch (type) {
            case DEPOSIT -> {
                if (fromAccountId != null || toAccountId == null) {
                    throw new InvalidTransactionException("Deposit must have only 'toAccountId'");
                }
            }

            case WITHDRAWAL -> {
                if (fromAccountId == null || toAccountId != null) {
                    throw new InvalidTransactionException("Withdrawal must have only 'fromAccountId'");
                }
            }

            case TRANSFER -> {
                if (fromAccountId == null || toAccountId == null) {
                    throw new InvalidTransactionException("Transfer must have both accounts");
                }
            }

            case COMMISSION -> {
                if (fromAccountId == null || toAccountId != null) {
                    throw new InvalidTransactionException("Commission must have only 'fromAccountId'");
                }
            }

            case REVERSAL -> {
                // реализуем позже, когда введём связь relatedTxnId
            }
        }
    }

    // ---------- Смена состояния ----------
    public void markPosted() {
        if (state != TransactionState.PENDING) {
            throw new InvalidTransactionException("Only PENDING → POSTED allowed");
        }
        this.state = TransactionState.POSTED;
    }

    public void markRejected() {
        if (state != TransactionState.PENDING) {
            throw new InvalidTransactionException("Only PENDING → REJECTED allowed");
        }
        this.state = TransactionState.REJECTED;
    }

    public TransactionState getState() {
        return state;
    }

    // ---------- Геттеры ----------

    public UUID getId() {
        return id;
    }

    public UUID getFromAccountId() {
        return fromAccountId;
    }

    public UUID getToAccountId() {
        return toAccountId;
    }

    public Money getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public TransactionType getType() {
        return type;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public UUID getInitiatedByUserId() {
        return initiatedByUserId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
