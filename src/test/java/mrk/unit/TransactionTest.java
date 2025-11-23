package mrk.unit;

import mrk.common.errors.impl.InvalidTransactionException;
import mrk.domain.model.Money;
import mrk.domain.model.Transaction;
import mrk.domain.model.enums.CurrencyType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void deposit_mustHaveOnlyToAccount() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Money amount = Money.of(new BigDecimal("10.00"), CurrencyType.RUB);

        Transaction t = Transaction.createDeposit(
                id, accountId, amount,
                UUID.randomUUID(), "key", Instant.now()
        );

        assertNull(t.getFromAccountId());
        assertEquals(accountId, t.getToAccountId());
    }

    @Test
    void invalidTransfer_withoutFrom_throws() {
        UUID id = UUID.randomUUID();
        Money amount = Money.of(new BigDecimal("10.00"), CurrencyType.RUB);

        assertThrows(InvalidTransactionException.class, () ->
                new Transaction(
                        id,
                        mrk.domain.model.enums.TransactionType.TRANSFER,
                        null,
                        UUID.randomUUID(),
                        amount,
                        "Transfer",
                        UUID.randomUUID(),
                        "key",
                        Instant.now(),
                        mrk.domain.model.enums.TransactionState.PENDING
                )
        );
    }
}
