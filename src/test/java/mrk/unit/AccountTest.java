package mrk.unit;

import mrk.common.errors.impl.CreditLimitExceededException;
import mrk.common.errors.impl.InsufficientFundsException;
import mrk.domain.model.Account;
import mrk.domain.model.Money;
import mrk.domain.model.enums.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account newChecking(BigDecimal balance) {
        return Account.createAccount(
                UUID.randomUUID(),
                UUID.randomUUID(),
                AccountType.CHECKING,
                "ACC-1",
                Money.of(balance, CurrencyType.RUB),
                Money.zero(CurrencyType.RUB),
                Money.of(new BigDecimal("500000"), CurrencyType.RUB),
                Instant.now()
        );
    }

    private Account newCredit(BigDecimal balance, BigDecimal creditLimit) {
        return Account.createAccount(
                UUID.randomUUID(),
                UUID.randomUUID(),
                AccountType.CREDIT,
                "ACC-CR",
                Money.of(balance, CurrencyType.RUB),
                Money.of(creditLimit, CurrencyType.RUB),
                Money.of(new BigDecimal("500000"), CurrencyType.RUB),
                Instant.now()
        );
    }

    @Test
    void withdrawFromChecking_cannotGoNegative() {
        Account acc = newChecking(new BigDecimal("100.00"));
        assertThrows(InsufficientFundsException.class,
                () -> acc.withdraw(Money.of(new BigDecimal("150.00"), CurrencyType.RUB)));
    }

    @Test
    void creditAccount_cannotExceedLimit() {
        Account acc = newCredit(BigDecimal.ZERO, new BigDecimal("1000.00"));
        // Лимит: -1000.00
        acc.withdraw(Money.of(new BigDecimal("500.00"), CurrencyType.RUB)); // OK, баланс -500

        assertThrows(CreditLimitExceededException.class,
                () -> acc.withdraw(Money.of(new BigDecimal("600.00"), CurrencyType.RUB))); // ушли бы ниже -1000
    }
}
