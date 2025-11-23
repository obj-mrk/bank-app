package mrk.adapters.web.dto;

import mrk.domain.model.Money;
import mrk.domain.model.enums.CurrencyType;
import mrk.domain.model.enums.TransactionState;
import mrk.domain.model.enums.TransactionType;
import mrk.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponseDto(
        UUID id,
        TransactionType type,
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        CurrencyType currency,
        TransactionState state,
        Instant createdAt
) {

    public static TransactionResponseDto fromDomain(Transaction txn) {
        Money money = txn.getAmount();
        return new TransactionResponseDto(
                txn.getId(),
                txn.getType(),
                txn.getFromAccountId(),
                txn.getToAccountId(),
                money.getAmount(),
                money.getCurrency(),
                txn.getState(),
                txn.getCreatedAt()
        );
    }
}
